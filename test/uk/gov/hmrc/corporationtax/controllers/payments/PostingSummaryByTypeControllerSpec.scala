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
import uk.gov.hmrc.corporationtax.helpers.payments.PostingSummaryByTypeHelper
import uk.gov.hmrc.corporationtax.services.payments.PostingSummaryByTypeService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class PostingSummaryByTypeControllerSpec extends AnyWordSpec with Matchers with PostingSummaryByTypeHelper {

  private trait Fixture {
    val mockService: PostingSummaryByTypeService = mock[PostingSummaryByTypeService]
    private val cc: ControllerComponents         = stubControllerComponents()

    implicit val ec: ExecutionContext                    = cc.executionContext
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/posting-summary")
    val controller                                       = new PostingSummaryByTypeController(cc, mockService)
    val taxReferenceNumber: Long                         = 1234567L
    val postingType: String                              = "PYT"

  }

  "GET /posting-summary " should {

    "return 200: OK with multiple elements in Posting Summary List" in new Fixture {

      when(mockService.getPostingSummaryByType(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(postingSummaryByTypeResponseMultiple))

      val result: Future[Result] = controller
        .getPostingSummaryByType(
          taxReferenceNumber,
          postingType
        )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(postingSummaryByTypeResponseMultiple)

      verify(mockService).getPostingSummaryByType(any(), any())(any[HeaderCarrier])
    }
    "return 200: OK for empty response in Posting Summary" in new Fixture {
      when(mockService.getPostingSummaryByType(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(postingSummaryByTypeResponseEmpty))

      val result: Future[Result] =
        controller.getPostingSummaryByType(taxReferenceNumber, postingType)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(postingSummaryByTypeResponseEmpty)

      verify(mockService).getPostingSummaryByType(any(), any())(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Fixture {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockService.getPostingSummaryByType(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] =
        controller.getPostingSummaryByType(taxReferenceNumber, postingType)(fakeRequest)

      status(result) shouldBe BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockService.getPostingSummaryByType(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] =
        controller.getPostingSummaryByType(taxReferenceNumber, postingType)(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve postingSummaryByTypeResponse"

      verify(mockService).getPostingSummaryByType(any(), any())(any[HeaderCarrier])
    }

  }

}
