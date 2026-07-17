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
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.{Application, Configuration, Environment, Mode}
import uk.gov.hmrc.corporationtax.helpers.InterestAccuralListHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

class InterestAccuralListConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with InterestAccuralListHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: InterestAccuralListConnector = app.injector.instanceOf[InterestAccuralListConnector]


  // TODO: add auth stub and relevant cases
  "getInterestAccuralList" should {

    def url(taxRef: Long, accPeriod: Long, interestType: String) =
      s"/rds-datacache-proxy/corporation-tax/interest-accural-list/$taxRef/$accPeriod/$interestType"

    "return Interest Accural empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(3L, 1L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(emptyInterestAccuralList)))
          )
      )

      val result = connector.getInterestAccuralList(2L, 5L, "IDE").futureValue
      result.interestAccuralList must contain allElementsOf emptyInterestAccuralList.interestAccuralList
    }

    "return Tax Transactions list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(interestAccuralSingleItemList)))
          )
      )

      val result = connector.getInterestAccuralList(1L, 5L, "IDE").futureValue

      verify(
        getRequestedFor(urlPathEqualTo(url(1L, 5L, "IDE")))
      )
      result.interestAccuralList must contain allElementsOf interestAccuralSingleItemList.interestAccuralList
    }

    "return Penalties list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(interestAccuralList)))
          )
      )

      val result = connector.getInterestAccuralList(1L, 5L, "IDE").futureValue
      result.interestAccuralList must contain allElementsOf interestAccuralList.interestAccuralList
    }

    "return failure when downstream returns INTERNAL_SERVER_ERROR" in {
      stubFor(
        get(urlPathEqualTo(url(99L, 5L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val ex = intercept[Exception] {
        connector.getInterestAccuralList(1L, 5L, "IDE").futureValue
      }

      ex.getMessage must include("500")
    }
  }
}
