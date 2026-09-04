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
import play.api.libs.json.Json
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.helpers.InterestAccrualListHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

class InterestAccrualListConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with InterestAccrualListHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: InterestAccrualListConnector = app.injector.instanceOf[InterestAccrualListConnector]


  // TODO: add auth stub and relevant cases
  "getInterestAccrualList" should {

    def url(taxRef: Long, accPeriod: Long, interestType: String) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/interest-accrual-list/$taxRef/$accPeriod/$interestType"

    "return Interest Accrual empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(2L, 5L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(emptyInterestAccrualList)))
          )
      )

      val result = connector.getInterestAccrualList(2L, 5L, "IDE").futureValue
      result.interestAccruals must contain allElementsOf emptyInterestAccrualList.interestAccruals
    }

    "return Tax Transactions list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(interestAccrualSingleItemList)))
          )
      )

      val result = connector.getInterestAccrualList(1L, 5L, "IDE").futureValue

      verify(
        getRequestedFor(urlPathEqualTo(url(1L, 5L, "IDE")))
      )
      result.interestAccruals must contain allElementsOf interestAccrualSingleItemList.interestAccruals
    }

    "return Penalties list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L, "IDE")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(interestAccrualList)))
          )
      )

      val result = connector.getInterestAccrualList(1L, 5L, "IDE").futureValue
      result.interestAccruals must contain allElementsOf interestAccrualList.interestAccruals
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
        connector.getInterestAccrualList(99L, 5L, "IDE").futureValue
      }

      ex.getMessage must include("500")
    }
  }
}
