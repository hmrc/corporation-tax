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

package uk.gov.hmrc.corporationtax.controllers.payments

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentAllocationChequeDetailsHelper
import uk.gov.hmrc.corporationtax.services.payments.PaymentAllocationChequeDetailsService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationChequeDetailsControllerSpec
    extends AnyWordSpec
    with Matchers
    with PaymentAllocationChequeDetailsHelper {

  private trait Fixture {
    val mockService: PaymentAllocationChequeDetailsService = mock[PaymentAllocationChequeDetailsService]
    private val cc: ControllerComponents                   = stubControllerComponents()

    implicit val ec: ExecutionContext                    = cc.executionContext
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    val controller                                       = new PaymentAllocationChequeDetailsController(cc, mockService)
    val taxRef: Long                                     = 1234567L
    val chequeId: Long                                   = 3456L

  }

  "GET /reallocationFromAccPeriod " should {

    "return 200: OK" in new Fixture {
      when(mockService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(fullPaymentAllocationChequeDetails))

      val result: Future[Result] = controller.getPaymentAllocationChequeDetails(taxRef, chequeId)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(fullPaymentAllocationChequeDetails)

      verify(mockService).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])
    }
    "return 200: OK for empty response " in new Fixture {
      when(mockService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyPaymentAllocationChequeDetails))

      val result: Future[Result] = controller.getPaymentAllocationChequeDetails(taxRef, chequeId)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyPaymentAllocationChequeDetails)

      verify(mockService).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Fixture {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.getPaymentAllocationChequeDetails(taxRef, chequeId)(fakeRequest)

      status(result) shouldBe BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getPaymentAllocationChequeDetails(taxRef, chequeId)(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve paymentAllocationChequeDetails"

      verify(mockService).getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier])
    }

  }

}
