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
import uk.gov.hmrc.corporationtax.testdata
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentPostingsHelper

class PaymentPostingsConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PaymentPostingsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig       = app.injector.instanceOf[AppConfig]
  private val connector: PaymentPostingsConnector = app.injector.instanceOf[PaymentPostingsConnector]

  "getPaymentPostings" should {

    def url(taxRef: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/payment-postings/$taxRef"

    "return an empty Payment Postings with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "paymentPostings": []
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentPostings(12L).futureValue
      result shouldBe emptyPaymentPostings
    }

    "return single Payment Postings with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(16L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "paymentPostings": [
                   |    {
                   |      "accountingPeriod": 202408,
                   |      "settlementSequenceNo": 123456,
                   |      "payslipId": 987654,
                   |      "postingAmount": 1500.75,
                   |      "effectiveDateOfPayment": "2024-09-15",
                   |      "paymentType": "BAC"
                   |    }
                   |  ]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentPostings(16L).futureValue
      result shouldBe singlePaymentPosting
    }

    "return multiple Payment Postings with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(100L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |    "paymentPostings": [
                   |    {
                   |      "accountingPeriod": 202408,
                   |      "settlementSequenceNo": 123456,
                   |      "payslipId": 987654,
                   |      "postingAmount": 1500.75,
                   |      "effectiveDateOfPayment": "2024-09-15",
                   |      "paymentType": "BAC"
                   |    },
                   |    {
                   |      "accountingPeriod": 202407,
                   |      "settlementSequenceNo": 642736,
                   |      "payslipId": 983123,
                   |      "postingAmount": 2712.63,
                   |      "effectiveDateOfPayment": "2024-08-20",
                   |      "paymentType": "BAC"
                   |    },
                   |    {
                   |      "accountingPeriod": 202404,
                   |      "settlementSequenceNo": 99423764,
                   |      "payslipId": 985624,
                   |      "postingAmount": 2416.89,
                   |      "effectiveDateOfPayment": "2024-05-26",
                   |      "paymentType": "BAC"
                   |    }
                   |  ]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentPostings(100L).futureValue
      result shouldBe multiplePaymentPostings
    }

  }

}
