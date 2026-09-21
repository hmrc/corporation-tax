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
  MissingStatuteRule
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
  )(implicit hc: HeaderCarrier): Future[Either[MissingStatuteRule, InterestAccrualListWithInterestAccruedDays]] =
    interestType match {
      case LATE_PAYMENT_INTEREST =>
        logger.info(s"Calculating number of days for interestAccrued for interestType: $interestType")
        getChargeableDaysForIDE(interestAccrualList: InterestAccrualList)
      case _                     =>
        logger.info(s"Calculating number of days for interestAccrued for InterestType :$interestType")
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

  private def calculateChargeableDays(fromDate: LocalDate, toDate: LocalDate): Int =
    (ChronoUnit.DAYS.between(fromDate, toDate) + 1L).toInt

  private def getChargeableDaysForIDE(
    interestAccrualList: InterestAccrualList
  )(implicit hc: HeaderCarrier): Future[Either[MissingStatuteRule, InterestAccrualListWithInterestAccruedDays]] =
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
                s"Cannot calculate number of days of interestAccrued, GET StatuteRule returned invalid response due to:${error.message} "
              )
              Left(error)
            case Right(days) =>
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
            logger.error(s"Cannot retrieve the statute rule due to ${error.message}")
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
  )(implicit hc: HeaderCarrier): Future[Either[MissingStatuteRule, Int]] =

    val configuredValueForMonthsResponse =
      statuteRuleService
        .getStatuteRule(APPEND_DUE_DATE_MONTHS, apEndDate, apEndDate)
        .map(_.toRight(MissingStatuteRule(s"Cannot find statute rule for ruleRateKey:$APPEND_DUE_DATE_MONTHS")))
    val configuredValueForDaysResponse   =
      statuteRuleService
        .getStatuteRule(APPEND_DUE_DATE_DAYS, apEndDate, apEndDate)
        .map(_.toRight(MissingStatuteRule(s"Cannot find statute rule for ruleRateKey:$APPEND_DUE_DATE_DAYS")))
    for {
      monthsResponse <- configuredValueForMonthsResponse
      daysResponse   <- configuredValueForDaysResponse
    } yield for {
      monthsResponseOutput <- monthsResponse
      daysResponseOutput   <- daysResponse
    } yield {
      val normalDueDate = apEndDate
        .plusMonths(monthsResponseOutput.statuteRule.numberOfDays.toLong)
        .plusDays(daysResponseOutput.statuteRule.numberOfDays.toLong)

      if (fromDate == normalDueDate) {
        ChronoUnit.DAYS.between(fromDate, toDate).toInt
      } else {
        calculateChargeableDays(fromDate, toDate)
      }
    }

}
