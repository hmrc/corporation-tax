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

package uk.gov.hmrc.corporationtax.controllers.clientExchangeProxy

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers
import play.api.test.Helpers.*
import uk.gov.hmrc.corporationtax.controllers.base.SpecBase
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientExchangeProxyResponse
import uk.gov.hmrc.corporationtax.services.clientExchangeProxy.ClientExchangeProxyService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.Future

class ClientExchangeProxyControllerSpec extends SpecBase {

  private class Setup {

    val mockService: ClientExchangeProxyService = mock[ClientExchangeProxyService]

    val controller = new ClientExchangeProxyController(cc, fakeIdentifierAction, mockService)

    val response: ClientExchangeProxyResponse =
      ClientExchangeProxyResponse(browserInterval = 3000, businessServiceInterval = 900)

  }

  "GET /initiateClientListDownload" should {

    "return 200: OK" in new Setup {

      when(mockService.initiateClientListDownload(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(response))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(response)

      verify(mockService).initiateClientListDownload(any())(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Setup {
      val err: UpstreamErrorResponse =
        UpstreamErrorResponse("Client Exchange Proxy service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockService.initiateClientListDownload(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result) shouldBe BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Client Exchange Proxy service unavailable"
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.initiateClientListDownload(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve clientExchangeProxyResponse"

      verify(mockService).initiateClientListDownload(any())(any[HeaderCarrier])
    }

  }

}
