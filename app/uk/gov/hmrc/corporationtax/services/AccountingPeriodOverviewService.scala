package uk.gov.hmrc.corporationtax.services

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.{AccountingPeriodDetailsResponse, AccountingPeriodOverview, AccountingPeriodsRowResponse, MissingAccountingPeriodError}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodOverviewService @Inject() (
  accPeriodService: AccountingPeriodService,
  accPeriodDetailsService: AccountingPeriodDetailsService
)(implicit ec: ExecutionContext)
    extends Logging {

  def getAccountingPeriodOverview(taxRef: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[Either[MissingAccountingPeriodError, AccountingPeriodOverview]] =
    accPeriodService.getAccountingPeriod(taxRef, accPeriod).map {
      case Right(accPeriodResponse)                    => ???
      case Left(error: MissingAccountingPeriodError) => ???
      case Left(_)                                   => ???
    }

  private def findClericalFlag(accPeriodRowResponse: AccountingPeriodsRowResponse): Boolean =
    accPeriodRowResponse.clericalIntSig || accPeriodRowResponse.creditDebitInterestInd
    
  private def getAccountingPeriodOverviewResponse(taxRef:Long, accPeriod:Long, accPeriodRowResponse: AccountingPeriodsRowResponse)(implicit hc: HeaderCarrier):Future[AccountingPeriodOverview]= {
    if(accPeriodRowResponse.taxChargePresent) {
      for {
        accPeriodDetails <- accPeriodDetailsService.getAccountingDetails(taxRef, accPeriod)
      } yield {
        Right(AccountingPeriodOverview(
          accountingPeriod = accPeriodRowResponse.accountingPeriod,
          apStartDate = accPeriodRowResponse.apStartDate,
          apEndDate = accPeriodRowResponse.apEndDate,
          apStatus = accPeriodRowResponse.apStatus,
          taxChargePresent = accPeriodRowResponse.taxChargePresent,
          clericalIntSig = accPeriodRowResponse.clericalIntSig,
          creditDebitInterestInd = accPeriodRowResponse.creditDebitInterestInd,
          taxTotal = accPeriodRowResponse.taxTotal,
          interestTotal = accPeriodRowResponse.interestTotal,
          penaltyTotal = accPeriodRowResponse.penaltyTotal,
          payslipTotal = accPeriodRowResponse.payslipTotal,
          repayReallocTotal = accPeriodRowResponse.repayReallocTotal,
          adjustmentTotal = accPeriodRowResponse.adjustmentTotal,
          clericalCalculationFlag = findClericalFlag(accPeriodRowResponse)
        )
        )
      }
    }
      
    else {
      accPeriodRowResponse.interestTotal
    }
  }
}
