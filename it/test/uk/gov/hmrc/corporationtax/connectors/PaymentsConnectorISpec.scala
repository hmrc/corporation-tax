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
import uk.gov.hmrc.corporationtax.helpers.PaymentsHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

class PaymentsConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PaymentsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: PaymentsConnector = app.injector.instanceOf[PaymentsConnector]


  // TODO: add auth stub and relevant cases
  "getPayments" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/payment-transactions/$taxRef/$accPeriod"

    "return Payment Transactions empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(emptyPaymentTransactions)))
          )
      )

      val result = connector.getPayments(1L, 5L).futureValue
      result.paymentTransactions must contain allElementsOf emptyPaymentTransactions.paymentTransactions
    }

    "return Tax Transactions list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(paymentTransactionsSingleItemList)))
          )
      )

      val result = connector.getPayments(1L, 5L).futureValue

      verify(
        getRequestedFor(urlPathEqualTo(url(1L, 5L)))
      )
      result.paymentTransactions must contain allElementsOf paymentTransactionsSingleItemList.paymentTransactions
    }

    "return Penalties list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(paymentTransactions)))
          )
      )

      val result = connector.getPayments(1L, 5L).futureValue
      result.paymentTransactions must contain allElementsOf paymentTransactions.paymentTransactions
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
        connector.getPayments(1L, 5L).futureValue
      }

      ex.getMessage must include("500")
    }
  }
}
