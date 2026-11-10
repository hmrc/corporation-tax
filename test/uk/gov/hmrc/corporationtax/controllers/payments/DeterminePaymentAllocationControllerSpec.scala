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
import uk.gov.hmrc.corporationtax.controllers.actions.FakeIdentifierAction
import uk.gov.hmrc.corporationtax.helpers.payments.DeterminePaymentAllocationHelper
import uk.gov.hmrc.corporationtax.queryParams.PaymentSearchResultQueryParams
import uk.gov.hmrc.corporationtax.services.payments.DeterminePaymentAllocationService
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class DeterminePaymentAllocationControllerSpec extends AnyWordSpec with Matchers with DeterminePaymentAllocationHelper {

  private trait Setup {
    val mockService: DeterminePaymentAllocationService = mock[DeterminePaymentAllocationService]
    val mockAuth                                       = mock[AuthConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    val fakeIdentifierAction          = new FakeIdentifierAction(cc.parsers)

    val fakeRequest = FakeRequest("GET", "/determine-payment-allocation")
    val controller  =
      new DeterminePaymentAllocationController(mockService, fakeIdentifierAction, cc)
  }

  "GET /determine-payment-allocation" should {

    "return 200 and a successful response with one item list of payment allocations" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(paymentAllocation))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(
          Some(LocalDate.of(2026, 7, 24)),
          Some(LocalDate.of(2026, 7, 24)),
          BigDecimal(1000)
        )
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(paymentAllocation)

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

    "return 200 and a successful response with multiple item list of payments allocations" in new Setup {
      when(mockService.getPaymentType(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multiPaymentAllocation))

      val result: Future[Result] = controller.getPaymentType(
        1L,
        PaymentSearchResultQueryParams(
          Some(LocalDate.of(2026, 7, 24)),
          Some(LocalDate.of(2026, 7, 24)),
          BigDecimal(1000)
        )
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multiPaymentAllocation)

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
        PaymentSearchResultQueryParams(
          Some(LocalDate.of(2026, 7, 24)),
          Some(LocalDate.of(2026, 7, 24)),
          BigDecimal(1000)
        )
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
