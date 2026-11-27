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

package uk.gov.hmrc.corporationtax.controllers.overarching

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.controllers.base.SpecBase
import uk.gov.hmrc.corporationtax.helpers.overarching.EnqueueHelper
import uk.gov.hmrc.corporationtax.services.overarching.EnqueueLongService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class EnqueueLongControllerSpec extends SpecBase with EnqueueHelper {

  private trait Setup {
    val mockService: EnqueueLongService = mock[EnqueueLongService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/enqueue-long")

    val controller =
      new EnqueueLongController(mockService, Helpers.stubControllerComponents())
  }

  "GET /enqueue-long" should {

    "return 200 and a successful response of enqueue long " in new Setup {
      when(
        mockService.getEnqueueLong(any(), any(), any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(enqueue))

      val result: Future[Result] = controller.getEnqueueLong(
        12345,
        "sender1",
        "queue1",
        "replyQueue1",
        "correlationId1",
        "filter1",
        "key1",
        1,
        1010
      )(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(enqueue)

      verify(mockService)
        .getEnqueueLong(
          eqTo(12345L),
          eqTo("sender1"),
          eqTo("queue1"),
          eqTo("replyQueue1"),
          eqTo("correlationId1"),
          eqTo("filter1"),
          eqTo("key1"),
          eqTo(1),
          eqTo(1010)
        )(
          any[HeaderCarrier]
        )
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(
        mockService.getEnqueueLong(any(), any(), any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier])
      )
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] =
        controller.getEnqueueLong(
          65789,
          "sender2",
          "queue2",
          "replyQueue2",
          "correlationId2",
          "filter2",
          "key2",
          2,
          2020
        )(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve enqueue long"

      verify(mockService)
        .getEnqueueLong(
          eqTo(65789L),
          eqTo("sender2"),
          eqTo("queue2"),
          eqTo("replyQueue2"),
          eqTo("correlationId2"),
          eqTo("filter2"),
          eqTo("key2"),
          eqTo(2),
          eqTo(2020)
        )(
          any[HeaderCarrier]
        )
    }
  }
}
