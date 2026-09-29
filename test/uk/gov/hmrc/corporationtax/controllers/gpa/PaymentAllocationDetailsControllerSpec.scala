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

package uk.gov.hmrc.corporationtax.controllers.gpa

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.gpa.PaymentAllocationDetailsHelper
import uk.gov.hmrc.corporationtax.services.gpa.PaymentAllocationDetailsService
import uk.gov.hmrc.corporationtax.queryParams.gpa.PaymentAllocationDetailsQueryParams
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationDetailsControllerSpec extends AnyWordSpec with Matchers with PaymentAllocationDetailsHelper {

  private trait Setup {
    val mockService: PaymentAllocationDetailsService = mock[PaymentAllocationDetailsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/gpa-payment-allocation-details")
    val controller  =
      new PaymentAllocationDetailsController(mockService, Helpers.stubControllerComponents())

    val startIndex = 5L
    val count      = 6L
  }

  "GET /gpa-payment-allocation-details" should {

    "return 200 and a successful response with the transformed payment allocation details" in new Setup {
      when(mockService.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(fullPaymentAllocationDetails))

      val result: Future[Result] = controller.getGPAPaymentAllocationDetail(
        1L,
        2L,
        3L,
        4L,
        PaymentAllocationDetailsQueryParams(startIndex, count)
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(fullPaymentAllocationDetails)

      verify(mockService).getGPAPaymentAllocationDetail(eqTo(1L), eqTo(2L), eqTo(3L), eqTo(4L), eqTo(5L), eqTo(6L))(
        any[HeaderCarrier]
      )
    }

    "return 200 and a successful response with the transformed payment allocation details with multiple allocation details" in new Setup {
      when(mockService.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(paymentAllocationDetailsWithMultipleAllocationDetails))

      val result: Future[Result] = controller.getGPAPaymentAllocationDetail(
        1L,
        2L,
        3L,
        4L,
        PaymentAllocationDetailsQueryParams(startIndex, count)
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(paymentAllocationDetailsWithMultipleAllocationDetails)

      verify(mockService).getGPAPaymentAllocationDetail(eqTo(1L), eqTo(2L), eqTo(3L), eqTo(4L), eqTo(5L), eqTo(6L))(
        any[HeaderCarrier]
      )
    }

    "return 200 and a successful response with the payment allocation details with minimal details" in new Setup {
      when(mockService.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(minimalPaymentAllocationDetails))

      val result: Future[Result] = controller.getGPAPaymentAllocationDetail(
        1L,
        2L,
        3L,
        4L,
        PaymentAllocationDetailsQueryParams(startIndex, count)
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(minimalPaymentAllocationDetails)

      verify(mockService).getGPAPaymentAllocationDetail(eqTo(1L), eqTo(2L), eqTo(3L), eqTo(4L), eqTo(5L), eqTo(6L))(
        any[HeaderCarrier]
      )
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getGPAPaymentAllocationDetail(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getGPAPaymentAllocationDetail(
        1L,
        2L,
        3L,
        4L,
        PaymentAllocationDetailsQueryParams(startIndex, count)
      )(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve payment allocation details"

      verify(mockService).getGPAPaymentAllocationDetail(eqTo(1L), eqTo(2L), eqTo(3L), eqTo(4L), eqTo(5L), eqTo(6L))(
        any[HeaderCarrier]
      )
    }
  }
}
