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
import play.api.mvc.Results.BadRequest
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.AdminRuleHelper
import uk.gov.hmrc.corporationtax.services.AdminRuleService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class AdministrationRuleControllerSpec extends AnyWordSpec with Matchers with AdminRuleHelper {

  private trait Fixture {
    val mockService: AdminRuleService    = mock[AdminRuleService]
    private val cc: ControllerComponents = stubControllerComponents()

    implicit val ec: ExecutionContext                    = cc.executionContext
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/administrative-rule")
    val controller                                       = new AdministrationRuleController(cc, mockService)

  }

  "GET /getAdminRule" should {

    "return 200: OK" in new Fixture {
      when(mockService.getAdminRule(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(example1adminRule))

      val result: Future[Result] = controller.getAdminRule("LAST-INST-PER-M")(fakeRequest)

      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(example1adminRule)

      verify(mockService).getAdminRule(eqTo("LAST-INST-PER-M"))(any[HeaderCarrier])
    }

    "return 400: BAD_REQUEST when the adminRule is empty" in new Fixture {
      when(mockService.getAdminRule(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(BadRequest(Json.obj("message" -> "AdminRule Key must be provided"))))

      val result: Future[Result] = controller.getAdminRule("")(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST

      (contentAsJson(result) \ "message").as[String] shouldBe "AdminRule Key must be provided"

    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Fixture {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockService.getAdminRule(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.getAdminRule("1234567")(fakeRequest)

      status(result) shouldBe BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockService.getAdminRule(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getAdminRule("12387")(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve adminRule"

      verify(mockService).getAdminRule(eqTo("12387"))(any[HeaderCarrier])
    }

  }

}
