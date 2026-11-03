package uk.gov.hmrc.corporationtax.services.payments

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.BusinessConstants.*
import uk.gov.hmrc.corporationtax.models.InterestCharges
import uk.gov.hmrc.corporationtax.models.payments.{AllAccountingPeriods, PaymentPostingRow, PaymentPostingsResponse, PaymentsOnAccount, PaymentsOnAccountRow}
import uk.gov.hmrc.corporationtax.services.InterestChargeService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class PaymentsOnAccountService @Inject (
  paymentPostingService: PaymentPostingsService,
  postingSummaryByType: PostingSummaryByTypeService,
  interestChargeService: InterestChargeService,
  allAccountingPeriodsService: AllAccountingPeriodsService
)(implicit ec: ExecutionContext)
    extends Logging {

  def getPaymentsOnAccount(taxReference: Long)(implicit
    hc: HeaderCarrier
  ): Future[PaymentsOnAccount] =
    for {
      paymentPostings       <- paymentPostingService.getPaymentPostings(taxReference)
      rfrPostingSummary     <- postingSummaryByType.getPostingSummaryByType(taxReference, rfrPostingType)
      repPostingSummary     <- postingSummaryByType.getPostingSummaryByType(taxReference, repPostingType)
      interestChargeSummary <- interestChargeService.getInterestChargesSummary(taxReference)
      allAccountingPeriod   <- allAccountingPeriodsService.getAccountingPeriodList(taxReference)
    } yield ???

  private def computePaymentsOnAccount(paymentPostings: PaymentPostingsResponse, buffer: ListBuffer[PaymentsOnAccountRow] = ListBuffer.empty): PaymentsOnAccount =
    paymentPostings.paymentPostingResults match {
      case Nil => ???
      case head :: tail =>
        if checkIfAccPeriodEqualsZero(head.accountingPeriod) then tail.headOption match {
          case Some(nextRecord) =>
            val tempPaymentsOnAccountRow = tryCombineCurrentPostingToNextPosting(head, nextRecord)
          case None => ???
        }
        else ???


    }

  private def checkIfAccPeriodEqualsZero(accPeriod: Long): Boolean =
    accPeriod == zeroAccPeriod


  private def tryCombineCurrentPostingToNextPosting(currentPosting:PaymentPostingRow, nextPosting:PaymentPostingRow):ListBuffer[] = {

  }

}
