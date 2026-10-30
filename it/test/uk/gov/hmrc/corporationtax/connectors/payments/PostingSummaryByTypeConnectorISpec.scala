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

package uk.gov.hmrc.corporationtax.connectors.payments

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.payments.PostingSummaryByTypeHelper
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class PostingSummaryByTypeConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PostingSummaryByTypeHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig              = app.injector.instanceOf[AppConfig]
  private val connector: PostingSummaryByTypeConnector = app.injector.instanceOf[PostingSummaryByTypeConnector]

  "getPostingSummaryByType" should {

    def url(taxRef: Long, postingType: String)=
      s"${appConfig.rdsDatacacheProxyEndpoint}/posting-summary/$taxRef/$postingType"

    "return an empty posting summary with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, "PYT")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"postingSummary":[]
                   |
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPostingSummaryByType(1L, "PYT").futureValue
      result shouldBe rdsPostingSummaryByTypeResponseEmpty
    }

    "return posting summary with multiple objects with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, "PYT")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                     |{
                     |"postingSummary":
                     |[
                     | {
                     | "accountingPeriod":1,
                     | "settlementSequenceNo": 123,
                     | "postingAmountSum":-456789
                     |  },
                     | {
                     | "accountingPeriod":2,
                     | "settlementSequenceNo": 456,
                     | "postingAmountSum":-789012
                     |  },
                     |   {
                     | "accountingPeriod":3,
                     | "settlementSequenceNo": 789,
                     | "postingAmountSum":123456
                     |  }
                     |]
                     |}
                     |""".stripMargin
              )
          )
      )

      val result = connector.getPostingSummaryByType(1L, "PYT").futureValue
      result shouldBe rdsPostingSummaryByTypeResponseMultiple

    }

    "return posting summary with fields missing with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, "PYT")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"postingSummary":
                   |[
                   | {
                   | "accountingPeriod":2,
                   | "postingAmountSum":-789012
                   |  }
                   |  
                   |]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPostingSummaryByType(1L, "PYT").futureValue
      result shouldBe rdsPostingSummaryByTypeResponseNone

    }

    "return INTERNAL_SERVER_ERROR when the service failed" in {
      stubFor(
        get(urlPathEqualTo(url(12L, "PYT")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("Boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getPostingSummaryByType(12, "PYT").futureValue
      }

      ex.getMessage should include("Boom")
    }

    "return 404 when BE returns BAD_REQUEST" in {
      stubFor(
        get(urlPathEqualTo(url(13L, "PYT")))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getPostingSummaryByType(13, "PYT").futureValue
      }

      ex.getMessage should include("Invalid Request")
    }
  }
}
