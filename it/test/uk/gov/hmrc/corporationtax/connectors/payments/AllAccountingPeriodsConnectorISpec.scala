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
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.payments.AllAccountingPeriodsHelper
import uk.gov.hmrc.http.HeaderCarrier

class AllAccountingPeriodsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with AllAccountingPeriodsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: AllAccountingPeriodsConnector = app.injector.instanceOf[AllAccountingPeriodsConnector]

  "getAccountingPeriodList" should {

    def url(taxRef: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/all-accounting-period/$taxRef"

    "return successful all accounting periods" in {
      stubFor(
        get(urlEqualTo(url(1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "accountingPeriodList": [
                   |    {
                   |      "accountingPeriod": 10,
                   |      "apEndDate": "2025-07-24",
                   |      "taxChargePresent": "Y",
                   |      "penaltyTotal": 10
                   |    }
                   |  ]
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getAccountingPeriodList(1L).futureValue
      result mustBe rdsAllAccountingPeriods
    }

    "return a multiple items of all accounting periods" in {
      stubFor(
        get(urlEqualTo(url(1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "accountingPeriodList": [
                   |    {
                   |      "accountingPeriod": 20,
                   |      "apEndDate": "2025-08-24",
                   |      "taxChargePresent": "1",
                   |      "penaltyTotal": 20
                   |    },
                   |    {
                   |      "accountingPeriod": 30,
                   |      "apEndDate": "2025-08-24",
                   |      "taxChargePresent": "N",
                   |      "penaltyTotal": 30
                   |    }
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getAccountingPeriodList(1L).futureValue
      result mustBe rdsMultipleAllAccountingPeriods
    }

    "return an empty list of all accounting periods" in {
      stubFor(
        get(urlEqualTo(url(1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "accountingPeriodList": []
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getAccountingPeriodList(1L).futureValue
      result mustBe rdsEmptyAllAccountingPeriods
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(1L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve all accounting periods"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getAccountingPeriodList(1L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
