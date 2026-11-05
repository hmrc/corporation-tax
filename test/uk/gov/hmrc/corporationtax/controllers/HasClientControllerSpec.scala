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
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.models.HasClientResponse
import uk.gov.hmrc.corporationtax.services.HasClientService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HasClientControllerSpec extends AnyWordSpec with Matchers {

  private trait Setup {
    val mockHasClientService: HasClientService = mock[HasClientService]

    private val cc                    = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest          = FakeRequest("GET", "/has-client")
    val controller           = new HasClientController(Helpers.stubControllerComponents(), mockHasClientService)
    val utr: Long            = 1234567890L
    val irAgentId: String    = "AGENT123"
    val credentialId: String = "0903840830080480"
  }

  "GET /has-client" should {

    "return 200 and true when the client exists" in new Setup {
      when(mockHasClientService.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(HasClientResponse(status = true)))

      val result: Future[Result] =
        controller.getHasClient(utr, irAgentId, credentialId)(fakeRequest)

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.obj("status" -> true)

      verify(mockHasClientService).hasClient(eqTo(utr), eqTo(irAgentId), eqTo(credentialId))(any[HeaderCarrier])
    }

    "return 200 and false when the client does not exist" in new Setup {
      when(mockHasClientService.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(HasClientResponse(status = false)))

      val result: Future[Result] =
        controller.getHasClient(utr, irAgentId, credentialId)(fakeRequest)

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.obj("status" -> false)

      verify(mockHasClientService).hasClient(eqTo(utr), eqTo(irAgentId), eqTo(credentialId))(any[HeaderCarrier])
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Setup {
      when(mockHasClientService.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] =
        controller.getHasClient(utr, irAgentId, credentialId)(fakeRequest)

      status(result) mustBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] mustBe "Failed to retrieve Has Client"

      verify(mockHasClientService).hasClient(eqTo(utr), eqTo(irAgentId), eqTo(credentialId))(any[HeaderCarrier])
    }
  }
}
