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

package uk.gov.hmrc.corporationtax.controllers

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
import uk.gov.hmrc.corporationtax.helpers.RepaymentsHelper
import uk.gov.hmrc.corporationtax.services.RepaymentsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class RepaymentsControllerSpec extends AnyWordSpec with Matchers with RepaymentsHelper {

  private trait Setup {
    val mockService: RepaymentsService = mock[RepaymentsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/repayments")
    val controller  =
      new RepaymentsController(mockService, Helpers.stubControllerComponents())
  }

  "GET /repayments" should {

    "return 200 and a successful response with the transformed repayments list with one item" in new Setup {
      when(mockService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(repaymentsWithOneItem))

      val result: Future[Result] = controller.getRepayments(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(repaymentsWithOneItem)

      verify(mockService).getRepayments(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 200 and a successful response with the transformed repayments list with multiple items" in new Setup {
      when(mockService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(repaymentsWithMultipleItems))

      val result: Future[Result] = controller.getRepayments(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(repaymentsWithMultipleItems)

      verify(mockService).getRepayments(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 200 and a successful response with an empty repayment list" in new Setup {
      when(mockService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyRepayments))

      val result: Future[Result] = controller.getRepayments(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyRepayments)

      verify(mockService).getRepayments(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getRepayments(3L, 4L)(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve the repayments list"

      verify(mockService).getRepayments(eqTo(3L), eqTo(4L))(any[HeaderCarrier])
    }
  }
}
