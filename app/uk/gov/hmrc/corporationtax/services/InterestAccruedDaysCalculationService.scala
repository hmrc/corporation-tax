/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.corporationtax.services

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.BusinessConstants.{
  APPEND_DUE_DATE_DAYS, APPEND_DUE_DATE_MONTHS, LATE_PAYMENT_INTEREST
}
import uk.gov.hmrc.corporationtax.models.{
  InterestAccrualList, InterestAccrualListWithInterestAccruedDays, InterestAccrualWithInterestAccruedDays,
  MissingDataError, MissingStatueRule
}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InterestAccruedDaysCalculationService @Inject() (
  statuteRuleService: StatuteRuleService
)(implicit ec: ExecutionContext)
    extends Logging {

  def getInterestAccrualListWithInterestAccruedDays(
    interestAccrualList: InterestAccrualList,
    taxRef: Long,
    accPeriod: Long,
    interestType: String
  )(implicit hc: HeaderCarrier): Future[Either[MissingDataError, InterestAccrualListWithInterestAccruedDays]] =
    interestType match {
      case LATE_PAYMENT_INTEREST =>
        logger.info("Calculating number of days for interestAccrued for IDE InterestType")
        getChargeableDaysForIDE(interestAccrualList: InterestAccrualList)
      case _                     =>
        logger.info("Calculating number of days for interestAccrued for NON-IDE InterestType")
        Future.successful(Right(getChargeableDaysForNonIDEInterestTypes(interestAccrualList: InterestAccrualList)))
    }

  private def getChargeableDaysForNonIDEInterestTypes(
    interestAccrualList: InterestAccrualList
  ): InterestAccrualListWithInterestAccruedDays =
    InterestAccrualListWithInterestAccruedDays(
      interestAccruals = interestAccrualList.interestAccruals.map { value =>
        InterestAccrualWithInterestAccruedDays(
          computationAmount = value.computationAmount,
          interestAccrualFromDate = value.interestAccrualFromDate,
          interestAccrualToDate = value.interestAccrualToDate,
          interestRate = value.interestRate,
          interestAmount = value.interestAmount,
          apEndDate = value.apEndDate,
          noOfDays = calculateChargeableDays(value.interestAccrualFromDate, value.interestAccrualToDate)
        )
      }
    )

  private def calculateChargeableDays(fromDate: LocalDate, toDate: LocalDate): Long =
    ChronoUnit.DAYS.between(fromDate, toDate) + 1L

  private def getChargeableDaysForIDE(
    interestAccrualList: InterestAccrualList
  )(implicit hc: HeaderCarrier): Future[Either[MissingDataError, InterestAccrualListWithInterestAccruedDays]] =
    Future
      .sequence(
        interestAccrualList.interestAccruals.map { value =>
          calculateChargeableDaysForIDE(
            value.apEndDate,
            value.interestAccrualToDate,
            value.interestAccrualFromDate
          ).map {
            case Left(error) =>
              logger.error(
                s"Cannot calculate number of days of interestAccrued, GET StatueRule returned invalid response due to:${error.message} "
              )
              Left(error)
            case Right(days)                   =>
              Right(
                InterestAccrualWithInterestAccruedDays(
                  computationAmount = value.computationAmount,
                  interestAccrualFromDate = value.interestAccrualFromDate,
                  interestAccrualToDate = value.interestAccrualToDate,
                  interestRate = value.interestRate,
                  interestAmount = value.interestAmount,
                  apEndDate = value.apEndDate,
                  noOfDays = days
                )
              )
          }

        }
      )
      .map { results =>
        results
          .collectFirst { case Left(error) =>
            logger.error(s"Cannot retrieve the statue rule due to ${error.message}")
            Left(error)
          }
          .getOrElse {
            Right(
              InterestAccrualListWithInterestAccruedDays(
                interestAccruals = results.collect { case Right(value) =>
                  value
                }
              )
            )
          }
      }

  private def calculateChargeableDaysForIDE(
    apEndDate: LocalDate,
    toDate: LocalDate,
    fromDate: LocalDate
  )(implicit hc: HeaderCarrier): Future[Either[MissingDataError, Long]] =
    for {
      configuredValueForMonthsResponse <-
        statuteRuleService
          .getStatueRule(APPEND_DUE_DATE_MONTHS, apEndDate, apEndDate)
          .map(_.toRight(MissingStatueRule(s"Cannot find statue rule for ruleRateKey:$APPEND_DUE_DATE_MONTHS")))
      configuredValueForDaysResponse   <-
        statuteRuleService
          .getStatueRule(APPEND_DUE_DATE_DAYS, apEndDate, apEndDate)
          .map(_.toRight(MissingStatueRule(s"Cannot find statue rule for ruleRateKey:$APPEND_DUE_DATE_DAYS")))
    } yield for {
      monthsResponse <- configuredValueForMonthsResponse
      daysResponse   <- configuredValueForDaysResponse
    } yield {

      val normalDueDate = apEndDate
        .plusMonths(monthsResponse.statuteRule.numberOfDays.toLong)
        .plusDays(daysResponse.statuteRule.numberOfDays.toLong)

      if (fromDate == normalDueDate) {
        ChronoUnit.DAYS.between(fromDate, toDate)
      } else {
        calculateChargeableDays(fromDate, toDate)
      }

    }

}
