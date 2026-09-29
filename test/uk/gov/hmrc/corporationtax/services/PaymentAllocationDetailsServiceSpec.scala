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
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.gpa.PaymentAllocationDetailsConnector
import uk.gov.hmrc.corporationtax.helpers.gpa.PaymentAllocationDetailsHelper
import uk.gov.hmrc.corporationtax.models.gpa.PaymentAllocationDetails
import uk.gov.hmrc.corporationtax.services.gpa.PaymentAllocationDetailsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationDetailsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with PaymentAllocationDetailsHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: PaymentAllocationDetailsConnector = mock[PaymentAllocationDetailsConnector]
    val service                                          = new PaymentAllocationDetailsService(mockConnector)
  }

  "getGPAPaymentAllocationDetail" should {

    "delegate to connector and successfully return transformed payment allocation details with negated amount" in new Setup {
      when(mockConnector.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsFullPaymentAllocationDetails))

      val result = service.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L).futureValue

      result shouldBe fullPaymentAllocationDetails

      verify(mockConnector).getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L)
    }

    "delegate to connector and successfully return transformed payment allocation details with multiple allocation details" in new Setup {
      when(mockConnector.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsPaymentAllocationDetailsWithMultipleAllocationDetails))

      val result = service.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L).futureValue

      result shouldBe paymentAllocationDetailsWithMultipleAllocationDetails

      verify(mockConnector).getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L)
    }

    "delegate to connector and successfully return transformed payment allocation details with mandatory fields" in new Setup {
      when(mockConnector.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsMinimalPaymentAllocationDetails))

      val result = service.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L).futureValue

      result shouldBe minimalPaymentAllocationDetails

      verify(mockConnector).getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L)
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(mockConnector.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        service.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L).futureValue
      }

      ex.getMessage should include("error")

      verify(mockConnector).getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5L, 6L)
    }
  }

}
