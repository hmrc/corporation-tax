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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.PaymentAllocationChequeDetailsHelper
import uk.gov.hmrc.http.HeaderCarrier

class PaymentAllocationChequeDetailsRdsProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PaymentAllocationChequeDetailsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: PaymentAllocationChequeDetailsRdsProxyConnector =
    app.injector.instanceOf[PaymentAllocationChequeDetailsRdsProxyConnector]

  "getPaymentAllocationChequeDetails" should {

    def url(taxRef: Long, chequeId: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/cheque-payment-allocation-details/$taxRef/$chequeId"

    "return PaymentAllocationChequeDetails empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1234L, 345L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"paymentAllocationDetails":
                   |[]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentAllocationChequeDetails(1234L, 345L).futureValue
      result mustEqual emptyRdsPaymentAllocationChequeDetails
    }

    "return PaymentAllocationChequeDetails list with multiple items with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(78965432L, 8745L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "paymentAllocationDetails": [
                   |    {
                   |      "accountingPeriod": 1,
                   |      "apEndDate": "2023-03-31",
                   |      "amount": 1500.00,
                   |      "effectiveDateOfPayment": "2023-04-15"
                   |    },
                   |    {
                   |      "accountingPeriod": 2,
                   |      "apEndDate": "2024-03-31",
                   |      "amount": 2750.50,
                   |      "effectiveDateOfPayment": "2024-04-20"
                   |    },
                   |    {
                   |      "accountingPeriod": 3,
                   |      "apEndDate": "2025-03-31",
                   |      "amount": 980.25,
                   |      "effectiveDateOfPayment": "2025-05-01"
                   |    },
                   |    {
                   |      "accountingPeriod": 4,
                   |      "apEndDate": "2026-03-31",
                   |      "amount": 3200.00,
                   |      "effectiveDateOfPayment": "2026-04-10"
                   |    }
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getPaymentAllocationChequeDetails(78965432L, 8745L).futureValue
      result mustEqual fullRdsPaymentAllocationChequeDetails
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(123L, 12L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentAllocationChequeDetails(123L, 12L).futureValue
      }
      ex.getMessage.toLowerCase must include("boom")
    }
    "return 400 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url(123L, 12L)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentAllocationChequeDetails(123L, 12L).futureValue
      }
      ex.getMessage must include("Invalid Request")
    }

    "return 404 when BE returns NOT_FOUND " in {
      stubFor(
        get(urlPathEqualTo(url(128L, 12L)))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentAllocationChequeDetails(128L, 12L).futureValue
      }
      ex.getMessage must include("Not found")
    }
  }

}
