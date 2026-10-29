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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.payments.AllAccountingPeriodsConnector
import uk.gov.hmrc.corporationtax.helpers.payments.AllAccountingPeriodsHelper
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AllAccountingPeriodsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with AllAccountingPeriodsHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: AllAccountingPeriodsConnector = mock[AllAccountingPeriodsConnector]
    val service                                      = new AllAccountingPeriodsService(mockConnector)
  }

  "getAccountingPeriodList" should {

    "delegate to connector and successfully return transformed all accounting periods" in new Setup {
      when(mockConnector.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsAllAccountingPeriods))

      val result = service.getAccountingPeriodList(1L).futureValue

      result shouldBe allAccountingPeriods

      verify(mockConnector).getAccountingPeriodList(1L)
    }

    "delegate to connector and successfully return multiple of items of transformed all accounting periods" in new Setup {
      when(mockConnector.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsMultipleAllAccountingPeriods))

      val result = service.getAccountingPeriodList(1L).futureValue

      result shouldBe multipleAllAccountingPeriods

      verify(mockConnector).getAccountingPeriodList(1L)
    }

    "delegate to connector and successfully return empty list of transformed all accounting periods" in new Setup {
      when(mockConnector.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsEmptyAllAccountingPeriods))

      val result = service.getAccountingPeriodList(1L).futureValue

      result shouldBe emptyAllAccountingPeriods

      verify(mockConnector).getAccountingPeriodList(1L)
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(mockConnector.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        service.getAccountingPeriodList(1L).futureValue
      }

      ex.getMessage should include("error")

      verify(mockConnector).getAccountingPeriodList(1L)
    }
  }

}
