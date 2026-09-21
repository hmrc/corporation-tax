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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{times, verify, verifyNoInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodsConnector
import uk.gov.hmrc.corporationtax.helpers.{AccountingPeriodsHelper, PayRepayReallocationHelper}
import uk.gov.hmrc.corporationtax.models.{
  AccountingPeriods, AccountingPeriodsRowResponse, MissingAccountingPeriodError, MissingDataError, RdsAccountingPeriod
}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with AccountingPeriodsHelper
    with PayRepayReallocationHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: AccountingPeriodsConnector              = mock[AccountingPeriodsConnector]
    val mockPayRepayService: PayRepayReallocationService = mock[PayRepayReallocationService]
    val service                                          = new AccountingPeriodService(mockRds, mockPayRepayService)
    val taxReferenceNumber: Long                         = 8674L
    val accPeriod: Long                                  = 202501L
    val zeroValue: BigDecimal                            = BigDecimal(0.00)
  }

  "AccountingPeriodService.getAccountingPeriod" should {
    "taxChargePresent = false and both interestTotal and penaltyTotal are zero" should {
      "return RdsAccountingPeriod and transform to Right(AccountingPeriodsRowResponse) for matching accountingPeriod by replacing paymentTotal with payslipTotal and RepayReallocationTotal with repayReallocTotal" in new BaseSetup {
        val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod()

        when(mockPayRepayService.getTotalAmounts(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(transformedPayRepayReallocation))

        val paymentTotal: BigDecimal           = transformedPayRepayReallocation.totalAmountPayments
        val repayReallocationTotal: BigDecimal = transformedPayRepayReallocation.totalAmountRepRfrRto

        val filteredAccountingPeriodsRowResponse: AccountingPeriodsRowResponse =
          AccountingPeriodsRowResponse(
            accountingPeriod = BigDecimal(202501),
            apStartDate = LocalDate.of(2025, 1, 1),
            apEndDate = LocalDate.of(2025, 12, 31),
            apStatus = "Open",
            taxChargePresent = false,
            clericalIntSig = false,
            creditDebitInterestInd = false,
            taxTotal = zeroValue,
            interestTotal = zeroValue,
            penaltyTotal = zeroValue,
            payslipTotal = paymentTotal,
            repayReallocTotal = repayReallocationTotal,
            adjustmentTotal = zeroValue
          )

        when(mockRds.getAccountingPeriods(any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(rdsAccountingPeriodResponse))

        val result: Either[MissingDataError, AccountingPeriodsRowResponse] =
          service.getAccountingPeriod(taxReferenceNumber, accPeriod).futureValue

        result shouldBe Right(filteredAccountingPeriodsRowResponse)

        verify(mockRds, times(1)).getAccountingPeriods(any())(any[HeaderCarrier])
        verify(mockPayRepayService, times(1)).getTotalAmounts(any(), any())(any[HeaderCarrier])
      }
      "return RdsAccountingPeriod and return Left(MissingAccountingPeriod) when the matching accountingPeriod" in new BaseSetup {
        val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod()

        val noMatchingAccPeriod: Long = 3456L

        when(mockPayRepayService.getTotalAmounts(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(transformedPayRepayReallocation))

        val accPeriodList: AccountingPeriods   = AccountingPeriods(accountingPeriods =
          List(
            AccountingPeriodsRowResponse(
              accountingPeriod = BigDecimal(202501),
              apStartDate = LocalDate.of(2025, 1, 1),
              apEndDate = LocalDate.of(2025, 12, 31),
              apStatus = "Open",
              taxChargePresent = false,
              clericalIntSig = false,
              creditDebitInterestInd = false,
              taxTotal = zeroValue,
              interestTotal = zeroValue,
              penaltyTotal = zeroValue,
              payslipTotal = BigDecimal(10.00),
              repayReallocTotal = BigDecimal(30.00),
              adjustmentTotal = zeroValue
            ),
            AccountingPeriodsRowResponse(
              accountingPeriod = BigDecimal(8765),
              apStartDate = LocalDate.of(2025, 1, 1),
              apEndDate = LocalDate.of(2025, 12, 31),
              apStatus = "Open",
              taxChargePresent = false,
              clericalIntSig = false,
              creditDebitInterestInd = false,
              taxTotal = zeroValue,
              interestTotal = zeroValue,
              penaltyTotal = zeroValue,
              payslipTotal = BigDecimal(10.00),
              repayReallocTotal = BigDecimal(30.00),
              adjustmentTotal = zeroValue
            ),
            AccountingPeriodsRowResponse(
              accountingPeriod = BigDecimal(1123),
              apStartDate = LocalDate.of(2025, 1, 1),
              apEndDate = LocalDate.of(2025, 12, 31),
              apStatus = "Open",
              taxChargePresent = false,
              clericalIntSig = false,
              creditDebitInterestInd = false,
              taxTotal = zeroValue,
              interestTotal = zeroValue,
              penaltyTotal = zeroValue,
              payslipTotal = BigDecimal(10.00),
              repayReallocTotal = BigDecimal(30.00),
              adjustmentTotal = zeroValue
            )
          )
        )
        val paymentTotal: BigDecimal           = transformedPayRepayReallocation.totalAmountPayments
        val repayReallocationTotal: BigDecimal = transformedPayRepayReallocation.totalAmountRepRfrRto

        when(mockRds.getAccountingPeriods(any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(rdsAccountingPeriodResponse))

        val result: Either[MissingDataError, AccountingPeriodsRowResponse] =
          service.getAccountingPeriod(taxReferenceNumber, noMatchingAccPeriod).futureValue

        result shouldBe Left(
          MissingAccountingPeriodError(
            s"Cannot find AccountingPeriod information for the accPeriod::$noMatchingAccPeriod"
          )
        )

        verify(mockRds, times(1)).getAccountingPeriods(any())(any[HeaderCarrier])
        verifyNoInteractions(mockPayRepayService)
      }
      "return RdsAccountingPeriod and transform to Right(AccountingPeriodsRowResponse) when all the fields are defined" in new BaseSetup {
        val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod(
          taxTotal = Some(BigDecimal(-1000.8765)),
          interestTotal = Some(BigDecimal(-9875.8895)),
          penaltyTotal = Some(BigDecimal(100058.254222)),
          payslipTotal = None,
          repayReallocTotal = Some(BigDecimal(-34534342.36262)),
          adjustmentTotal = Some(BigDecimal(1200.00)),
          taxChargePresent = Some("N"),
          clericalIntSig = Some("Y"),
          creditDebitInterestInd = Some("F")
        )

        val filteredAccountingPeriodsRowResponse: AccountingPeriodsRowResponse =
          AccountingPeriodsRowResponse(
            accountingPeriod = BigDecimal(accPeriod),
            apStartDate = LocalDate.of(2025, 1, 1),
            apEndDate = LocalDate.of(2025, 12, 31),
            apStatus = "Open",
            taxChargePresent = false,
            clericalIntSig = true,
            creditDebitInterestInd = false,
            taxTotal = BigDecimal(1000.88),
            interestTotal = BigDecimal(9875.89),
            penaltyTotal = BigDecimal(-100058.25),
            payslipTotal = zeroValue,
            repayReallocTotal = BigDecimal(34534342.36),
            adjustmentTotal = BigDecimal(-1200.00)
          )
        when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
          .thenReturn(Future.successful(rdsAccountingPeriodResponse))

        val result: Either[MissingDataError, AccountingPeriodsRowResponse] =
          service.getAccountingPeriod(taxReferenceNumber, accPeriod).futureValue

        result shouldBe Right(filteredAccountingPeriodsRowResponse)

        verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
      }
      "propagate exception and errors from PayRepayService when taxChargePresent = false" in new BaseSetup {
        val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod(
          Some(BigDecimal(100058.254222)),
          None,
          None,
          Some(BigDecimal(0)),
          Some(BigDecimal(-34534342.36262)),
          Some(BigDecimal(1200.00)),
          Some("N"),
          Some("N"),
          Some("Y")
        )
        val exception                                        = new RuntimeException("Error in the downstream services")

        when(mockRds.getAccountingPeriods(any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(rdsAccountingPeriodResponse))

        when(mockPayRepayService.getTotalAmounts(any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.failed(exception))

        val ex: RuntimeException = intercept[RuntimeException] {
          service.getAccountingPeriod(taxReferenceNumber, accPeriod).futureValue
        }

        ex.getMessage should include("Error in the downstream services")

        verify(mockRds, times(1)).getAccountingPeriods(any())(any[HeaderCarrier])
        verify(mockPayRepayService, times(1)).getTotalAmounts(any(), any())(any[HeaderCarrier])
      }
    }
    "when taxChargePresent = true "                                             should {
      "return RdsAccountingPeriod and transform to Right(AccountingPeriodsRowResponse) when all the fields are defined with all boolean fields transformed without any alteration of values" in new BaseSetup {
        val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod(
          taxTotal = Some(BigDecimal(-1000.8765)),
          interestTotal = Some(BigDecimal(-9875.8895)),
          penaltyTotal = Some(BigDecimal(100058.254222)),
          payslipTotal = Some(BigDecimal(200.00)),
          repayReallocTotal = Some(BigDecimal(-34534342.36262)),
          adjustmentTotal = Some(BigDecimal(1200.00)),
          taxChargePresent = Some("Y"),
          clericalIntSig = Some("Y"),
          creditDebitInterestInd = Some("F")
        )

        val filteredAccountingPeriodsRowResponse: AccountingPeriodsRowResponse =
          AccountingPeriodsRowResponse(
            accountingPeriod = BigDecimal(202501),
            apStartDate = LocalDate.of(2025, 1, 1),
            apEndDate = LocalDate.of(2025, 12, 31),
            apStatus = "Open",
            taxChargePresent = true,
            clericalIntSig = true,
            creditDebitInterestInd = false,
            taxTotal = BigDecimal(1000.88),
            interestTotal = BigDecimal(9875.89),
            penaltyTotal = BigDecimal(-100058.25),
            payslipTotal = BigDecimal(-200.00),
            repayReallocTotal = BigDecimal(34534342.36),
            adjustmentTotal = BigDecimal(-1200.00)
          )
        when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
          .thenReturn(Future.successful(rdsAccountingPeriodResponse))

        val result: Either[MissingDataError, AccountingPeriodsRowResponse] =
          service.getAccountingPeriod(taxReferenceNumber, accPeriod).futureValue

        result shouldBe Right(filteredAccountingPeriodsRowResponse)

        verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
        verifyNoInteractions(mockPayRepayService)
      }
      "return empty RdsAccountingPeriod and transform to empty Right(AccountingPeriodsRowResponse)" in new BaseSetup {
        val rdsAccountingPeriodResponse: RdsAccountingPeriod = emptyRdsAccountingPeriods

        val accPeriodResponse: AccountingPeriods = emptyAccountingPeriods
        when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
          .thenReturn(Future.successful(rdsAccountingPeriodResponse))

        val result: Either[MissingDataError, AccountingPeriodsRowResponse] =
          service.getAccountingPeriod(taxReferenceNumber, accPeriod).futureValue

        result shouldBe Left(
          MissingAccountingPeriodError(s"Cannot find AccountingPeriod information for the accPeriod::$accPeriod")
        )

        verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
        verifyNoInteractions(mockPayRepayService)
      }
    }
    "propagate exception and errors from connector " in new BaseSetup {
      val exception = new RuntimeException("Error in the downstream services")

      when(mockRds.getAccountingPeriods(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(exception))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getAccountingPeriod(taxReferenceNumber, accPeriod).futureValue
      }

      ex.getMessage should include("Error in the downstream services")

      verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
      verifyNoInteractions(mockPayRepayService)
    }
  }

}
