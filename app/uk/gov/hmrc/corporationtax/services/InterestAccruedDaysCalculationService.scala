package uk.gov.hmrc.corporationtax.services

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.BusinessConstants.{APPEND_DUE_DATE_DAYS, APPEND_DUE_DATE_MONTHS, LATE_PAYMENT_INTEREST}
import uk.gov.hmrc.corporationtax.models.{InterestAccrualList, InterestAccrualListWithInterestAccruedDays, InterestAccrualWithInterestAccruedDays, MissingDataError, MissingStatueRule}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InterestAccruedDaysCalculationService @Inject()(
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
        Future.successful(Right(getChargeableDaysForNonIDEInterestTypes(interestAccrualList: InterestAccrualList)))
      case _                     => getChargeableDaysForIDE(interestAccrualList: InterestAccrualList)
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
          noOfDays = calculateChargeableDaysForNonIDE(value.interestAccrualFromDate, value.interestAccrualToDate)
        )
      }
    )

  private def calculateChargeableDaysForNonIDE(fromDate: LocalDate, toDate: LocalDate): Long =
    ChronoUnit.DAYS.between(toDate, fromDate) + 1L

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
            case Left(error) => Left(error)
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
            logger.error(s"Cannot find the statue rule${error.message}")
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
      configuredValueForMonthsResponse <- statuteRuleService
                                            .getStatueRule(APPEND_DUE_DATE_MONTHS, apEndDate, apEndDate)
                                            .map(_.toRight(MissingStatueRule("Cannot find statue rule for ")))
      configuredValueForDaysResponse   <- statuteRuleService
                                            .getStatueRule(APPEND_DUE_DATE_DAYS, apEndDate, apEndDate)
                                            .map(_.toRight(MissingStatueRule("Cannot find data")))
    } yield for {
      monthsResponse <- configuredValueForMonthsResponse
      daysResponse   <- configuredValueForDaysResponse
    } yield {
      val normalDueDate = apEndDate
        .plusMonths(monthsResponse.statuteRule.numberOfDays.toLong)
        .plusDays(daysResponse.statuteRule.numberOfDays.toLong)

      if (fromDate == normalDueDate) ChronoUnit.DAYS.between(toDate, fromDate)
      else ChronoUnit.DAYS.between(toDate, fromDate) + 1L

    }

}
