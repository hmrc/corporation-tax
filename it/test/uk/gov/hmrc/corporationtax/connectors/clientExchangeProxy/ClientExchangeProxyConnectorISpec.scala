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

package uk.gov.hmrc.corporationtax.connectors.clientExchangeProxy

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.clientExchangeProxy.ClientExchangeProxyHelper
import uk.gov.hmrc.http.HeaderCarrier

class ClientExchangeProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with ClientExchangeProxyHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig           = app.injector.instanceOf[AppConfig]
  private val connector: ClientExchangeProxyConnector =
    app.injector.instanceOf[ClientExchangeProxyConnector]

  "initiateClientListDownload" should {
    def url(credentialId: String) =
      s"${appConfig.clientExchangeProxyEndpoint}/CT/$credentialId/agent/clientList"

    "return clientExchangeProxyResponse with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("78965432")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"browserInterval": 4500,
                   |"businessServiceInterval":600
                   |}""".stripMargin
              )
          )
      )

      val result = connector.initiateClientListDownload("78965432").futureValue
      result mustBe clientExchangeResponse
    }
    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url("123456")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("boom")
          )
      )

      val ex = intercept[Exception] {
        connector.initiateClientListDownload("123456").futureValue

      }
      ex.getMessage.toLowerCase must include("boom")
    }
    "return 400 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url("1232523")))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.initiateClientListDownload("123456").futureValue

      }

    }
    "return 404 when BE returns NOT_FOUND " in {
      stubFor(
        get(urlPathEqualTo(url("128")))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        connector.initiateClientListDownload("128").futureValue
      }
      ex.getMessage must include("Not found")
    }
  }

}
