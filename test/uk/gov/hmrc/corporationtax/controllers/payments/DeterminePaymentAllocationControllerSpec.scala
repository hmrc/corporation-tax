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
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.corporationtax.controllers.actions.IdentifierAction
import uk.gov.hmrc.corporationtax.helpers.payments.DeterminePaymentAllocationHelper
import uk.gov.hmrc.corporationtax.queryParams.PaymentSearchResultQueryParams
import uk.gov.hmrc.corporationtax.services.payments.DeterminePaymentAllocationService
import uk.gov.hmrc.corporationtax.utils.DeterminePaymentAllocationSerializer.*
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class DeterminePaymentAllocationControllerSpec extends AnyWordSpec with Matchers with DeterminePaymentAllocationHelper {

  private trait Setup {
    val mockService: DeterminePaymentAllocationService = mock[DeterminePaymentAllocationService]
    val mockIdentify: IdentifierAction = mock[IdentifierAction]
    val mockAuth = mock[AuthConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/determine-payment-allocation")
    val controller  =
      new DeterminePaymentAllocationController(mockService, mockIdentify, cc)
  }

  "GET /determine-payment-allocation" should {

    "return 200 and a successful response with one item list of electronic payments" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicOneItemList))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(electronicOneItemList)

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response with one item list of cheque payments" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(chequeOneItemList))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(chequeOneItemList)

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response with multiple item list of electronic payments" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicMultiItemList))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(electronicMultiItemList)

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response with multiple item list of cheque payments" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(chequeMultiItemList))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(chequeMultiItemList)

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response with multiple item list of both electronic and cheque payments" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(mixedElecAndCheqList))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(mixedElecAndCheqList)

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response with empty list of both electronic and cheque payments" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyElecAndCheqList))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyElecAndCheqList)

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
      )(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error")
        .as[String] shouldBe "Failed to retrieve payment type for for determining payment allocation"

      verify(mockService)
        .getPaymentType(
          eqTo(1L),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(Some(LocalDate.of(2026, 7, 24))),
          eqTo(BigDecimal(1000))
        )(
          any[HeaderCarrier]
        )
    }
  }
}
