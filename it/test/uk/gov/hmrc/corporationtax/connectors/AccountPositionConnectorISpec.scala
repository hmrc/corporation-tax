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
import uk.gov.hmrc.corporationtax.helpers.AccountPositionHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier


class AccountPositionConnectorISpec extends
  AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with ApplicationWithWiremock
  with BeforeAndAfterEach
  with AccountPositionHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val accountPositionConnector: AccountPositionConnector = app.injector.instanceOf[AccountPositionConnector]


  "getAccountPosition" should {

    def getUrl(taxRef: Long): String = s"${appConfig.rdsDatacacheProxyEndpoint}/account-position/$taxRef"

    "return a default record from proxy" in {
      stubFor(
        get(urlPathEqualTo(getUrl(1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{ "amountDue":15.18,
                   |  "asOnDate":"2026-01-01",
                   |  "gpaLinkFlag":"N",
                   |  "taxpayerList":["1002"],
                   |  "apAmounts":[{
                   |    "accountingPeriod":51,
                   |    "apEndDate":"2024-02-03",
                   |    "amountDueForAp":4.326,
                   |    "apStatus":"N"
                   |    }],
                   |    "doesCompanyExist":"Y"}
                   |""".stripMargin
              )
          )
      )

      val result = accountPositionConnector.getAccountPosition(1L).futureValue

      result mustBe Some(defaultRecord)
    }

    "return a record with empty values from proxy" in {
      stubFor(
        get(urlPathEqualTo(getUrl(1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "taxpayerList":[],
                   |  "apAmounts": []
                   |}""".stripMargin
              )
          )
      )

      val result = accountPositionConnector.getAccountPosition(1L).futureValue

      result mustBe Some(emptyRecord)
    }


    "return INTERNAL_SERVER_ERROR when there is problem with Downstream services" in {
      stubFor(
        get(urlPathEqualTo(getUrl(20L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("Boom")
          )
      )

      val ex = intercept[Exception] {
        accountPositionConnector.getAccountPosition(20L).futureValue
      }
      ex.getMessage must include("Boom")

    }


    "return 400 BAD_REQUEST for incorrect request" in {
      stubFor(
        get(urlPathEqualTo(getUrl(20L)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Bad Request")
          )
      )

      val ex = intercept[Exception] {
        accountPositionConnector.getAccountPosition(20L).futureValue
      }
      ex.getMessage must include("Bad Request")
    }

  }

}
