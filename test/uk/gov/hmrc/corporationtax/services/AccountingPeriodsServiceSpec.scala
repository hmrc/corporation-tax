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
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodsConnector
import uk.gov.hmrc.corporationtax.helpers.AccountingPeriodsHelper
import uk.gov.hmrc.corporationtax.models.{AccountingPeriods, RdsAccountingPeriod}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with AccountingPeriodsHelper {

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

    "return RdsAccountingPeriod and transform to AccountingPeriods when all amount and boolean fields are None" in new BaseSetup {
      val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod()

      val accPeriodResponse: AccountingPeriods =
        accountingPeriods(zeroValue, zeroValue, zeroValue, zeroValue, zeroValue, zeroValue, false, false, false)
      when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsAccountingPeriodResponse))

      val result: AccountingPeriods =
        service.getAccountingPeriod(taxReferenceNumber).futureValue

      result shouldBe accPeriodResponse

      verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
    }
    "return RdsAccountingPeriod and transform to AccountingPeriods when all the fields are defined" in new BaseSetup {
      val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod(
        Some(BigDecimal(-1000.8765)),
        Some(BigDecimal(-9875.8895)),
        Some(BigDecimal(100058.254222)),
        None,
        Some(BigDecimal(-34534342.36262)),
        Some(BigDecimal(1200.00)),
        Some("N"),
        Some("Y"),
        Some("F")
      )

      val accPeriodResponse: AccountingPeriods = accountingPeriods(
        BigDecimal(1000.88),
        BigDecimal(9875.89),
        BigDecimal(-100058.25),
        BigDecimal(0.00),
        BigDecimal(34534342.36),
        BigDecimal(-1200.00),
        false,
        true,
        false
      )
      when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsAccountingPeriodResponse))

      val result: AccountingPeriods =
        service.getAccountingPeriod(taxReferenceNumber).futureValue

      result shouldBe accPeriodResponse

      verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
    }
    "return RdsAccountingPeriod and transform to AccountingPeriods when all the fields are defined with all boolean fields transformed" in new BaseSetup {
      val rdsAccountingPeriodResponse: RdsAccountingPeriod = rdsAccountingPeriod(
        Some(BigDecimal(-1000.8765)),
        Some(BigDecimal(-9875.8895)),
        Some(BigDecimal(100058.254222)),
        Some(BigDecimal(0)),
        Some(BigDecimal(-34534342.36262)),
        Some(BigDecimal(1200.00)),
        Some("Y"),
        Some("N"),
        Some("Y")
      )

      val accPeriodResponse: AccountingPeriods = accountingPeriods(
        BigDecimal(1000.88),
        BigDecimal(9875.89),
        BigDecimal(-100058.25),
        BigDecimal(0.00),
        BigDecimal(34534342.36),
        BigDecimal(-1200.00),
        true,
        false,
        true
      )
      when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsAccountingPeriodResponse))

      val result: AccountingPeriods =
        service.getAccountingPeriod(taxReferenceNumber).futureValue

      result shouldBe accPeriodResponse

      verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
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

      when(mockRds.getAccountingPeriods(eqTo(taxReferenceNumber))(any[HeaderCarrier]))
        .thenReturn(Future.failed(exception))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getAccountingPeriod(taxReferenceNumber).futureValue
      }

      ex.getMessage should include("Error in the downstream services")

      verify(mockRds).getAccountingPeriods(taxReferenceNumber)(hc)
    }

  }

}
