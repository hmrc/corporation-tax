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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.models.StatuteRuleResponse
import uk.gov.hmrc.corporationtax.services.StatuteRuleService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.helpers.StatuteRuleHelper

import scala.concurrent.{ExecutionContext, Future}

class StatuteRuleControllerSpec extends AnyWordSpec with Matchers with StatuteRuleHelper {

  private trait Fixture {
    val mockStatuteRuleService: StatuteRuleService = mock[StatuteRuleService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val fakeRequest     = FakeRequest("GET", "/")
    val fakePostRequest = FakeRequest("GET", "/WrongUrl")
    val controller      = new StatuteRuleController(Helpers.stubControllerComponents(), mockStatuteRuleService)
  }

  "GET /" should {

    "return 200: OK" in new Fixture {
      when(mockStatuteRuleService.getStatueRule(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Some(StatuteRuleResponse(defaultResponseRecord))))

      val result: Future[Result] = controller
        .getStatueRule("C", "1991-04-19", "1992-06-20")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(StatuteRuleResponse(defaultResponseRecord))

      verify(mockStatuteRuleService)
        .getStatueRule(eqTo("C"), eqTo("1991-04-19"), eqTo("1992-06-20"))(any[HeaderCarrier])
    }

    "return 404: NotFound" in new Fixture {
      when(mockStatuteRuleService.getStatueRule(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(None))

      val result: Future[Result] = controller
        .getStatueRule("C", "1991-04-19", "1992-06-20")(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND

      verify(mockStatuteRuleService)
        .getStatueRule(eqTo("C"), eqTo("1991-04-19"), eqTo("1992-06-20"))(any[HeaderCarrier])
    }

    "return 500: INTERNAL_SERVER_ERROR: server level error handling" in new Fixture {
      when(mockStatuteRuleService.getStatueRule(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller
        .getStatueRule("C", "1991-04-19", "1992-06-20")(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve StatueRule"

      verify(mockStatuteRuleService)
        .getStatueRule(eqTo("C"), eqTo("1991-04-19"), eqTo("1992-06-20"))(any[HeaderCarrier])
    }

    "return 500: INTERNAL_SERVER_ERROR: wrong query params" in new Fixture {
      when(mockStatuteRuleService.getStatueRule(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Some(StatuteRuleResponse(defaultResponseRecord))))

      val result: Future[Result] = controller
        .getStatueRule("C", "1991-04", "1992-06")(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Error input parameters provided"

      verify(mockStatuteRuleService, times(0))
        .getStatueRule(eqTo("C"), eqTo("1991-04"), eqTo("1992-06"))(any[HeaderCarrier])
    }

  }

}
