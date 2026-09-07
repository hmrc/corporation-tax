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
import uk.gov.hmrc.corporationtax.helpers.DisplayNeededHelper
import uk.gov.hmrc.corporationtax.services.DisplayNeededService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DisplayNeededControllerSpec extends AnyWordSpec with Matchers with DisplayNeededHelper {

  private trait Setup {
    val mockDisplayNeededService: DisplayNeededService = mock[DisplayNeededService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/display-needed")
    val controller  =
      new DisplayNeededController(Helpers.stubControllerComponents(), mockDisplayNeededService)
  }

  "GET /display-needed" should {

    "return 200 and display needed flags all set to false" in new Setup {
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(displayNeededAllFalse))

      val result: Future[Result] = controller.getDisplayNeeded(10L, 1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(displayNeededAllFalse)

      verify(mockDisplayNeededService).getDisplayNeeded(eqTo(10L), eqTo(1L))(any[HeaderCarrier])
    }

    "return 200 and display needed flags all set to true" in new Setup {
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(displayNeededAllTrue))

      val result: Future[Result] = controller.getDisplayNeeded(20L, 1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(displayNeededAllTrue)

      verify(mockDisplayNeededService).getDisplayNeeded(eqTo(20L), eqTo(1L))(any[HeaderCarrier])
    }

    "return 200 and display needed some flags set to true and false" in new Setup {
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(displayNeededMixed))

      val result: Future[Result] = controller.getDisplayNeeded(30L, 1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(displayNeededMixed)

      verify(mockDisplayNeededService).getDisplayNeeded(eqTo(30L), eqTo(1L))(any[HeaderCarrier])
    }

    "return Eerror message" in new Setup {
      when(mockDisplayNeededService.getDisplayNeeded(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getDisplayNeeded(999L, 1L)(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve display needed"

      verify(mockDisplayNeededService).getDisplayNeeded(eqTo(999L), eqTo(1L))(any[HeaderCarrier])
    }

  }
}
