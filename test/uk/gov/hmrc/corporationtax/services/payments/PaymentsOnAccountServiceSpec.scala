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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentsOnAccountHelper
import uk.gov.hmrc.corporationtax.models.payments.{AccountingPeriodList, PaymentPostingRow}
import uk.gov.hmrc.corporationtax.models.{InterestCharges, InterestChargesResponse}
import uk.gov.hmrc.corporationtax.services.InterestChargeService
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class PaymentsOnAccountServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with PaymentsOnAccountHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    private val mockPaymentPostingService       = mock[PaymentPostingsService]
    private val mockPostingSummaryByType        = mock[PostingSummaryByTypeService]
    private val mockInterestChargeService       = mock[InterestChargeService]
    private val mockAllAccountingPeriodsService = mock[AllAccountingPeriodsService]

    val zero: Long = 0

    val service = new PaymentsOnAccountService(
      mockPaymentPostingService,
      mockPostingSummaryByType,
      mockInterestChargeService,
      mockAllAccountingPeriodsService
    )
  }

  "PaymentsOnAccountService" should {
    "accPeriodEqualsZero" should {
      "return true if accPeriod is zero" in new BaseSetup {
        val result: Boolean = service.accPeriodEqualsZero(zero)
        result shouldBe true
      }
      "return false if accPeriod is not zero" in new BaseSetup {
        val result: Boolean = service.accPeriodEqualsZero(124121)
        result shouldBe false
      }
    }

    "isPartPayment" should {
      "return true when currentPostingAmount is less than originalPostingAmount" in new BaseSetup {
        val result: Boolean = service.isPartPayment(BigDecimal(1500), BigDecimal(1600))
        result shouldBe true
      }
      "return false when currentPostingAmount is greater than originalPostingAmount" in new BaseSetup {
        val result: Boolean = service.isPartPayment(BigDecimal(1900), BigDecimal(1200))
        result shouldBe false
      }
    }

    "evaluateApEndDate" should {

      val paymentPosting = PaymentPostingRow(
        accountingPeriod = 123456L,
        settlementSequenceNo = 1234141L,
        payslipId = 797L,
        postingAmount = BigDecimal(12341.79),
        effectiveDateOfPayment = LocalDate.of(2022, 12, 23),
        paymentType = "BACS"
      )

      "return None if the currentPosting accountingPeriod is zero" in new BaseSetup {
        val result: Option[LocalDate] = service.evaluateApEndDate(paymentPosting)
        result shouldBe Some(paymentPosting.effectiveDateOfPayment)
      }
      "return Some(address) if the currentPosting accPeriod is not zero" in new BaseSetup {
        val result: Option[LocalDate] = service.evaluateApEndDate(paymentPosting.copy(accountingPeriod = 0))
        result shouldBe None

      }
    }

    "isValidPaymentOnPosting" should {
      val currentPaymentPostingRow                     = PaymentPostingRow(
        accountingPeriod = 123456L,
        settlementSequenceNo = 9876L,
        payslipId = 5676L,
        postingAmount = BigDecimal(67560.75),
        effectiveDateOfPayment = LocalDate.of(2026, 7, 12),
        paymentType = "Cheque"
      )
      val notMatchingCurrentPaymentPostingRow          = PaymentPostingRow(
        accountingPeriod = 123L,
        settlementSequenceNo = 9876L,
        payslipId = 5676L,
        postingAmount = BigDecimal(67560.75),
        effectiveDateOfPayment = LocalDate.of(2026, 7, 12),
        paymentType = "BACS"
      )
      val matchingAccPeriodList                        = AccountingPeriodList(
        accountingPeriod = BigDecimal(12424.67),
        apEndDate = LocalDate.of(2023, 5, 6),
        taxChargePresent = true,
        penaltyTotal = BigDecimal(-6868.98)
      )
      val matchingAccPeriodListWithoutTaxChargePresent = AccountingPeriodList(
        accountingPeriod = BigDecimal(12424.67),
        apEndDate = LocalDate.of(2023, 5, 6),
        taxChargePresent = false,
        penaltyTotal = BigDecimal(-6868.98)
      )
      val interestCharges: InterestCharges             = InterestCharges(
        interestCharges = List(
          InterestChargesResponse(
            accountingPeriod = 83737L,
            interestChargeSummary = BigDecimal(686.87)
          ),
          InterestChargesResponse(
            accountingPeriod = 837L,
            interestChargeSummary = BigDecimal(700.87)
          ),
          InterestChargesResponse(
            accountingPeriod = 123456L,
            interestChargeSummary = BigDecimal(686.87)
          ),
          InterestChargesResponse(
            accountingPeriod = 737L,
            interestChargeSummary = BigDecimal(500.87)
          ),
          InterestChargesResponse(
            accountingPeriod = 67353L,
            interestChargeSummary = BigDecimal(34526.87)
          )
        )
      )
      "should return true when hasInterestCharges returns true " in new BaseSetup {
        val result: Boolean =
          service.isValidPaymentOnPosting(matchingAccPeriodList, currentPaymentPostingRow, interestCharges)
        result shouldBe true
      }
      "should return true when hasPenalty returns true" in new BaseSetup {
        val result: Boolean =
          service.isValidPaymentOnPosting(matchingAccPeriodList, currentPaymentPostingRow, interestCharges)
        result shouldBe true
      }

      "should return false when hasPenalty returns false " in new BaseSetup {
        val result: Boolean = service.isValidPaymentOnPosting(
          matchingAccPeriodListWithoutTaxChargePresent,
          currentPaymentPostingRow,
          interestCharges
        )
        result shouldBe false
      }
      "should return false when hasInterestCharges returns false as no PaymentPostingRow records were found " in new BaseSetup {
        val result: Boolean = service.isValidPaymentOnPosting(
          matchingAccPeriodListWithoutTaxChargePresent,
          notMatchingCurrentPaymentPostingRow,
          interestCharges
        )
        result shouldBe false

      }
    }

//    "combinePosting" should {
//      val paymentPostings: List[PaymentPostingRow] = List(
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 1L,
//          payslipId = 5002L,
//          postingAmount = BigDecimal("1800.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 2L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("1500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202402L,
//          settlementSequenceNo = 3L,
//          payslipId = 5003L,
//          postingAmount = BigDecimal("1500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 2, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202402L,
//          settlementSequenceNo = 4L,
//          payslipId = 5004L,
//          postingAmount = BigDecimal("300.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 2, 20),
//          paymentType = "EXPENSES"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 5L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("250.50"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "BONUS"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202403L,
//          settlementSequenceNo = 6L,
//          payslipId = 5005L,
//          postingAmount = BigDecimal("2100.25"),
//          effectiveDateOfPayment = LocalDate.of(2024, 3, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202403L,
//          settlementSequenceNo = 7L,
//          payslipId = 5006L,
//          postingAmount = BigDecimal("-45.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 3, 18),
//          paymentType = "DEDUCTION"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202404L,
//          settlementSequenceNo = 8L,
//          payslipId = 5007L,
//          postingAmount = BigDecimal("1950.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 4, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 9L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("-120.75"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "DEDUCTION"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202404L,
//          settlementSequenceNo = 10L,
//          payslipId = 5008L,
//          postingAmount = BigDecimal("500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 4, 30),
//          paymentType = "BONUS"
//        )
//      )
//      val distinctPaymentPostings: List[PaymentPostingRow] = List(
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 1L,
//          payslipId = 5002L,
//          postingAmount = BigDecimal("1800.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 2L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("1500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202402232L,
//          settlementSequenceNo = 3L,
//          payslipId = 5003L,
//          postingAmount = BigDecimal("1500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 2, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 2024232302L,
//          settlementSequenceNo = 4L,
//          payslipId = 5004L,
//          postingAmount = BigDecimal("300.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 2, 20),
//          paymentType = "EXPENSES"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 2023232401L,
//          settlementSequenceNo = 5L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("250.50"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "BONUS"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 203232403L,
//          settlementSequenceNo = 6L,
//          payslipId = 5005L,
//          postingAmount = BigDecimal("2100.25"),
//          effectiveDateOfPayment = LocalDate.of(2024, 3, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 22302403L,
//          settlementSequenceNo = 7L,
//          payslipId = 500326L,
//          postingAmount = BigDecimal("-45.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 3, 18),
//          paymentType = "DEDUCTION"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 232202404L,
//          settlementSequenceNo = 8L,
//          payslipId = 5232007L,
//          postingAmount = BigDecimal("1950.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 4, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 2553202401L,
//          settlementSequenceNo = 9L,
//          payslipId = 5023201L,
//          postingAmount = BigDecimal("-120.75"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "DEDUCTION"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 2025523404L,
//          settlementSequenceNo = 10L,
//          payslipId = 5023208L,
//          postingAmount = BigDecimal("500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 4, 30),
//          paymentType = "BONUS"
//        )
//      )
//
//      val combinedPaymentPostings: List[PaymentPostingRow] = List(
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 1L,
//          payslipId = 5002L,
//          postingAmount = BigDecimal("1800.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 2L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("1500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202402L,
//          settlementSequenceNo = 4L,
//          payslipId = 5004L,
//          postingAmount = BigDecimal("300.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 2, 20),
//          paymentType = "EXPENSES"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202403L,
//          settlementSequenceNo = 6L,
//          payslipId = 5005L,
//          postingAmount = BigDecimal("2100.25"),
//          effectiveDateOfPayment = LocalDate.of(2024, 3, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202403L,
//          settlementSequenceNo = 7L,
//          payslipId = 5006L,
//          postingAmount = BigDecimal("-45.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 3, 18),
//          paymentType = "DEDUCTION"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202404L,
//          settlementSequenceNo = 8L,
//          payslipId = 5007L,
//          postingAmount = BigDecimal("1950.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 4, 15),
//          paymentType = "SALARY"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202401L,
//          settlementSequenceNo = 9L,
//          payslipId = 5001L,
//          postingAmount = BigDecimal("1629.75"),
//          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
//          paymentType = "DEDUCTION"
//        ),
//        PaymentPostingRow(
//          accountingPeriod = 202404L,
//          settlementSequenceNo = 10L,
//          payslipId = 5008L,
//          postingAmount = BigDecimal("500.00"),
//          effectiveDateOfPayment = LocalDate.of(2024, 4, 30),
//          paymentType = "BONUS"
//        )
//      )
//      val paymentPostingsResponse = PaymentPostingsResponse(paymentPostingResults = paymentPostings)
//
//      val distinctPaymentsPostingResponse = PaymentPostingsResponse(paymentPostingResults = distinctPaymentPostings)
//      "should return the combined posting by adding the current postingAmount to next postingAmount when two postings have same accPeriod and payslipId " in new BaseSetup {
//        val result: PaymentPostingsResponse = service.combinePosting(paymentPostingsResponse)
//        result shouldBe combinedPaymentPostings
//      }
//      "should return the posting unchanged when  by adding the current postingAmount to next postingAmount when two postings have not matching accPeriod and payslipId" in new BaseSetup {
//        val result: PaymentPostingsResponse = service.combinePosting(distinctPaymentsPostingResponse)
//        result shouldBe distinctPaymentPostings
//
//      }
//    }

  }
}
