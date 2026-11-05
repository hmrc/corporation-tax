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
import org.scalatest.matchers.must.Matchers.{must, mustBe}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.models.HasClient
import uk.gov.hmrc.http.HeaderCarrier

class HasClientConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig =
    app.injector.instanceOf[AppConfig]

  private val connector: HasClientConnector =
    app.injector.instanceOf[HasClientConnector]

  "hasClient" should {

    def url(utr: Long, irAgentId: String, credentialId: String) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/has-client/$utr/$irAgentId/$credentialId"

    "return HasClient with status 1 when the client exists" in {
      stubFor(
        get(urlPathEqualTo(url(1234567890L, "AGENT123", "0903840830080480")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody("""{"status":1}""")
          )
      )

      val result = connector.hasClient(
        1234567890L,
        "AGENT123",
        "0903840830080480"
      ).futureValue

      result mustBe HasClient(1)
    }

    "return HasClient with status 0 when th client does not exist" in {
      stubFor(
        get(urlPathEqualTo(url(1234567890L, "AGENT123", "0903840830080480")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody("""{"status":0}""")
          )
      )

      val result = connector.hasClient(
        1234567890L,
        "AGENT123",
        "0903840830080480"
      ).futureValue

      result mustBe HasClient(0)
    }

    "return failure when downstream returns INTERNAL_SERVER_ERROR" in {
      stubFor(
        get(urlPathEqualTo(url(1234567890L, "AGENT123", "0903840830080480")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val ex = intercept[Exception] {
        connector.hasClient(
          1234567890L,
          "AGENT123",
          "0903840830080480"
        ).futureValue
      }

      ex.getMessage must include("500")
    }

    "return 400 when BE returns BAD_REQUEST" in {
      stubFor(
        get(urlPathEqualTo(url(1234567890L, "AGENT123", "0903840830080480")))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
          )
      )

      val ex = intercept[Exception] {
        connector.hasClient(
          1234567890L,
          "AGENT123",
          "0903840830080480"
        ).futureValue
      }

      ex.getMessage must include("400")
    }

  }

}
