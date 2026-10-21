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
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.payments.PaymentSearchResultHelper
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class PaymentSearchResultConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PaymentSearchResultHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig              = app.injector.instanceOf[AppConfig]
  private val connector: PaymentSearchResultConnector = app.injector.instanceOf[PaymentSearchResultConnector]

  "getPaymentSearchResult" should {

    def url(taxRef: Long)=
      s"${appConfig.rdsDatacacheProxyEndpoint}/payment-search-result/$taxRef"

    "return an empty paymentSearchResult with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .withQueryParam("fromDate", equalTo("2007-01-01"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"paymentSearchResult":[]
                   |
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentSearchResult(12L, Some(LocalDate.of(2007, 1, 1)), None).futureValue
      result shouldBe rdsEmptyPaymentSearchResult
    }

    "return paymentSearchResult with multiple objects with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(3L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                     |{
                     |"paymentSearchResult":
                     |[
                     | {
                     | "accountingPeriod":1,
                     | "payslipId": 1,
                     | "amount":37500,
                     | "effectiveDateOfPayment":"2007-07-14",
                     | "paymentType": "BGP",
                     | "bankSortCode": 114433,
                     | "bankAccountNumber": 1004005,
                     | "chequeNumber": 100301,
                     | "paymentAmount": 31250,
                     | "chequeId": 916
                     |  },
                     | {
                     | "accountingPeriod":1,
                     | "payslipId": 2,
                     | "amount":31250,
                     | "effectiveDateOfPayment":"2006-10-14",
                     | "paymentType": "BGP",
                     | "bankSortCode": 114433,
                     | "bankAccountNumber": 1004005,
                     | "chequeNumber": 100301,
                     | "paymentAmount": 31250,
                     | "chequeId": 917
                     |  },
                     |   {
                     | "accountingPeriod":1,
                     | "payslipId": 3,
                     | "amount":37500,
                     | "effectiveDateOfPayment":"2005-07-14",
                     | "paymentType": "BGP",
                     | "bankSortCode": 114433,
                     | "bankAccountNumber": 1004005,
                     | "chequeNumber": 100301,
                     | "paymentAmount": 31250,
                     | "chequeId": 918
                     |  }
                     |]
                     |}
                     |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentSearchResult(3L, None, None).futureValue
      result shouldBe rdsMultiplePaymentSearchResult

    }

    "return INTERNAL_SERVER_ERROR when the service failed" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("Boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentSearchResult(12, None, None).futureValue
      }

      ex.getMessage should include("Boom")
    }

    "return 404 when BE returns BAD_REQUEST" in {
      stubFor(
        get(urlPathEqualTo(url(13L)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentSearchResult(13, None, None).futureValue
      }

      ex.getMessage should include("Invalid Request")
    }
  }
}
