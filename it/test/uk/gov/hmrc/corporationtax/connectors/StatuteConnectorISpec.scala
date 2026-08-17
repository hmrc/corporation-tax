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
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.models.StatuteRule
import uk.gov.hmrc.corporationtax.testdata.StatuteRuleHelper
import uk.gov.hmrc.http.HeaderCarrier

class StatuteConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with StatuteRuleHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: StatuteRuleConnector = app.injector.instanceOf[StatuteRuleConnector]

  "getStatueRule" should {

    def url(ruleRateKey: String, startDateStr: String, endDateStr: String) =
      s"/rds-datacache-proxy/corporation-tax/statue-rule/$ruleRateKey/$startDateStr/$endDateStr"

    "return no StatuteRule record" in {
      stubFor(
        get(urlPathEqualTo(url("C1", "1999-01-19", "1999-07-20")))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody(s"""{}""".stripMargin)
          )
      )

      val result = connector.getStatueRule("C1", "1999-01-19", "1999-07-20").futureValue
      result mustBe None
    }

    "return StatuteRule default" in {
      stubFor(
        get(urlPathEqualTo(url("C1", "1999-01-19", "1999-07-20")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "statuteRule": {
                   |    "numberOfDays":27,
                   |    "ruleEndDate":"1999-02-14",
                   |    "ruleAmount":100.011,
                   |    "ruleRate":5.75,
                   |    "ruleStartDate":"1999-01-18"
                   |  }
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getStatueRule("C1", "1999-01-19", "1999-07-20").futureValue
      result mustBe Some(StatuteRule(defaultRecord))
    }

    "return StatuteRule record with empty fields" in {
      stubFor(
        get(urlPathEqualTo(url("C1", "1999-01-19", "1999-07-20")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "statuteRule": {

                   |  }
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getStatueRule("C1", "1999-01-19", "1999-07-20").futureValue
      result mustBe Some(StatuteRule(recordWithEmptyFields))
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url("C1", "1999-01-19", "1999-07-20")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" : "Failed to retrieve penalties"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getStatueRule("C1", "1999-01-19", "1999-07-20").futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }

  }
}
