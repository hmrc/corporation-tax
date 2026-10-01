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

package uk.gov.hmrc.corporationtax.connectors.gpa

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlPathEqualTo}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.connectors.gpa.GroupPaymentPeriodsInRangeConnector
import uk.gov.hmrc.corporationtax.helpers.gpa.PeriodWithinRangeHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

class GroupPaymentPeriodsInRangeConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PeriodWithinRangeHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: GroupPaymentPeriodsInRangeConnector = app.injector.instanceOf[GroupPaymentPeriodsInRangeConnector]

  // TODO: add auth stub and relevant cases
  "getGroupPaymentPeriodsInRange" should {

    def url(gpaUTR: Long, nominatedCompanyUTR: Long, pPeriod: Int, pMonthRestriction: Int) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/group-payment-periods-in-range/$gpaUTR/$nominatedCompanyUTR/$pPeriod/$pMonthRestriction"

    "returns 200 with PeriodWithinRange with field set to false" in {
      stubFor(
        get(urlPathEqualTo(url(10L, 1000L, 1, 1)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"isPeriodWithinRange": "N"
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getGroupPaymentPeriodsInRange(10L, 1000L, 1, 1).futureValue
      result mustBe periodWithinRangeResponseFalse
    }

    "returns 200 with PeriodWithinRange with field set to true" in {
      stubFor(
        get(urlPathEqualTo(url(20L, 1000L, 1, 1)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"isPeriodWithinRange": "Y"
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getGroupPaymentPeriodsInRange(20L, 1000L, 1, 1).futureValue
      result mustBe periodWithinRangeResponseTrue
    }

    "return error when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(999L, 1000L, 1, 1)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |"statusCode" : 500
                   |"message" : "Error from downstream"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getGroupPaymentPeriodsInRange(999L, 1000L, 1, 1).futureValue
      }
      ex.getMessage.toLowerCase must include("error from downstream")
    }
  }
}
