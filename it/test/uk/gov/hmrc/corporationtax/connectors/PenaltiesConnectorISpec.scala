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
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.PenaltiesHelper
import uk.gov.hmrc.http.HeaderCarrier

class PenaltiesConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PenaltiesHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: PenaltiesConnector = app.injector.instanceOf[PenaltiesConnector]

  // TODO: add auth stub and relevant cases
  "getPenaltyTransactionList" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/penalty-transactions/$taxRef/$accPeriod"

    "return Penalties empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"penaltyTransactions":
                   |[
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getPenaltyTransactionList(1L, 5L).futureValue
      result.penaltyTransactions must contain allElementsOf penaltiesEmptyList.penaltyTransactions
    }

    "return Penalties list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"penaltyTransactions":
                   |[
                   |  {"penaltyDate":"2025-05-01","type":"F","postingAmount":100.13 }
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getPenaltyTransactionList(1L, 5L).futureValue
      result.penaltyTransactions must contain allElementsOf penaltiesSingleItemList.penaltyTransactions
    }

    "return Penalties list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"penaltyTransactions":
                   |[
                   |  {"penaltyDate":"2025-05-01","type":"F","postingAmount":100.13 },
                   |  {"penaltyDate":"2021-03-07","type":"G","postingAmount":29.13 }
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getPenaltyTransactionList(1L, 5L).futureValue
      result.penaltyTransactions must contain allElementsOf penaltiesSingleItemList.penaltyTransactions
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
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
        connector.getPenaltyTransactionList(1L, 5L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
