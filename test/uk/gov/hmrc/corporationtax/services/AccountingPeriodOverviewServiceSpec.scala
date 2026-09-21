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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, verifyNoInteractions, verifyNoMoreInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.models.{
  AccountingPeriodDetails, AccountingPeriodDetailsResponse, AccountingPeriodOverview, AccountingPeriodsRowResponse,
  DisplayNeeded, MissingAccountingPeriodError
}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodOverviewServiceSpec extends AnyWordSpec with Matchers with ScalaFutures {

  private trait Fixture {
    val mockAccPeriodService: AccountingPeriodService               = mock[AccountingPeriodService]
    val mockAccPeriodDetailsService: AccountingPeriodDetailsService = mock[AccountingPeriodDetailsService]
    val mockDisplayNeededService: DisplayNeededService              = mock[DisplayNeededService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val taxRef    = 12L
    val accPeriod = 36L

    val service                                                                           =
      new AccountingPeriodOverviewService(mockAccPeriodService, mockAccPeriodDetailsService, mockDisplayNeededService)
    val accPeriodRowResponseWithTaxChargePresent: AccountingPeriodsRowResponse            = AccountingPeriodsRowResponse(
      accountingPeriod = BigDecimal(202501),
      apStartDate = LocalDate.of(2025, 1, 1),
      apEndDate = LocalDate.of(2025, 12, 31),
      apStatus = "Open",
      taxChargePresent = true,
      clericalIntSig = false,
      creditDebitInterestInd = false,
      taxTotal = BigDecimal(3000.00),
      interestTotal = BigDecimal(100.00),
      penaltyTotal = BigDecimal(6543.00),
      payslipTotal = BigDecimal(5000.00),
      repayReallocTotal = BigDecimal(6500.00),
      adjustmentTotal = BigDecimal(13553.87)
    )
    val accPeriodRowResponseWithNoChargePresent: AccountingPeriodsRowResponse             = AccountingPeriodsRowResponse(
      accountingPeriod = BigDecimal(202501),
      apStartDate = LocalDate.of(2025, 1, 1),
      apEndDate = LocalDate.of(2025, 12, 31),
      apStatus = "Open",
      taxChargePresent = false,
      clericalIntSig = false,
      creditDebitInterestInd = false,
      taxTotal = BigDecimal(3000.00),
      interestTotal = BigDecimal(100.00),
      penaltyTotal = BigDecimal(6543.00),
      payslipTotal = BigDecimal(5000.00),
      repayReallocTotal = BigDecimal(6500.00),
      adjustmentTotal = BigDecimal(13553.87)
    )
    val accPeriodRowResponseForFalseClericalCalculationFlag: AccountingPeriodsRowResponse =
      AccountingPeriodsRowResponse(
        accountingPeriod = BigDecimal(202501),
        apStartDate = LocalDate.of(2025, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
        apStatus = "Open",
        taxChargePresent = false,
        clericalIntSig = false,
        creditDebitInterestInd = false,
        taxTotal = BigDecimal(3000.00),
        interestTotal = BigDecimal(100.00),
        penaltyTotal = BigDecimal(6543.00),
        payslipTotal = BigDecimal(5000.00),
        repayReallocTotal = BigDecimal(6500.00),
        adjustmentTotal = BigDecimal(13553.87)
      )
    val accPeriodRowResponseForTrueClericalCalculationFlag: AccountingPeriodsRowResponse  = AccountingPeriodsRowResponse(
      accountingPeriod = BigDecimal(202501),
      apStartDate = LocalDate.of(2025, 1, 1),
      apEndDate = LocalDate.of(2025, 12, 31),
      apStatus = "Open",
      taxChargePresent = true,
      clericalIntSig = true,
      creditDebitInterestInd = false,
      taxTotal = BigDecimal(3000.00),
      interestTotal = BigDecimal(100.00),
      penaltyTotal = BigDecimal(6543.00),
      payslipTotal = BigDecimal(5000.00),
      repayReallocTotal = BigDecimal(6500.00),
      adjustmentTotal = BigDecimal(13553.87)
    )
    val displayNeededResponse: DisplayNeeded                                              = DisplayNeeded(
      taxIsDisplayNeededFlag = true,
      interestIsDisplayNeededFlag = false,
      paymentIsDisplayNeededFlag = true,
      repayReallocIsDisplayNeededFlag = false
    )
    val accountingPeriodDetails: AccountingPeriodDetailsResponse                          = AccountingPeriodDetailsResponse(
      accountingPeriodDetails = AccountingPeriodDetails(
        isApBalanced = true,
        lpiCalcFlag = false,
        crDbCalcFlag = false,
        creditInterestAmount = BigDecimal(-123.24),
        debitInterestAmount = BigDecimal(-5930.02),
        latePaymentInterestAmount = BigDecimal(3231.24),
        repaymentInterestAmount = BigDecimal(1567.23),
        totalDerivedActualInterest = BigDecimal(9297.95),
        amountDueForAp = BigDecimal(-12.23)
      )
    )

    def getAccPeriodOverviewForTaxChargePresent(
      accountingPeriodsRowResponse: AccountingPeriodsRowResponse,
      accountingPeriodDetails: AccountingPeriodDetailsResponse,
      displayNeeded: DisplayNeeded,
      clericalCalculationFlag: Boolean
    ): AccountingPeriodOverview =
      AccountingPeriodOverview(
        accountingPeriod = accountingPeriodsRowResponse.accountingPeriod,
        apStartDate = accountingPeriodsRowResponse.apStartDate,
        apEndDate = accountingPeriodsRowResponse.apEndDate,
        apStatus = accountingPeriodsRowResponse.apStatus,
        taxChargePresent = accountingPeriodsRowResponse.taxChargePresent,
        clericalIntSig = accountingPeriodsRowResponse.clericalIntSig,
        creditDebitInterestInd = accountingPeriodsRowResponse.creditDebitInterestInd,
        taxTotal = accountingPeriodsRowResponse.taxTotal,
        interestTotal = accountingPeriodDetails.accountingPeriodDetails.totalDerivedActualInterest,
        penaltyTotal = accountingPeriodsRowResponse.penaltyTotal,
        payslipTotal = accountingPeriodsRowResponse.payslipTotal,
        repayReallocTotal = accountingPeriodsRowResponse.repayReallocTotal,
        adjustmentTotal = accountingPeriodsRowResponse.adjustmentTotal,
        clericalCalculationFlag = clericalCalculationFlag,
        taxIsDisplayNeededFlag = displayNeeded.taxIsDisplayNeededFlag,
        interestIsDisplayNeededFlag = displayNeeded.interestIsDisplayNeededFlag,
        paymentIsDisplayNeededFlag = displayNeeded.paymentIsDisplayNeededFlag,
        repayReallocIsDisplayNeededFlag = displayNeeded.repayReallocIsDisplayNeededFlag
      )

    def getAccPeriodOverviewForNoTaxChargePresent(
      accountingPeriodsRowResponse: AccountingPeriodsRowResponse,
      displayNeeded: DisplayNeeded,
      clericalCalculationFlag: Boolean
    ): AccountingPeriodOverview =
      AccountingPeriodOverview(
        accountingPeriod = accountingPeriodsRowResponse.accountingPeriod,
        apStartDate = accountingPeriodsRowResponse.apStartDate,
        apEndDate = accountingPeriodsRowResponse.apEndDate,
        apStatus = accountingPeriodsRowResponse.apStatus,
        taxChargePresent = accountingPeriodsRowResponse.taxChargePresent,
        clericalIntSig = accountingPeriodsRowResponse.clericalIntSig,
        creditDebitInterestInd = accountingPeriodsRowResponse.creditDebitInterestInd,
        taxTotal = accountingPeriodsRowResponse.taxTotal,
        interestTotal = accountingPeriodsRowResponse.interestTotal,
        penaltyTotal = accountingPeriodsRowResponse.penaltyTotal,
        payslipTotal = accountingPeriodsRowResponse.payslipTotal,
        repayReallocTotal = accountingPeriodsRowResponse.repayReallocTotal,
        adjustmentTotal = accountingPeriodsRowResponse.adjustmentTotal,
        clericalCalculationFlag = clericalCalculationFlag,
        taxIsDisplayNeededFlag = displayNeeded.taxIsDisplayNeededFlag,
        interestIsDisplayNeededFlag = displayNeeded.interestIsDisplayNeededFlag,
        paymentIsDisplayNeededFlag = displayNeeded.paymentIsDisplayNeededFlag,
        repayReallocIsDisplayNeededFlag = displayNeeded.repayReallocIsDisplayNeededFlag
      )
  }

  "AccountingPeriodOverviewService.getAccountingPeriodOverview" should {
    "when taxChargePresent = true"  should {
      "call AccountingPeriodDetailsService and return Right(AccountingPeriodOverview) for AccountingPeriodsRowResponse" in new Fixture {
        when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(Right(accPeriodRowResponseWithTaxChargePresent)))
        when(mockAccPeriodDetailsService.getAccountingDetails(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(accountingPeriodDetails))
        when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(displayNeededResponse))

        val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForTaxChargePresent(
          accPeriodRowResponseWithTaxChargePresent,
          accountingPeriodDetails,
          displayNeededResponse,
          false
        )

        val result: Either[MissingAccountingPeriodError, AccountingPeriodOverview] =
          service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue

        result shouldBe Right(expectedAccPeriodOverviewResponse)

        verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])
        verify(mockDisplayNeededService, times(1)).getDisplayNeeded(any(), any())(any[HeaderCarrier])
        verify(mockAccPeriodDetailsService, times(1)).getAccountingDetails(any(), any())(any[HeaderCarrier])

        verifyNoMoreInteractions(mockAccPeriodService)
        verifyNoMoreInteractions(mockDisplayNeededService)
        verifyNoMoreInteractions(mockAccPeriodDetailsService)

      }
      "propagate errors and exceptions from AccountingPeriodDetailsService" in new Fixture {
        when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(Right(accPeriodRowResponseWithTaxChargePresent)))
        when(mockAccPeriodDetailsService.getAccountingDetails(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.failed(new RuntimeException("Boom")))
        when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(displayNeededResponse))

        val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForTaxChargePresent(
          accPeriodRowResponseWithTaxChargePresent,
          accountingPeriodDetails,
          displayNeededResponse,
          false
        )

        val ex: RuntimeException = intercept[RuntimeException] {
          service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue
        }

        ex.getMessage should include("Boom")

        verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])
        verify(mockDisplayNeededService, times(1)).getDisplayNeeded(any(), any())(any[HeaderCarrier])
        verify(mockAccPeriodDetailsService, times(1)).getAccountingDetails(any(), any())(any[HeaderCarrier])

        verifyNoMoreInteractions(mockAccPeriodService)
        verifyNoMoreInteractions(mockDisplayNeededService)
        verifyNoMoreInteractions(mockAccPeriodDetailsService)

      }
    }
    "when taxChargePresent = false" should {
      "return Right(AccountingPeriodOverview) when taxChargePresent = false for AccountingPeriodsRowResponse" in new Fixture {
        when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(Right(accPeriodRowResponseWithNoChargePresent)))
        when(mockAccPeriodDetailsService.getAccountingDetails(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(accountingPeriodDetails))
        when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(displayNeededResponse))

        val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForNoTaxChargePresent(
          accPeriodRowResponseWithNoChargePresent,
          displayNeededResponse,
          false
        )

        val result: Either[MissingAccountingPeriodError, AccountingPeriodOverview] =
          service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue

        result shouldBe Right(expectedAccPeriodOverviewResponse)

        verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])
        verify(mockDisplayNeededService, times(1)).getDisplayNeeded(any(), any())(any[HeaderCarrier])

        verifyNoMoreInteractions(mockAccPeriodService)
        verifyNoMoreInteractions(mockDisplayNeededService)
        verifyNoInteractions(mockAccPeriodDetailsService)
      }
    }
    "return Right(AccountingPeriodOverview) after evaluating clericalCalculationFlag = true" in new Fixture {
      when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(accPeriodRowResponseForTrueClericalCalculationFlag)))
      when(mockAccPeriodDetailsService.getAccountingDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(accountingPeriodDetails))
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(displayNeededResponse))

      val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForTaxChargePresent(
        accPeriodRowResponseForTrueClericalCalculationFlag,
        accountingPeriodDetails,
        displayNeededResponse,
        true
      )

      val result: Either[MissingAccountingPeriodError, AccountingPeriodOverview] =
        service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue

      result shouldBe Right(expectedAccPeriodOverviewResponse)

      verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])
      verify(mockDisplayNeededService, times(1)).getDisplayNeeded(any(), any())(any[HeaderCarrier])
      verify(mockAccPeriodDetailsService, times(1)).getAccountingDetails(any(), any())(any[HeaderCarrier])

      verifyNoMoreInteractions(mockAccPeriodService)
      verifyNoMoreInteractions(mockDisplayNeededService)
      verifyNoMoreInteractions(mockAccPeriodDetailsService)
    }
    "return Right(AccountingPeriodOverview) after evaluating clericalCalculationFlag = false" in new Fixture {
      when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(accPeriodRowResponseForFalseClericalCalculationFlag)))
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(displayNeededResponse))

      val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForNoTaxChargePresent(
        accPeriodRowResponseForFalseClericalCalculationFlag,
        displayNeededResponse,
        false
      )

      val result: Either[MissingAccountingPeriodError, AccountingPeriodOverview] =
        service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue

      result shouldBe Right(expectedAccPeriodOverviewResponse)

      verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])
      verify(mockDisplayNeededService, times(1)).getDisplayNeeded(any(), any())(any[HeaderCarrier])

      verifyNoMoreInteractions(mockAccPeriodService)
      verifyNoMoreInteractions(mockDisplayNeededService)
      verifyNoInteractions(mockAccPeriodDetailsService)
    }
    "return Left(MissingAccountingPeriodError) when AccountingPeriodServiceReturns Left(MissingAccountingPeriodError)" in new Fixture {
      when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Left(MissingAccountingPeriodError("Cannot find AccountingPeriod"))))

      val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForNoTaxChargePresent(
        accPeriodRowResponseWithNoChargePresent,
        displayNeededResponse,
        false
      )

      val result: Either[MissingAccountingPeriodError, AccountingPeriodOverview] =
        service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue

      result shouldBe Left(MissingAccountingPeriodError("Cannot find AccountingPeriod"))

      verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])

      verifyNoMoreInteractions(mockAccPeriodService)
      verifyNoInteractions(mockDisplayNeededService)
      verifyNoInteractions(mockAccPeriodDetailsService)
    }
    "propagate errors and exceptions from AccountingPeriodService" in new Fixture {
      when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(accPeriodRowResponseWithTaxChargePresent)))
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("Boom")))

      val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForTaxChargePresent(
        accPeriodRowResponseWithTaxChargePresent,
        accountingPeriodDetails,
        displayNeededResponse,
        false
      )

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue
      }

      ex.getMessage should include("Boom")

      verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])
      verify(mockDisplayNeededService, times(1)).getDisplayNeeded(any(), any())(any[HeaderCarrier])

      verifyNoMoreInteractions(mockAccPeriodService)
      verifyNoMoreInteractions(mockDisplayNeededService)
      verifyNoInteractions(mockAccPeriodDetailsService)
    }
    "propagate errors and exceptions from DisplayNeededService" in new Fixture {
      when(mockAccPeriodService.getAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("Boom")))

      val expectedAccPeriodOverviewResponse: AccountingPeriodOverview = getAccPeriodOverviewForTaxChargePresent(
        accPeriodRowResponseWithTaxChargePresent,
        accountingPeriodDetails,
        displayNeededResponse,
        false
      )

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getAccountingPeriodOverview(taxRef, accPeriod).futureValue
      }

      ex.getMessage should include("Boom")

      verify(mockAccPeriodService, times(1)).getAccountingPeriod(any(), any())(any[HeaderCarrier])

      verifyNoMoreInteractions(mockAccPeriodService)
      verifyNoInteractions(mockDisplayNeededService)
      verifyNoInteractions(mockAccPeriodDetailsService)
    }
  }
}
