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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentAllocationChequeDetailsRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentAllocationChequeDetailsHelper
import uk.gov.hmrc.corporationtax.models.payments.PaymentAllocationChequeDetails
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationChequeDetailsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with PaymentAllocationChequeDetailsHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: PaymentAllocationChequeDetailsRdsProxyConnector = mock[PaymentAllocationChequeDetailsRdsProxyConnector]
    val service                                                  = new PaymentAllocationChequeDetailsService(mockRds)
    val taxRef: Long                                             = 1234567L
    val chequeId: Long                                           = 3456L
  }

  "PaymentAllocationChequeDetailsService.getPaymentAllocationChequeDetails" should {

    "delegate to connector and successfully return RdsPaymentAllocationChequeDetails with amount = None  and transform to PaymentAllocationChequeDetails with amount = 0.00" in new BaseSetup {
      when(mockRds.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(fullRdsPaymentAllocationChequeDetails))

      val result: PaymentAllocationChequeDetails =
        service.getPaymentAllocationChequeDetails(taxRef, chequeId).futureValue

      result shouldBe fullPaymentAllocationChequeDetails

      verify(mockRds).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])

      verify(mockRds, times(1)).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])

    }
    "delegate to connector and successfully return empty PaymentAllocationChequeDetails  when RdsPaymentAllocationChequeDetails is empty" in new BaseSetup {
      when(mockRds.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyRdsPaymentAllocationChequeDetails))

      val result: PaymentAllocationChequeDetails =
        service.getPaymentAllocationChequeDetails(taxRef, chequeId).futureValue

      result shouldBe emptyPaymentAllocationChequeDetails

      verify(mockRds).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])

      verify(mockRds, times(1)).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])

    }
    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getPaymentAllocationChequeDetails(taxRef, chequeId).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])

    }
  }

}
