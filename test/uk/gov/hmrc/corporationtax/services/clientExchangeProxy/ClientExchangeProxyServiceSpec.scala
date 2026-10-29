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

package uk.gov.hmrc.corporationtax.services.clientExchangeProxy

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.clientExchangeProxy.ClientExchangeProxyConnector
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientExchangeProxyResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ClientExchangeProxyServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: ClientExchangeProxyConnector = mock[ClientExchangeProxyConnector]
    val service                                     = new ClientExchangeProxyService(mockConnector)

    val response: ClientExchangeProxyResponse =
      ClientExchangeProxyResponse(browserInterval = 3000, businessServiceInterval = 900)

    val credentialId: String = "123456"
  }

  "ClientExchangeProxyService.initiateClientListDownload" should {

    "delegate to connector and successfully return ClientExchangeProxyResponse" in new BaseSetup {
      when(mockConnector.initiateClientListDownload(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(response))

      val result: ClientExchangeProxyResponse = service.initiateClientListDownload(credentialId).futureValue

      result shouldBe response

      verify(mockConnector).initiateClientListDownload(any())(any[HeaderCarrier])

      verify(mockConnector, times(1)).initiateClientListDownload(any())(any[HeaderCarrier])

    }

    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockConnector.initiateClientListDownload(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.initiateClientListDownload(credentialId).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockConnector, times(1)).initiateClientListDownload(any())(any[HeaderCarrier])

    }

  }

}
