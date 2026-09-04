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
import uk.gov.hmrc.corporationtax.testdata.DisplayNeededHelper
import uk.gov.hmrc.http.HeaderCarrier

class DisplayNeededConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with DisplayNeededHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: DisplayNeededConnector = app.injector.instanceOf[DisplayNeededConnector]

  // TODO: add auth stub and relevant cases
  "getDisplayNeeded" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/display-needed/$taxRef/$accPeriod"

    "return Display Needed from BE with status code OK, with taxRef: 10L" in {
      stubFor(
        get(urlPathEqualTo(url(10L, 1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"displayNeeded":
                   |[
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getDisplayNeeded(10L, 1L).futureValue
      result mustBe displayNeededAllFalse
    }

    "return Display Needed from BE with status code OK, with taxRef: 10L" in {
      stubFor(
        get(urlPathEqualTo(url(20L, 1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"displayNeeded":
                   |[
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getDisplayNeeded(20L, 1L).futureValue
      result mustBe displayNeededAllTrue
    }

    "return Display Needed from BE with status code OK, with taxRef: 30L" in {
      stubFor(
        get(urlPathEqualTo(url(30L, 1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"displayNeeded":
                   |[
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getDisplayNeeded(30L, 1L).futureValue
      result mustBe displayNeededMixed
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(999L, 1L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" : "Failed to retrieve dispaly needed"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getDisplayNeeded(999L, 1L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
