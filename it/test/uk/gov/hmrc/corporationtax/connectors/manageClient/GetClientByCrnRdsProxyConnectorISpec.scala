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

package uk.gov.hmrc.corporationtax.connectors.manageClient

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.manageClient.AllClientsHelper
import uk.gov.hmrc.http.HeaderCarrier

class GetClientByCrnRdsProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with AllClientsHelper{

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: GetClientByCrnRdsProxyConnector =
    app.injector.instanceOf[GetClientByCrnRdsProxyConnector]

  "getClientByCrn" should {

    def url(irAgentReference:String, credentialId:String, companyRegNo:String) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/clients-by-crn/$irAgentReference/$credentialId/$companyRegNo"

    "return AllClients with emptyClient List  empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("STN003", "123456", "BUX300")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "clientCount": 0,
                   |  "clientList": [],
                   |  "clientNameStartChars": []
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getClientByCrn("STN003", "123456", "BUX300").futureValue
      result mustEqual emptyAllClients
    }
    "return AllClients with multiple clientList and clientNameStartChars from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("STN003", "123456", "BUX300")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "clientCount": 3,
                   |  "clientList": [
                   |    {
                   |      "utr": "1234567890",
                   |      "companyRegNo": "01234567",
                   |      "companyName": "ABC Limited",
                   |      "agentOwnRef": "AGENT001"
                   |    },
                   |    {
                   |      "utr": "9876543210",
                   |      "companyRegNo": null,
                   |      "companyName": "XYZ Solutions Ltd",
                   |      "agentOwnRef": "AGENT002"
                   |    },
                   |    {
                   |      "utr": "5678901234",
                   |      "companyRegNo": "09876543",
                   |      "companyName": "Global Technologies Ltd",
                   |      "agentOwnRef": null
                   |    }
                   |  ],
                   |  "clientNameStartChars": [
                   |    "A",
                   |    "G",
                   |    "X"
                   |  ]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getClientByCrn("STN003", "123456", "BUX300").futureValue
      result mustEqual allClients1
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url("STN003", "123456", "BUX300")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getClientByCrn("STN003", "123456", "BUX300").futureValue
      }
      ex.getMessage.toLowerCase must include("boom")
    }
    "return 400 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url("STN003", "123456", "BUX300")))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getClientByCrn("STN003", "123456", "BUX300").futureValue
      }
      ex.getMessage must include("Invalid Request")
    }

    "return 404 when BE returns NOT_FOUND " in {
      stubFor(
        get(urlPathEqualTo(url("STN003", "123456", "BUX300")))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        connector.getClientByCrn("STN003", "123456", "BUX300").futureValue
      }
      ex.getMessage must include("Not found")
    }
  }

}
