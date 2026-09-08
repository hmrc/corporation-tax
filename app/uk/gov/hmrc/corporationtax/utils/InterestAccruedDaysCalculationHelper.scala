package uk.gov.hmrc.corporationtax.utils

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.BusinessConstants.{APPEND_DUE_DATE_MONTHS, LATE_PAYMENT_INTEREST}
import uk.gov.hmrc.corporationtax.models.{InterestAccrualList, InterestAccrualListWithInterestAccruedDays, InterestAccrualWithInterestAccruedDays, MissingDataError, MissingStatueRule}
import uk.gov.hmrc.corporationtax.services.StatuteRuleService
import uk.gov.hmrc.http.HeaderCarrier

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InterestAccruedDaysCalculationHelper @Inject() (
  statuteRuleService: StatuteRuleService
)(implicit ec:ExecutionContext) extends Logging {

  def getInterestAccrualListWithInterestAccruedDays(
    interestAccrualList: InterestAccrualList,
    taxRef: Long,
    accPeriod: Long,
    interestType: String
  )(implicit hc: HeaderCarrier): InterestAccrualListWithInterestAccruedDays =
    interestType match {
      case LATE_PAYMENT_INTEREST =>
        getChargeableDaysForNonIDEInterestTypes(interestAccrualList: InterestAccrualList)
      case _                     => getChargeableDaysForIDE(interestAccrualList: InterestAccrualList, taxRef: Long, accPeriod: Long)
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
    interestAccrualList: InterestAccrualList,
    taxRef: Long,
    accPeriod: Long
  )(implicit hc: HeaderCarrier): InterestAccrualListWithInterestAccruedDays =
    InterestAccrualListWithInterestAccruedDays(
      interestAccruals = interestAccrualList.interestAccruals.map { value =>
        InterestAccrualWithInterestAccruedDays(
          computationAmount = value.computationAmount,
          interestAccrualFromDate = value.interestAccrualFromDate,
          interestAccrualToDate = value.interestAccrualToDate,
          interestRate = value.interestRate,
          interestAmount = value.interestAmount,
          apEndDate = value.apEndDate,
          noOfDays =
            calculateChargeableDaysForIDE(value.apEndDate) 
        )
      }
    )

  private def calculateChargeableDaysForIDE(
    apEndDate: LocalDate
  )(implicit hc:HeaderCarrier): Future[Either[MissingDataError, Long]] =
    for {
      configuredValueForMonths <- statuteRuleService.getStatueRule(APPEND_DUE_DATE_MONTHS,apEndDate, apEndDate)
        .map(_.toRight(MissingStatueRule("Cannot find statueRule for")))
    } yield Future.successful(Right(12L))

}
