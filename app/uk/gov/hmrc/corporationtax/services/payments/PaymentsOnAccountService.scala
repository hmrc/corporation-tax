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
import uk.gov.hmrc.corporationtax.models.payments.{
  AccountingPeriodList, AllAccountingPeriods, PaymentPostingRow, PaymentPostingsResponse, PaymentsOnAccount,
  PaymentsOnAccountRow, PostingSummaryByTypeResponse
}
import uk.gov.hmrc.corporationtax.services.InterestChargeService
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
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
    } yield
      val combinedPostings = combinePosting(paymentPostings)
      computeNewPaymentsOnAccount(
        combinedPostings,
        rfrPostingSummary,
        repPostingSummary,
        interestChargeSummary,
        allAccountingPeriod
      )

  private[payments] def computeNewPaymentsOnAccount(
    paymentPostings: PaymentPostingsResponse,
    rfrPostingSummary: PostingSummaryByTypeResponse,
    rtrPostingSummary: PostingSummaryByTypeResponse,
    interestChargeSummary: InterestCharges,
    allAccountingPeriods: AllAccountingPeriods
  ): PaymentsOnAccount = {
    @tailrec
    def evaluateEachPaymentsOnAccountRow(
      remainingList: List[PaymentPostingRow],
      acc: ListBuffer[PaymentsOnAccountRow] = ListBuffer.empty[PaymentsOnAccountRow]
    ): PaymentsOnAccount =
      remainingList match {
        case Nil          => PaymentsOnAccount(paymentsOnAccount = acc.toList)
        case head :: tail =>
          if accPeriodEqualsZero(head.accountingPeriod) then {
            evaluateEachPaymentsOnAccountRow(
              tail,
              applyReallocationsMayBeRepayments(None, head, rfrPostingSummary, rtrPostingSummary, acc)
            )
          } else {
            allAccountingPeriods.accountingPeriodList.find(_.accountingPeriod == head.accountingPeriod) match {
              case Some(matchingAccPeriod) =>
                if hasInterestChargesOrPenalties(matchingAccPeriod, head, interestChargeSummary) then {
                  evaluateEachPaymentsOnAccountRow(tail, acc)
                } else {
                  evaluateEachPaymentsOnAccountRow(
                    tail,
                    applyReallocationsMayBeRepayments(Some(matchingAccPeriod), head, rfrPostingSummary, rtrPostingSummary, acc)
                  )
                }

              case None => evaluateEachPaymentsOnAccountRow(tail, acc)
            }

          }
      }

    evaluateEachPaymentsOnAccountRow(paymentPostings.paymentPostingResults)
  }

  // Done
  private[payments] def accPeriodEqualsZero(accPeriod: Long): Boolean =
    accPeriod == zeroAccPeriod

  //Done
  private[payments] def applyReallocationsMayBeRepayments(
    matchingAccPeriodRecord: Option[AccountingPeriodList],
    currentPosting: PaymentPostingRow,
    rfrPostingSummary: PostingSummaryByTypeResponse,
    rtrPostingSummary: PostingSummaryByTypeResponse,
    acc: ListBuffer[PaymentsOnAccountRow]
  ): ListBuffer[PaymentsOnAccountRow] = {

    val postingAfterReallocationRepayments =
      if (accPeriodEqualsZero(currentPosting.accountingPeriod)) {
        applyReallocations(currentPosting, rfrPostingSummary)
      } else {
        val postingAfterReallocation = applyReallocations(currentPosting, rfrPostingSummary)
        val postingAfterRepayments   = applyRepayments(postingAfterReallocation, rtrPostingSummary)
        postingAfterRepayments
      }

    if (excludePayment(postingAfterReallocationRepayments, currentPosting)) {
      acc
    } else {
      val newPosting: PaymentsOnAccountRow =
        createNewPaymentsOnAccount(matchingAccPeriodRecord, postingAfterReallocationRepayments, currentPosting.postingAmount)
      acc += newPosting
    }

  }
  //Done
  private[payments] def combinePosting(paymentPostings: PaymentPostingsResponse): PaymentPostingsResponse = {
    val combinedRows: List[PaymentPostingRow] = paymentPostings.paymentPostingResults
      .foldLeft(Vector.empty[PaymentPostingRow]) { (acc, t) =>
        acc.indexWhere(row => (row.accountingPeriod == t.accountingPeriod) && (row.payslipId == t.payslipId)) match {
          case -1 => acc :+ t
          case i  => acc.updated(i, t.copy(postingAmount = acc(i).postingAmount + t.postingAmount))
        }
      }
      .toList

    paymentPostings.copy(paymentPostingResults = combinedRows)
  }

  // Done
  private[payments] def hasInterestChargesOrPenalties(
    matchingAccPeriod: AccountingPeriodList,
    currentPosting: PaymentPostingRow,
    interestChargeSummary: InterestCharges
  ): Boolean =
    hasInterestCharges(currentPosting, interestChargeSummary) || hasPenalty(matchingAccPeriod)

  // Done
  private[payments] def hasInterestCharges(
    currentPosting: PaymentPostingRow,
    interestChargeSummary: InterestCharges
  ): Boolean =
    interestChargeSummary.interestCharges.find(record =>
      (record.accountingPeriod == currentPosting.accountingPeriod) && (record.interestChargeSummary < zeroAmount)
    ) match {
      case Some(record) => true
      case None         => false
    }

  // Done
  private[payments] def hasPenalty(matchingAccPeriod: AccountingPeriodList): Boolean =
    matchingAccPeriod.taxChargePresent && matchingAccPeriod.penaltyTotal < zeroAmount

  //Done
  private def applyReallocations(
    currentPosting: PaymentPostingRow,
    rfrPosting: PostingSummaryByTypeResponse
  ): PaymentPostingRow =
    println("Reallocations")
    addToCurrentPaymentPostingRow(currentPosting, rfrPosting)

  //Done
  private def applyRepayments(
    currentPosting: PaymentPostingRow,
    rtrPosting: PostingSummaryByTypeResponse
  ): PaymentPostingRow =
    println("Repayments")
    addToCurrentPaymentPostingRow(currentPosting, rtrPosting)

  //Done
  private [payments] def addToCurrentPaymentPostingRow(
    currentPosting: PaymentPostingRow,
    repaymentsOrReallocations: PostingSummaryByTypeResponse
  ): PaymentPostingRow = {
    val matchingRecordsSum: BigDecimal = repaymentsOrReallocations.postingSummary
      .filter(record =>
        record.accountingPeriod == currentPosting.accountingPeriod && record.settlementSequenceNo == currentPosting.settlementSequenceNo
      )
      .map(_.postingAmountSum)
      .sum

    println(s"matching Record Sum : $matchingRecordsSum")

    val newPaymentPosting = currentPosting.copy(postingAmount = currentPosting.postingAmount + matchingRecordsSum)
    newPaymentPosting
  }

  //Done
  private[payments] def excludePayment(
    postingAfterReallocationsRepayments: PaymentPostingRow,
    originalPosting: PaymentPostingRow
  ): Boolean =
    println(s"PostingAfterReallocations ${postingAfterReallocationsRepayments.postingAmount}")
    println(s"originalPostingAmount ${originalPosting.postingAmount}")
    (postingAfterReallocationsRepayments.postingAmount == zeroAmount) || (postingAfterReallocationsRepayments.postingAmount > originalPosting.postingAmount)

  private[payments] def createNewPaymentsOnAccount(
    matchingAccPeriod: Option[AccountingPeriodList],
    currentPosting: PaymentPostingRow,
    originalAmount: BigDecimal
  ): PaymentsOnAccountRow =
    PaymentsOnAccountRow(
      accPeriod = currentPosting.accountingPeriod,
      effectiveDateOfPayment = currentPosting.effectiveDateOfPayment,
      apEndDate = evaluateApEndDate(matchingAccPeriod),
      paymentType = currentPosting.paymentType,
      postingAmount = AmountTransformation.apply(Some(currentPosting.postingAmount)),
      isPartPayment = isPartPayment(currentPosting.postingAmount, originalAmount)
    )

  // Done
  private[payments] def isPartPayment(currentPostingAmount: BigDecimal, originalPostingAmount: BigDecimal): Boolean =
    currentPostingAmount < originalPostingAmount

  // Done
  private[payments] def evaluateApEndDate(matchingAccPeriod: Option[AccountingPeriodList]): Option[LocalDate] =
    matchingAccPeriod match {
      case None => None
      case Some(value) => Some(value.apEndDate)
    }

}
