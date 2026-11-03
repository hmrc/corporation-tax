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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentPostingsHelper
import uk.gov.hmrc.corporationtax.services.payments.PaymentPostingsService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class PaymentPostingsControllerSpec extends AnyWordSpec with Matchers with PaymentPostingsHelper {

  private trait Fixture {
    val mockService: PaymentPostingsService = mock[PaymentPostingsService]
    private val cc: ControllerComponents    = stubControllerComponents()

    implicit val ec: ExecutionContext                    = cc.executionContext
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/payment-postings")
    val controller                                       = new PaymentPostingsController(cc, mockService)
    val taxReferenceNumber: Long                         = 1234567L

  }

  "GET /payment-postings " should {

    "return 200: OK with single elements in Payment Postings List" in new Fixture {

      when(mockService.getPaymentPostings(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(singlePaymentPosting))

      val result: Future[Result] = controller.getPaymentPostings(taxReferenceNumber)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(singlePaymentPosting)

      verify(mockService).getPaymentPostings(any())(any[HeaderCarrier])
    }

    "return 200: OK with multiple elements in Payment Postings List" in new Fixture {

      when(mockService.getPaymentPostings(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multiplePaymentPostings))

      val result: Future[Result] = controller.getPaymentPostings(taxReferenceNumber)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multiplePaymentPostings)

      verify(mockService).getPaymentPostings(any())(any[HeaderCarrier])
    }

    "return 200: OK for empty response in Payment Postings" in new Fixture {
      when(mockService.getPaymentPostings(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyPaymentPostings))

      val result: Future[Result] = controller.getPaymentPostings(taxReferenceNumber)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyPaymentPostings)

      verify(mockService).getPaymentPostings(any())(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Fixture {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockService.getPaymentPostings(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.getPaymentPostings(taxReferenceNumber)(fakeRequest)

      status(result) shouldBe BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockService.getPaymentPostings(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getPaymentPostings(taxReferenceNumber)(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve payment postings"

      verify(mockService).getPaymentPostings(eqTo(taxReferenceNumber))(any[HeaderCarrier])
    }

  }

}
