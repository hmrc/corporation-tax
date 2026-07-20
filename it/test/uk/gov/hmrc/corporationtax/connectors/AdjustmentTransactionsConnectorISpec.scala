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
import uk.gov.hmrc.corporationtax.testdata.AdjustmentTransactionsHelper
import uk.gov.hmrc.http.HeaderCarrier

class AdjustmentTransactionsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with AdjustmentTransactionsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val adjustmentTransactionsConnector: AdjustmentTransactionsConnector = app.injector.instanceOf[AdjustmentTransactionsConnector]

  "getAdjustmentTransactions" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/adjustment-transactions/$taxRef/$accPeriod"

    "return a successful an empty adjustment transactions list from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"adjustmentTransactionsList":
                   |[
                   |]}""".stripMargin
              )
          )
      )

      val result = adjustmentTransactionsConnector.getAdjustmentTransactions(1L, 5L).futureValue
      result.adjustmentTransactionsList must contain allElementsOf emptyAdjustmentTransactionsList.adjustmentTransactionsList
    }

    "return an adjustment transaction list with single item from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"adjustmentTransactionsList":
                   |[
                   |  {"amount":50.00,"type":"N"}
                   |]}""".stripMargin
              )
          )
      )

      val result = adjustmentTransactionsConnector.getAdjustmentTransactions(1L, 5L).futureValue
      result.adjustmentTransactionsList must contain allElementsOf adjustmentTransactionsListWithOneItem.adjustmentTransactionsList
    }

    "return an adjustment transactions list with multiple items from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"adjustmentTransactionsList":
                   |[
                   |  {"amount":60.00 ,"type":"F"},
                   |  {"amount":70.00 ,"type":"L"}
                   |]}""".stripMargin
              )
          )
      )

      val result = adjustmentTransactionsConnector.getAdjustmentTransactions(1L, 2L).futureValue
      result.adjustmentTransactionsList must contain allElementsOf adjustmentTransactionsListWithMultipleItems.adjustmentTransactionsList
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" : "Failed to retrieve adjustment transactions from the BE"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        adjustmentTransactionsConnector.getAdjustmentTransactions(1L, 2L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
