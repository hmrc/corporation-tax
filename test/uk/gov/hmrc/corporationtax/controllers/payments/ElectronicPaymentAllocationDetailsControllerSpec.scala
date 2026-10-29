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
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.payments.ElectronicPaymentAllocationDetailsHelper
import uk.gov.hmrc.corporationtax.services.payments.ElectronicPaymentAllocationDetailsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ElectronicPaymentAllocationDetailsControllerSpec
    extends AnyWordSpec
    with Matchers
    with ElectronicPaymentAllocationDetailsHelper {

  private trait Setup {
    val mockService: ElectronicPaymentAllocationDetailsService = mock[ElectronicPaymentAllocationDetailsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/electronic-payment-allocation-details")
    val controller  =
      new ElectronicPaymentAllocationDetailsController(mockService, Helpers.stubControllerComponents())
  }

  "GET /electronic-payment-allocation-details" should {

    "return 200 and a successful response with the transformed electronic payment allocation details" in new Setup {
      when(mockService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(ePaymentAllocationDetails))

      val result: Future[Result] = controller.getPayAllocationElectronicDtls(
        1L,
        2L,
        3L
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(ePaymentAllocationDetails)

      verify(mockService).getPayAllocationElectronicDtls(eqTo(1L), eqTo(2L), eqTo(3L))(
        any[HeaderCarrier]
      )
    }

    "return 200 and a successful response with multiple transformed electronic payment allocation details" in new Setup {
      when(mockService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleEPaymentAllocationDetails))

      val result: Future[Result] = controller.getPayAllocationElectronicDtls(
        1L,
        2L,
        3L
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multipleEPaymentAllocationDetails)

      verify(mockService).getPayAllocationElectronicDtls(eqTo(1L), eqTo(2L), eqTo(3L))(
        any[HeaderCarrier]
      )
    }

    "return 200 and a successful response with empty electronic payment allocation details" in new Setup {
      when(mockService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyEPaymentAllocationDetails))

      val result: Future[Result] = controller.getPayAllocationElectronicDtls(
        1L,
        2L,
        3L
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyEPaymentAllocationDetails)

      verify(mockService).getPayAllocationElectronicDtls(eqTo(1L), eqTo(2L), eqTo(3L))(
        any[HeaderCarrier]
      )
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getPayAllocationElectronicDtls(
        1L,
        2L,
        3L
      )(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve electronic payment allocation details"

      verify(mockService).getPayAllocationElectronicDtls(eqTo(1L), eqTo(2L), eqTo(3L))(
        any[HeaderCarrier]
      )
    }
  }
}
