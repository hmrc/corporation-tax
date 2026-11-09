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

package uk.gov.hmrc.corporationtax.services.payments

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.BusinessConstants.*
import uk.gov.hmrc.corporationtax.models.InterestCharges
import uk.gov.hmrc.corporationtax.models.payments.{AllAccountingPeriods, PaymentPostingRow, PaymentPostingsResponse, PaymentsOnAccount, PaymentsOnAccountRow}
import uk.gov.hmrc.corporationtax.services.InterestChargeService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.annotation.tailrec
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

  private def computeNewPaymentsOnAccount(paymentPostings: PaymentPostingsResponse): PaymentsOnAccount = {
    @tailrec
    def evaluateEachPaymentsOnAccount(remainingList: PaymentPostingsResponse, acc: PaymentsOnAccount): PaymentsOnAccount =
      remainingList.paymentPostingResults match {
        case Nil  => acc
        case head :: tail  => if checkIfAccPeriodEqualsZero(head) then
          if(tail == Nil) {
            evaluateEachPaymentsOnAccount(tail, acc ++ head)
          }
          else {
            tryCombineCurrentPostingToNextPosting(head, tail.head)
          }
        else {
          ???
        }
      }
    evaluateEachPaymentsOnAccount(paymentPostings, PaymentsOnAccount(List.empty))
  }



  private def checkIfAccPeriodEqualsZero(accPeriod: Long): Boolean =
    accPeriod == zeroAccPeriod


  private def tryCombineCurrentPostingToNextPosting(currentPosting:PaymentPostingRow, nextPosting:PaymentPostingRow, acc:ListBuffer[PaymentsOnAccount]):PaymentPostingRow = {
    if((currentPosting.accountingPeriod == nextPosting.accountingPeriod) && (currentPosting.payslipId == nextPosting.payslipId)){
      val combinedPosting = nextPosting.copy(postingAmount = nextPosting.postingAmount + currentPosting.postingAmount)
    }
    else {
      
    }

  }

}
