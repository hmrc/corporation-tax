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
import uk.gov.hmrc.corporationtax.helpers.TaxTransactionsHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

class TaxTransactionsConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with TaxTransactionsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: TaxTransactionsConnector = app.injector.instanceOf[TaxTransactionsConnector]


  // TODO: add auth stub and relevant cases
  "getTaxTransactions" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/tax-transactions/$taxRef/$accPeriod"

    "return Tax Transactions empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(emptyTaxTransactions)))
          )
      )

      val result = connector.getTaxTransactions(1L, 5L).futureValue
      result.taxTransactions must contain allElementsOf emptyTaxTransactions.taxTransactions
    }

    "return Tax Transactions list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(taxTransactionsSingleItemList)))
          )
      )

      val result = connector.getTaxTransactions(1L, 5L).futureValue

      verify(
        getRequestedFor(urlPathEqualTo(url(1L, 5L)))
      )
      result.taxTransactions must contain allElementsOf taxTransactionsSingleItemList.taxTransactions
    }

    "return Penalties list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(taxTransactions)))
          )
      )

      val result = connector.getTaxTransactions(1L, 5L).futureValue
      result.taxTransactions must contain allElementsOf taxTransactions.taxTransactions
    }

    "return failure when downstream returns INTERNAL_SERVER_ERROR" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val ex = intercept[Exception] {
        connector.getTaxTransactions(1L, 5L).futureValue
      }

      ex.getMessage must include("500")
    }
  }
}
