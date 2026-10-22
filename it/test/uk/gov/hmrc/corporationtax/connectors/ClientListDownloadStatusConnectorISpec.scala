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

package uk.gov.hmrc.corporationtax.connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.*
import play.api.libs.json.Json
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus

class ClientListDownloadStatusConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: ClientListDownloadStatusConnector = app.injector.instanceOf[ClientListDownloadStatusConnector]

  val initiateDownload: ClientDownloadStatus = ClientDownloadStatus(-1)

  // TODO: add auth stub and relevant cases
  "getClientListDownloadStatus" should {
    
    def url(credentialId: String) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/client-list-status/$credentialId"

    "return initiate download status for client download status - OK" in {
      stubFor(
        get(urlPathEqualTo(url("1")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(initiateDownload)))
          )
      )

      val result = connector.getClientListDownloadStatus("1").futureValue
      result mustBe initiateDownload
    }
    
    "return failure when downstream returns INTERNAL_SERVER_ERROR" in {
      stubFor(
        get(urlPathEqualTo(url("1")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val ex = intercept[Exception] {
        connector.getClientListDownloadStatus("1").futureValue
      }

      ex.getMessage must include("500")
    }
  }
}
