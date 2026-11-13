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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{times, verify, verifyNoMoreInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.models.BusinessConstants.{repPostingType, rfrPostingType}
import uk.gov.hmrc.corporationtax.models.payments.*
import uk.gov.hmrc.corporationtax.models.{InterestCharges, InterestChargesResponse}
import uk.gov.hmrc.corporationtax.services.InterestChargeService
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class PaymentsOnAccountServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockPaymentPostingService: PaymentPostingsService = mock[PaymentPostingsService]
    val mockPostingSummaryByType: PostingSummaryByTypeService = mock[PostingSummaryByTypeService]
    val mockInterestChargeService: InterestChargeService = mock[InterestChargeService]
    val mockAllAccountingPeriodsService: AllAccountingPeriodsService = mock[AllAccountingPeriodsService]

    val taxReference = 729374L

    val zero: Long = 0

    val service = new PaymentsOnAccountService(
      mockPaymentPostingService,
      mockPostingSummaryByType,
      mockInterestChargeService,
      mockAllAccountingPeriodsService
    )
  }

  "PaymentsOnAccountService" should {

    "getPaymentsOnAccount" should {
      "return an emptyList of PaymentsOnAccount when paymentPostings result returns an emptyList" in new BaseSetup {
        val emptyPaymentPostingsResponse: PaymentPostingsResponse = PaymentPostingsResponse(paymentPostingResults = List.empty)
        val rfrResponse: PostingSummaryByTypeResponse = PostingSummaryByTypeResponse(
          postingSummary = List(
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-1500.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202302),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("2000.50")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202303),
              settlementSequenceNo = BigDecimal(2),
              postingAmountSum = BigDecimal("800.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-250.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("100.99")
            )
          )
        )
        val repResponse: PostingSummaryByTypeResponse = PostingSummaryByTypeResponse(
          postingSummary = List(
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(19),
              postingAmountSum = BigDecimal("3250.75")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202307),
              settlementSequenceNo = BigDecimal(2),
              postingAmountSum = BigDecimal("675.40")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("0.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-950.43")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-10.00")
            )
          )
        )
        val interestCharges: InterestCharges =
          InterestCharges(
            List(
              InterestChargesResponse(
                accountingPeriod = BigDecimal(12),
                interestChargeSummary = BigDecimal(123.45)
              ),
              InterestChargesResponse(
                accountingPeriod = BigDecimal(145),
                interestChargeSummary = BigDecimal(987.45)
              )
            )
          )
        val allAccountingPeriods: AllAccountingPeriods = AllAccountingPeriods(
            List(
              AccountingPeriodList(
                accountingPeriod = BigDecimal(20),
                apEndDate = LocalDate.of(2025, 8, 24),
                taxChargePresent = true,
                penaltyTotal = BigDecimal(20)
              ),
              AccountingPeriodList(
                accountingPeriod = BigDecimal(30),
                apEndDate = LocalDate.of(2025, 8, 24),
                taxChargePresent = false,
                penaltyTotal = BigDecimal(30)
              )
            )
          )

        when(mockPaymentPostingService.getPaymentPostings(any())(any[HeaderCarrier])).thenReturn(Future.successful(emptyPaymentPostingsResponse))
        when(mockPostingSummaryByType.getPostingSummaryByType(any(), eqTo(rfrPostingType))(any[HeaderCarrier])).thenReturn(Future.successful(rfrResponse))
        when(mockPostingSummaryByType.getPostingSummaryByType(any(), eqTo(repPostingType))(any[HeaderCarrier])).thenReturn(Future.successful(repResponse))
        when(mockInterestChargeService.getInterestChargesSummary(any())(any[HeaderCarrier])).thenReturn(Future.successful(interestCharges))
        when(mockAllAccountingPeriodsService.getAccountingPeriodList(any())(any[HeaderCarrier])).thenReturn(Future.successful(allAccountingPeriods))

        val result: PaymentsOnAccount  = service.getPaymentsOnAccount(taxReference).futureValue

        result shouldBe PaymentsOnAccount(paymentsOnAccount = List.empty)

        verify(mockPaymentPostingService, times(1)).getPaymentPostings(any())(any[HeaderCarrier])
        verify(mockPostingSummaryByType, times(1)).getPostingSummaryByType(any(), eqTo(rfrPostingType))(any[HeaderCarrier])
        verify(mockPostingSummaryByType, times(1)).getPostingSummaryByType(any(), eqTo(repPostingType))(any[HeaderCarrier])
        verify(mockInterestChargeService, times(1)).getInterestChargesSummary(any())(any[HeaderCarrier])
        verify(mockAllAccountingPeriodsService, times(1)).getAccountingPeriodList(any())(any[HeaderCarrier])

        verifyNoMoreInteractions(mockPaymentPostingService)
        verifyNoMoreInteractions(mockPostingSummaryByType)
        verifyNoMoreInteractions(mockInterestChargeService)
        verifyNoMoreInteractions(mockAllAccountingPeriodsService)

      }
      "return a PaymentsOnAccount with single element by applying only reallocations when paymentPostings result returns an a list containing a single element with accPeriod zero" in new BaseSetup {
        val paymentPosting: PaymentPostingsResponse = PaymentPostingsResponse(
            List(
              PaymentPostingRow(
                accountingPeriod = 0,
                settlementSequenceNo = 1L,
                payslipId = 94L,
                postingAmount = 60000.75,
                effectiveDateOfPayment = LocalDate.of(2024, 9, 15),
                paymentType = "BAC"
              )
            )
          )
        val rfrResponse: PostingSummaryByTypeResponse = PostingSummaryByTypeResponse(
          postingSummary = List(
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-15000.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("2000.50")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(12),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("800.00")
            )
          )
        )
        val repResponse: PostingSummaryByTypeResponse = PostingSummaryByTypeResponse(
          postingSummary = List(
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(19),
              postingAmountSum = BigDecimal("3250.75")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202307),
              settlementSequenceNo = BigDecimal(2),
              postingAmountSum = BigDecimal("675.40")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("0.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-950.43")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-10.00")
            )
          )
        )
        val interestCharges: InterestCharges =
          InterestCharges(
            List(
              InterestChargesResponse(
                accountingPeriod = BigDecimal(12),
                interestChargeSummary = BigDecimal(123.45)
              ),
              InterestChargesResponse(
                accountingPeriod = BigDecimal(145),
                interestChargeSummary = BigDecimal(987.45)
              )
            )
          )
        val allAccountingPeriods: AllAccountingPeriods = AllAccountingPeriods(
            List(
              AccountingPeriodList(
                accountingPeriod = BigDecimal(20),
                apEndDate = LocalDate.of(2025, 8, 24),
                taxChargePresent = true,
                penaltyTotal = BigDecimal(20)
              ),
              AccountingPeriodList(
                accountingPeriod = BigDecimal(30),
                apEndDate = LocalDate.of(2025, 8, 24),
                taxChargePresent = false,
                penaltyTotal = BigDecimal(30)
              )
            )
          )

        when(mockPaymentPostingService.getPaymentPostings(any())(any[HeaderCarrier])).thenReturn(Future.successful(paymentPosting))
        when(mockPostingSummaryByType.getPostingSummaryByType(any(), eqTo(rfrPostingType))(any[HeaderCarrier])).thenReturn(Future.successful(rfrResponse))
        when(mockPostingSummaryByType.getPostingSummaryByType(any(), eqTo(repPostingType))(any[HeaderCarrier])).thenReturn(Future.successful(repResponse))
        when(mockInterestChargeService.getInterestChargesSummary(any())(any[HeaderCarrier])).thenReturn(Future.successful(interestCharges))
        when(mockAllAccountingPeriodsService.getAccountingPeriodList(any())(any[HeaderCarrier])).thenReturn(Future.successful(allAccountingPeriods))

        val expectedResult: PaymentsOnAccount = PaymentsOnAccount(paymentsOnAccount = List(
          PaymentsOnAccountRow(
            accPeriod = 0,
            effectiveDateOfPayment = LocalDate.of(2024, 9, 15),
            apEndDate = None,
            paymentType = "BAC",
            postingAmount = -47001.25,
            isPartPayment = true
          )
        ))

        val result: PaymentsOnAccount  = service.getPaymentsOnAccount(taxReference).futureValue

        result shouldBe expectedResult

        verify(mockPaymentPostingService, times(1)).getPaymentPostings(any())(any[HeaderCarrier])
        verify(mockPostingSummaryByType, times(1)).getPostingSummaryByType(any(), eqTo(rfrPostingType))(any[HeaderCarrier])
        verify(mockPostingSummaryByType, times(1)).getPostingSummaryByType(any(), eqTo(repPostingType))(any[HeaderCarrier])
        verify(mockInterestChargeService, times(1)).getInterestChargesSummary(any())(any[HeaderCarrier])
        verify(mockAllAccountingPeriodsService, times(1)).getAccountingPeriodList(any())(any[HeaderCarrier])

        verifyNoMoreInteractions(mockPaymentPostingService)
        verifyNoMoreInteractions(mockPostingSummaryByType)
        verifyNoMoreInteractions(mockInterestChargeService)
        verifyNoMoreInteractions(mockAllAccountingPeriodsService)

      }
      "return a single PaymentsOnAccount when paymentPostings result returns an a list containing a single element with non-zero accPeriod" in new BaseSetup {
        val emptyPaymentPostingsResponse: PaymentPostingsResponse = PaymentPostingsResponse(paymentPostingResults = List.empty)

        val rfrResponse: PostingSummaryByTypeResponse = PostingSummaryByTypeResponse(
          postingSummary = List(
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-1500.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202302),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("2000.50")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202303),
              settlementSequenceNo = BigDecimal(2),
              postingAmountSum = BigDecimal("800.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-250.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("100.99")
            )
          )
        )

        val repResponse: PostingSummaryByTypeResponse = PostingSummaryByTypeResponse(
          postingSummary = List(
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(19),
              postingAmountSum = BigDecimal("3250.75")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202307),
              settlementSequenceNo = BigDecimal(2),
              postingAmountSum = BigDecimal("675.40")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(0),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("0.00")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-950.43")
            ),
            PostingSummaryByTypeRow(
              accountingPeriod = BigDecimal(202306),
              settlementSequenceNo = BigDecimal(1),
              postingAmountSum = BigDecimal("-10.00")
            )
          )
        )

        val interestCharges: InterestCharges =
          InterestCharges(
            List(
              InterestChargesResponse(
                accountingPeriod = BigDecimal(12),
                interestChargeSummary = BigDecimal(123.45)
              ),
              InterestChargesResponse(
                accountingPeriod = BigDecimal(145),
                interestChargeSummary = BigDecimal(987.45)
              )
            )
          )

        val allAccountingPeriods: AllAccountingPeriods = AllAccountingPeriods(
            List(
              AccountingPeriodList(
                accountingPeriod = BigDecimal(20),
                apEndDate = LocalDate.of(2025, 8, 24),
                taxChargePresent = true,
                penaltyTotal = BigDecimal(20)
              ),
              AccountingPeriodList(
                accountingPeriod = BigDecimal(30),
                apEndDate = LocalDate.of(2025, 8, 24),
                taxChargePresent = false,
                penaltyTotal = BigDecimal(30)
              )
            )
          )

        when(mockPaymentPostingService.getPaymentPostings(any())(any[HeaderCarrier])).thenReturn(Future.successful(emptyPaymentPostingsResponse))
        when(mockPostingSummaryByType.getPostingSummaryByType(any(), eqTo(rfrPostingType))(any[HeaderCarrier])).thenReturn(Future.successful(rfrResponse))
        when(mockPostingSummaryByType.getPostingSummaryByType(any(), eqTo(repPostingType))(any[HeaderCarrier])).thenReturn(Future.successful(repResponse))
        when(mockInterestChargeService.getInterestChargesSummary(any())(any[HeaderCarrier])).thenReturn(Future.successful(interestCharges))
        when(mockAllAccountingPeriodsService.getAccountingPeriodList(any())(any[HeaderCarrier])).thenReturn(Future.successful(allAccountingPeriods))

        val result: PaymentsOnAccount  = service.getPaymentsOnAccount(taxReference).futureValue

        result shouldBe PaymentsOnAccount(paymentsOnAccount = List.empty)

        verify(mockPaymentPostingService, times(1)).getPaymentPostings(any())(any[HeaderCarrier])
        verify(mockPostingSummaryByType, times(1)).getPostingSummaryByType(any(), eqTo(rfrPostingType))(any[HeaderCarrier])
        verify(mockPostingSummaryByType, times(1)).getPostingSummaryByType(any(), eqTo(repPostingType))(any[HeaderCarrier])
        verify(mockInterestChargeService, times(1)).getInterestChargesSummary(any())(any[HeaderCarrier])
        verify(mockAllAccountingPeriodsService, times(1)).getAccountingPeriodList(any())(any[HeaderCarrier])

        verifyNoMoreInteractions(mockPaymentPostingService)
        verifyNoMoreInteractions(mockPostingSummaryByType)
        verifyNoMoreInteractions(mockInterestChargeService)
        verifyNoMoreInteractions(mockAllAccountingPeriodsService)

      }
    }

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
      "return None if the currentPosting accountingPeriod is zero(Suspense Account)" in new BaseSetup {
        val result: Option[LocalDate] = service.evaluateApEndDate(None)
        result shouldBe None
      }
      "return Some(apEndDate) if the currentPosting accPeriod is not zero and matching accPeriod is found in AllAccountingPeriods" in new BaseSetup {
        val matchingAccountingPeriod: AccountingPeriodList = AccountingPeriodList(
          accountingPeriod = 123456L,
          apEndDate = LocalDate.of(2022, 12, 23),
          taxChargePresent = false,
          penaltyTotal = BigDecimal(1873.99)
        )
        val result: Option[LocalDate]                      = service.evaluateApEndDate(Some(matchingAccountingPeriod))

        result shouldBe Some(matchingAccountingPeriod.apEndDate)

      }
    }

    "hasInterestChargesOrPenalties" should {
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
          service.hasInterestChargesOrPenalties(matchingAccPeriodList, currentPaymentPostingRow, interestCharges)
        result shouldBe true
      }
      "should return true when hasPenalty returns true" in new BaseSetup {
        val result: Boolean =
          service.hasInterestChargesOrPenalties(matchingAccPeriodList, currentPaymentPostingRow, interestCharges)
        result shouldBe true
      }

      "should return false when hasPenalty returns false " in new BaseSetup {
        val result: Boolean = service.hasInterestChargesOrPenalties(
          matchingAccPeriodListWithoutTaxChargePresent,
          currentPaymentPostingRow,
          interestCharges
        )
        result shouldBe false
      }
      "should return false when hasInterestCharges returns false as no PaymentPostingRow records were found " in new BaseSetup {
        val result: Boolean = service.hasInterestChargesOrPenalties(
          matchingAccPeriodListWithoutTaxChargePresent,
          notMatchingCurrentPaymentPostingRow,
          interestCharges
        )
        result shouldBe false

      }
    }

    "combinePosting" should {
      val paymentPostings: List[PaymentPostingRow]         = List(
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("1800.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 2L,
          payslipId = 5001L,
          postingAmount = BigDecimal("1500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202402L,
          settlementSequenceNo = 3L,
          payslipId = 5003L,
          postingAmount = BigDecimal("1500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 2, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202402L,
          settlementSequenceNo = 4L,
          payslipId = 5004L,
          postingAmount = BigDecimal("300.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 2, 20),
          paymentType = "EXPENSES"
        ),
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 5L,
          payslipId = 5002L,
          postingAmount = BigDecimal("250.50"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "BONUS"
        )
      )
      val distinctPaymentPostings: List[PaymentPostingRow] = List(
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("1800.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 2L,
          payslipId = 5001L,
          postingAmount = BigDecimal("1500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202402232L,
          settlementSequenceNo = 3L,
          payslipId = 5003L,
          postingAmount = BigDecimal("1500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 2, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 2024232302L,
          settlementSequenceNo = 4L,
          payslipId = 5004L,
          postingAmount = BigDecimal("300.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 2, 20),
          paymentType = "EXPENSES"
        ),
        PaymentPostingRow(
          accountingPeriod = 2023232401L,
          settlementSequenceNo = 5L,
          payslipId = 5001L,
          postingAmount = BigDecimal("250.50"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "BONUS"
        ),
        PaymentPostingRow(
          accountingPeriod = 203232403L,
          settlementSequenceNo = 6L,
          payslipId = 5005L,
          postingAmount = BigDecimal("2100.25"),
          effectiveDateOfPayment = LocalDate.of(2024, 3, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 22302403L,
          settlementSequenceNo = 7L,
          payslipId = 500326L,
          postingAmount = BigDecimal("-45.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 3, 18),
          paymentType = "DEDUCTION"
        ),
        PaymentPostingRow(
          accountingPeriod = 232202404L,
          settlementSequenceNo = 8L,
          payslipId = 5232007L,
          postingAmount = BigDecimal("1950.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 4, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 2553202401L,
          settlementSequenceNo = 9L,
          payslipId = 5023201L,
          postingAmount = BigDecimal("-120.75"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "DEDUCTION"
        ),
        PaymentPostingRow(
          accountingPeriod = 2025523404L,
          settlementSequenceNo = 10L,
          payslipId = 5023208L,
          postingAmount = BigDecimal("500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 4, 30),
          paymentType = "BONUS"
        )
      )
      val combinedPaymentPostings: List[PaymentPostingRow] = List(
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 5L,
          payslipId = 5002L,
          postingAmount = BigDecimal("2050.50"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "BONUS"
        ),
        PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 2L,
          payslipId = 5001L,
          postingAmount = BigDecimal("1500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202402L,
          settlementSequenceNo = 3L,
          payslipId = 5003L,
          postingAmount = BigDecimal("1500.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 2, 15),
          paymentType = "SALARY"
        ),
        PaymentPostingRow(
          accountingPeriod = 202402L,
          settlementSequenceNo = 4L,
          payslipId = 5004L,
          postingAmount = BigDecimal("300.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 2, 20),
          paymentType = "EXPENSES"
        )
      )

      val paymentsPostingsResponse        = PaymentPostingsResponse(paymentPostingResults = paymentPostings)
      val combinedPaymentPostingsResponse = PaymentPostingsResponse(paymentPostingResults = combinedPaymentPostings)
      val distinctPaymentPostingsResponse = PaymentPostingsResponse(paymentPostingResults = distinctPaymentPostings)

      "should return the combined posting by adding the current postingAmount to next postingAmount when two postings have same accPeriod and payslipId " in new BaseSetup {
        val result: PaymentPostingsResponse = service.combinePosting(paymentsPostingsResponse)
        result shouldBe combinedPaymentPostingsResponse
      }
      "should return the posting unchanged when the current postingAmount to next postingAmount have not matching accPeriod and payslipId" in new BaseSetup {
        val result: PaymentPostingsResponse = service.combinePosting(distinctPaymentPostingsResponse)
        result shouldBe distinctPaymentPostingsResponse

      }
    }

    "excludePayment" should {
      "return true when postingAmount is zero" in new BaseSetup {
        val originalPaymentPostings: PaymentPostingRow          = PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("0.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        )
        val paymentPostingAfterReallocations: PaymentPostingRow = PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("1800.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        )

        val result: Boolean = service.excludePayment(paymentPostingAfterReallocations, originalPaymentPostings)

        result shouldBe true

      }

      "return true when postingAmount after reallocations is greater than originalPosting amount" in new BaseSetup {

        val originalPaymentPostings: PaymentPostingRow          = PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("1800.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        )
        val paymentPostingAfterReallocations: PaymentPostingRow = PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("2000.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        )

        val result: Boolean = service.excludePayment(paymentPostingAfterReallocations, originalPaymentPostings)

        result shouldBe true

      }

      "return false when postingAmount is greater than zero or postingAmount for reallocations is less than originalPosting amount" in new BaseSetup {

        val originalPaymentPostings: PaymentPostingRow          = PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("-1800.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        )
        val paymentPostingAfterReallocations: PaymentPostingRow = PaymentPostingRow(
          accountingPeriod = 202401L,
          settlementSequenceNo = 1L,
          payslipId = 5002L,
          postingAmount = BigDecimal("0.00"),
          effectiveDateOfPayment = LocalDate.of(2024, 1, 31),
          paymentType = "SALARY"
        )

        val result: Boolean = service.excludePayment(paymentPostingAfterReallocations, originalPaymentPostings)

        result shouldBe true

      }
    }

    "applyReallocationsMayBeRepayments" should {
      val currentPaymentPostingRowWithZeroAccPeriod    = PaymentPostingRow(
        accountingPeriod = 0L,
        settlementSequenceNo = 1L,
        payslipId = 1006L,
        postingAmount = BigDecimal("3250.75"),
        effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
        paymentType = "BACS"
      )
      val currentPaymentPostingRowWithNonZeroAccPeriod = PaymentPostingRow(
        accountingPeriod = 202306L,
        settlementSequenceNo = 1L,
        payslipId = 1006L,
        postingAmount = BigDecimal("3250.75"),
        effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
        paymentType = "BACS"
      )

      val rfrResponse = PostingSummaryByTypeResponse(
        postingSummary = List(
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(0),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("-1500.00")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202302),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("2000.50")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202303),
            settlementSequenceNo = BigDecimal(2),
            postingAmountSum = BigDecimal("800.00")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202306),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("-250.00")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202306),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("100.99")
          )
        )
      )

      val repResponse = PostingSummaryByTypeResponse(
        postingSummary = List(
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202306),
            settlementSequenceNo = BigDecimal(19),
            postingAmountSum = BigDecimal("3250.75")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202307),
            settlementSequenceNo = BigDecimal(2),
            postingAmountSum = BigDecimal("675.40")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(0),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("0.00")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202306),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("-950.43")
          ),
          PostingSummaryByTypeRow(
            accountingPeriod = BigDecimal(202306),
            settlementSequenceNo = BigDecimal(1),
            postingAmountSum = BigDecimal("-10.00")
          )
        )
      )

      val originalAccumulator = ListBuffer(
        PaymentsOnAccountRow(
          accPeriod = 202306L,
          effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
          apEndDate = Some(LocalDate.of(2023, 9, 30)),
          paymentType = "BACS",
          postingAmount = BigDecimal("3250.75"),
          isPartPayment = false
        ),
        PaymentsOnAccountRow(
          accPeriod = 202307L,
          effectiveDateOfPayment = LocalDate.of(2023, 7, 21),
          apEndDate = Some(LocalDate.of(2023, 9, 30)),
          paymentType = "CHAPS",
          postingAmount = BigDecimal("675.40"),
          isPartPayment = true
        ),
        PaymentsOnAccountRow(
          accPeriod = 202308L,
          effectiveDateOfPayment = LocalDate.of(2023, 8, 1),
          apEndDate = Some(LocalDate.of(2023, 12, 31)),
          paymentType = "DD",
          postingAmount = BigDecimal("0.00"),
          isPartPayment = false
        ),
        PaymentsOnAccountRow(
          accPeriod = 202309L,
          effectiveDateOfPayment = LocalDate.of(2023, 9, 29),
          apEndDate = Some(LocalDate.of(2023, 12, 31)),
          paymentType = "CARD",
          postingAmount = BigDecimal("98765.43"),
          isPartPayment = true
        ),
        PaymentsOnAccountRow(
          accPeriod = 202310L,
          effectiveDateOfPayment = LocalDate.of(2023, 10, 16),
          apEndDate = None,
          paymentType = "REFUND",
          postingAmount = BigDecimal("-1120.00"),
          isPartPayment = false
        )
      )

      "apply only Reallocations when the accPeriod is zero" in new BaseSetup {

        val accumulatorAfterReallocations: ListBuffer[PaymentsOnAccountRow] = ListBuffer(
          PaymentsOnAccountRow(
            accPeriod = 202306L,
            effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
            apEndDate = Some(LocalDate.of(2023, 9, 30)),
            paymentType = "BACS",
            postingAmount = BigDecimal("3250.75"),
            isPartPayment = false
          ),
          PaymentsOnAccountRow(
            accPeriod = 202307L,
            effectiveDateOfPayment = LocalDate.of(2023, 7, 21),
            apEndDate = Some(LocalDate.of(2023, 9, 30)),
            paymentType = "CHAPS",
            postingAmount = BigDecimal("675.40"),
            isPartPayment = true
          ),
          PaymentsOnAccountRow(
            accPeriod = 202308L,
            effectiveDateOfPayment = LocalDate.of(2023, 8, 1),
            apEndDate = Some(LocalDate.of(2023, 12, 31)),
            paymentType = "DD",
            postingAmount = BigDecimal("0.00"),
            isPartPayment = false
          ),
          PaymentsOnAccountRow(
            accPeriod = 202309L,
            effectiveDateOfPayment = LocalDate.of(2023, 9, 29),
            apEndDate = Some(LocalDate.of(2023, 12, 31)),
            paymentType = "CARD",
            postingAmount = BigDecimal("98765.43"),
            isPartPayment = true
          ),
          PaymentsOnAccountRow(
            accPeriod = 202310L,
            effectiveDateOfPayment = LocalDate.of(2023, 10, 16),
            apEndDate = None,
            paymentType = "REFUND",
            postingAmount = BigDecimal("-1120.00"),
            isPartPayment = false
          ),
          PaymentsOnAccountRow(
            accPeriod = 0,
            effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
            apEndDate = None,
            paymentType = "BACS",
            postingAmount = BigDecimal("-1750.75"),
            isPartPayment = true
          )
        )

        val result: ListBuffer[PaymentsOnAccountRow] = service.applyReallocationsMayBeRepayments(
          None,
          currentPaymentPostingRowWithZeroAccPeriod,
          rfrResponse,
          repResponse,
          originalAccumulator
        )

        result shouldBe accumulatorAfterReallocations

      }

      "apply both Reallocations and Repayments when the accPeriod is not zero" in new BaseSetup {

        val matchingAccountingPeriod: AccountingPeriodList = AccountingPeriodList(
          accountingPeriod = 202306,
          apEndDate = LocalDate.of(2022, 12, 23),
          taxChargePresent = false,
          penaltyTotal = BigDecimal(1873.99)
        )

        val accumulatorAfterReallocationsAndRepayments: ListBuffer[PaymentsOnAccountRow] = ListBuffer(
          PaymentsOnAccountRow(
            accPeriod = 202306L,
            effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
            apEndDate = Some(LocalDate.of(2023, 9, 30)),
            paymentType = "BACS",
            postingAmount = BigDecimal("3250.75"),
            isPartPayment = false
          ),
          PaymentsOnAccountRow(
            accPeriod = 202307L,
            effectiveDateOfPayment = LocalDate.of(2023, 7, 21),
            apEndDate = Some(LocalDate.of(2023, 9, 30)),
            paymentType = "CHAPS",
            postingAmount = BigDecimal("675.40"),
            isPartPayment = true
          ),
          PaymentsOnAccountRow(
            accPeriod = 202308L,
            effectiveDateOfPayment = LocalDate.of(2023, 8, 1),
            apEndDate = Some(LocalDate.of(2023, 12, 31)),
            paymentType = "DD",
            postingAmount = BigDecimal("0.00"),
            isPartPayment = false
          ),
          PaymentsOnAccountRow(
            accPeriod = 202309L,
            effectiveDateOfPayment = LocalDate.of(2023, 9, 29),
            apEndDate = Some(LocalDate.of(2023, 12, 31)),
            paymentType = "CARD",
            postingAmount = BigDecimal("98765.43"),
            isPartPayment = true
          ),
          PaymentsOnAccountRow(
            accPeriod = 202310L,
            effectiveDateOfPayment = LocalDate.of(2023, 10, 16),
            apEndDate = None,
            paymentType = "REFUND",
            postingAmount = BigDecimal("-1120.00"),
            isPartPayment = false
          ),
          PaymentsOnAccountRow(
            accPeriod = 202306,
            effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
            apEndDate = Some(LocalDate.of(2022, 12, 23)),
            paymentType = "BACS",
            postingAmount = BigDecimal("-2141.31"),
            isPartPayment = true
          )
        )

        val result: ListBuffer[PaymentsOnAccountRow] = service.applyReallocationsMayBeRepayments(
          Some(matchingAccountingPeriod),
          currentPaymentPostingRowWithNonZeroAccPeriod,
          rfrResponse,
          repResponse,
          originalAccumulator
        )

        result shouldBe accumulatorAfterReallocationsAndRepayments

      }

      "return the buffer unchanged if the current posting is excluded " in new BaseSetup {
        val currentPaymentPosting: PaymentPostingRow       = PaymentPostingRow(
          accountingPeriod = 202306L,
          settlementSequenceNo = 1L,
          payslipId = 1006L,
          postingAmount = BigDecimal("30750.75"),
          effectiveDateOfPayment = LocalDate.of(2023, 6, 14),
          paymentType = "BACS"
        )
        val matchingAccountingPeriod: AccountingPeriodList = AccountingPeriodList(
          accountingPeriod = 202306,
          apEndDate = LocalDate.of(2022, 12, 23),
          taxChargePresent = false,
          penaltyTotal = BigDecimal(1873.99)
        )

        val result: ListBuffer[PaymentsOnAccountRow] = service.applyReallocationsMayBeRepayments(
          Some(matchingAccountingPeriod),
          currentPaymentPostingRowWithNonZeroAccPeriod,
          rfrResponse,
          repResponse,
          originalAccumulator
        )

        result shouldBe originalAccumulator

        result.length shouldBe originalAccumulator.length
      }

    }

  }
}
