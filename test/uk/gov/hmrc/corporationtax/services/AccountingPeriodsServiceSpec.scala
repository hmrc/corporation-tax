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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodsConnector
import uk.gov.hmrc.corporationtax.helpers.{AccountingPeriodsHelper, PayRepayReallocationHelper}
import uk.gov.hmrc.corporationtax.models.{AccountingPeriods, RdsAccountingPeriod}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodsServiceSpec
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

    val mockRds: AccountingPeriodsConnector = mock[AccountingPeriodsConnector]
    val service                             = new AccountingPeriodsService(mockRds)
    val taxReferenceNumber: Long            = 8674L
    val zeroValue: BigDecimal               = BigDecimal(0.00)
  }

  "AccountingPeriodsService.getAccountingPeriod" should {
    "return RdsAccountingPeriod and transform to AccountingPeriods and replace paymentTotal with payslipTotal and RepayReallocationTotal with repayReallocTotal" in new BaseSetup {
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

      val accPeriodResponse: AccountingPeriods =
        accountingPeriods(
          taxTotal = BigDecimal(1000.88),
          interestTotal = BigDecimal(9875.89),
          penaltyTotal = BigDecimal(-100058.25),
          payslipTotal = BigDecimal(0.00),
          repayReallocTotal = BigDecimal(34534342.36),
          adjustmentTotal = BigDecimal(-1200.00),
          taxChargePresent = false,
          clericalIntSig = true,
          creditDebitInterestInd = false
        )

      when(mockRds.getAccountingPeriods(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsAccountingPeriodResponse))

      val result: AccountingPeriods =
        service.getAccountingPeriod(taxReferenceNumber).futureValue

      result shouldBe accPeriodResponse

      verify(mockRds, times(1)).getAccountingPeriods(any())(any[HeaderCarrier])
    }
  }

  "return empty RdsAccountingPeriod and transform to empty AccountingPeriods" in new BaseSetup {
    val rdsAccountingPeriodResponse: RdsAccountingPeriod = emptyRdsAccountingPeriods

    val accPeriodResponse: AccountingPeriods = emptyAccountingPeriods
    when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
      .thenReturn(Future.successful(rdsAccountingPeriodResponse))

    val result: AccountingPeriods =
      service.getAccountingPeriod(taxReferenceNumber).futureValue

    result shouldBe accPeriodResponse

    verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
  }
  "propagate exception and errors from connector " in new BaseSetup {
    val exception = new RuntimeException("Error in the downstream services")

    when(mockRds.getAccountingPeriods(any())(any[HeaderCarrier]))
      .thenReturn(Future.failed(exception))

    val ex: RuntimeException = intercept[RuntimeException] {
      service.getAccountingPeriod(taxReferenceNumber).futureValue
    }

    ex.getMessage should include("Error in the downstream services")

    verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
  }

}
