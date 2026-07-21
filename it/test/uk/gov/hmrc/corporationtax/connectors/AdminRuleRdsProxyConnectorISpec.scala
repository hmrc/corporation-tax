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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.helpers.AdminRuleHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class AdminRuleRdsProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with AdminRuleHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: AdminRuleRdsProxyConnector = app.injector.instanceOf[AdminRuleRdsProxyConnector]

  "getAdminRule" should {

    def url(adminRule: String) =
      s"/rds-datacache-proxy/corporation-tax/administrative-rule/$adminRule"

    "return an empty getAdminRule from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("START-OF-CTSA")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getAdminRule("START-OF-CTSA").futureValue
      result.ruleDate mustBe empty
      result.ruleNumber mustBe empty
    }

    "return adminRule from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("INST-PERIOD")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"ruleNumber":3,
                   |"ruleDate":"1997-07-01"
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getAdminRule("INST-PERIOD").futureValue

      result.ruleNumber mustBe Some(3L)
      result.ruleDate mustBe Some(LocalDate.of(1997, 7, 1))

    }

    "return adminRule from BE with status code OK with only ruleNumber" in {
      stubFor(
        get(urlPathEqualTo(url("INST-REV-PER-D")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"ruleNumber":3
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getAdminRule("INST-REV-PER-D").futureValue
      result.ruleNumber mustBe Some(3L)
    }

    "return adminRule from BE with status code OK with only ruleDate" in {
      stubFor(
        get(urlPathEqualTo(url("LAST-INST-PER-M")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"ruleDate":"2012-04-08"
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getAdminRule("LAST-INST-PER-M").futureValue
      result.ruleDate mustBe Some(LocalDate.of(2012, 4, 8))
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url("123")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getAdminRule("123").futureValue
      }
      ex.getMessage.toLowerCase must include("boom")
    }

    "return 400 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url("123")))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getAdminRule("123").futureValue
      }
      ex.getMessage must include("Invalid Request")
    }

    "return 404 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url("123")))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        connector.getAdminRule("123").futureValue
      }
      ex.getMessage must include("Not found")
    }
  }

}
