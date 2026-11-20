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
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.manageClient.AllClientsHelper
import uk.gov.hmrc.http.HeaderCarrier

class AllClientsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with AllClientsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: AllClientsConnector = app.injector.instanceOf[AllClientsConnector]

  "getAllClients" should {

    def url(irAgentId: String, credentialId: String, start: Int, count: Int, sort: Int, order: String) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/all-clients/$irAgentId/$credentialId?start=$start&count=$count&sort=$sort&order=$order"

    "return successful response of all clients" in {
      stubFor(
        get(urlEqualTo(url("irAgentId1", "credentialId1", 0, 30, 0, "ASC")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "clientCount": 100,
                   |  "clientList": [
                   |    {
                   |      "utr": "12345",
                   |      "companyRegNo": "01234A",
                   |      "companyName": "ABC Limited",
                   |      "agentOwnRef": "ABC10"
                   |    }
                   |  ],
                   |  "clientNameStartChars": [
                   |    "A"
                   |  ]
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getAllClients("irAgentId1", "credentialId1", 0, 30, 0, "ASC").futureValue
      result mustBe allClients
    }

    "return all clients with multiple items of client list" in {
      stubFor(
        get(urlEqualTo(url("irAgentId2", "credentialId2", 0, 30, 0, "ASC")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "clientCount": 100,
                   |  "clientList": [
                   |    {
                   |      "utr": "12345",
                   |      "companyRegNo": "01234A",
                   |      "companyName": "ABC Limited",
                   |      "agentOwnRef": "ABC10"
                   |    },
                   |    {
                   |      "utr": "67890",
                   |      "companyRegNo": "6789B",
                   |      "companyName": "DEF Limited",
                   |      "agentOwnRef": "DEF10"
                   |    }
                   |  ],
                   |  "clientNameStartChars": [
                   |    "D"
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getAllClients("irAgentId2", "credentialId2", 0, 30, 0, "ASC").futureValue
      result mustBe multipleClientListAllClients
    }

    "return all clients with multiple items of client name chars" in {
      stubFor(
        get(urlEqualTo(url("irAgentId3", "credentialId3", 0, 30, 0, "ASC")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "clientCount": 100,
                   |  "clientList": [
                   |    {
                   |      "utr": "111213",
                   |      "companyRegNo": "111213C",
                   |      "companyName": "GHI Limited",
                   |      "agentOwnRef": "GHI10"
                   |    }
                   |  ],
                   |  "clientNameStartChars": [
                   |    "G", "H", "I"
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getAllClients("irAgentId3", "credentialId3", 0, 30, 0, "ASC").futureValue
      result mustBe multipleItemsClientNameAllClients
    }

    "return all clients with non default params" in {
      stubFor(
        get(urlEqualTo(url("irAgentId4", "credentialId4", 0, 30, 3, "DESC")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "clientCount": 100,
                   |  "clientList": [
                   |    {
                   |      "utr": "12345",
                   |      "companyRegNo": "01234A",
                   |      "companyName": "ABC Limited",
                   |      "agentOwnRef": "ABC10"
                   |    },
                   |    {
                   |      "utr": "67890",
                   |      "companyRegNo": "6789B",
                   |      "companyName": "DEF Limited",
                   |      "agentOwnRef": "DEF10"
                   |    }
                   |  ],
                   |  "clientNameStartChars": [
                   |    "D"
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getAllClients("irAgentId4", "credentialId4", 0, 30, 3, "DESC").futureValue
      result mustBe getParamCombo(0, 30, 3, "DESC")
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url("irAgentIdNA", "credentialIdNA", 0, 30, 0, "ASC")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve all clients"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getAllClients("irAgentIdNA", "credentialIdNA", 0, 30, 0, "ASC").futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
