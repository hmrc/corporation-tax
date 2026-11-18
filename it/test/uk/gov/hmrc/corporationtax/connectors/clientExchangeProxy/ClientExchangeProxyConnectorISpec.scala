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
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.AsynchronousProcessWaitTime
import uk.gov.hmrc.corporationtax.testdata.clientExchangeProxy.ClientExchangeProxyHelper
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

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

  val credId = "cred-123"
  val serviceName = "CT"
  val gracePeriod = 14400

  def url(credentialId: String) =
    s"${appConfig.clientExchangeProxyEndpoint}/CT/$credentialId/agent/clientList"

  "initiateClientListDownload" should {

    "return AsynchronousProcessWaitTime when XML is valid and status is 2xx" in {
      val xmlBody =
        """<AsynchronousProcessWaitTime browserInterval="1000">
          |  <BusinessServiceInterval>100</BusinessServiceInterval>
          |  <BusinessServiceInterval>200</BusinessServiceInterval>
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      stubFor(
        get(urlPathEqualTo(url(credId)))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/xml")
              .withBody(xmlBody)
          )
      )

      val result = connector.initiateClientListDownload(credId).futureValue

      result mustBe AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(100L, 200L)
      )
    }

    "return AsynchronousProcessWaitTime with empty businessIntervals when no BusinessServiceInterval elements" in {
      val xmlBody =
        """<AsynchronousProcessWaitTime browserInterval="500">
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      stubFor(
        get(urlPathEqualTo(url(credId)))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/xml")
              .withBody(xmlBody)
          )
      )

      val result = connector.initiateClientListDownload(credId).futureValue

      result mustBe AsynchronousProcessWaitTime(
        browserIntervalMs = 500L,
        businessIntervalsMs = List.empty
      )
    }

    "fail with UpstreamErrorResponse(502) when XML is invalid or missing required elements" in {
      val xmlBody =
        """<AsynchronousProcessWaitTime>
          |  <BusinessServiceInterval>100</BusinessServiceInterval>
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      stubFor(
        get(urlPathEqualTo(url(credId)))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/xml")
              .withBody(xmlBody)
          )
      )

      val ex = connector.initiateClientListDownload(credId).failed.futureValue

      ex mustBe a[UpstreamErrorResponse]
      val upstream = ex.asInstanceOf[UpstreamErrorResponse]
      upstream.statusCode mustBe 502
      upstream.message must include("client-exchange-proxy parse error")
    }

    "fail with UpstreamErrorResponse(502) when XML is not well-formed" in {
      val xmlBody = "<AsynchronousProcessWaitTime"

      stubFor(
        get(urlPathEqualTo(url(credId)))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/xml")
              .withBody(xmlBody)
          )
      )

      val ex = connector.initiateClientListDownload(credId).failed.futureValue

      ex mustBe a[UpstreamErrorResponse]
      val upstream = ex.asInstanceOf[UpstreamErrorResponse]
      upstream.statusCode mustBe 502
      upstream.message must include("client-exchange-proxy parse error")
    }

    "fail with UpstreamErrorResponse using upstream status when response is non-2xx (e.g. 500)" in {
      stubFor(
        get(urlPathEqualTo(url(credId)))
          .willReturn(
            aResponse()
              .withStatus(500)
              .withBody("upstream error")
          )
      )

      val ex = connector.initiateClientListDownload(credId).failed.futureValue

      ex mustBe a[UpstreamErrorResponse]
      val upstream = ex.asInstanceOf[UpstreamErrorResponse]
      upstream.statusCode mustBe 500
      upstream.message must include("client-exchange-proxy HTTP 500")
    }
    
  }
}

