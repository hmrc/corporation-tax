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
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.payments.ElectronicPaymentAllocationDetailsHelper
import uk.gov.hmrc.http.HeaderCarrier

class ElectronicPaymentAllocationDetailsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with ElectronicPaymentAllocationDetailsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: ElectronicPaymentAllocationDetailsConnector = app.injector.instanceOf[ElectronicPaymentAllocationDetailsConnector]

  "getPayAllocationElectronicDtls" should {

    def url(taxRef: Long, accPeriod: Long, payslipId: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/electronic-payment-allocation-details/$taxRef/$accPeriod/$payslipId"

    "return successful electronic payment allocation details" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "electronicPaymentAllocationDetails": [
                   |    {
                   |      "accountingPeriod": 10,
                   |      "apEndDate": "2024-08-31",
                   |      "amount": 10,
                   |      "effectiveDateOfPayment": "2024-08-31"
                   |    }
                   |  ]
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getPayAllocationElectronicDtls(1L, 2L, 3L).futureValue
      result mustBe ePaymentAllocationDetails
    }

    "return multiple electronic payment allocation details" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "electronicPaymentAllocationDetails": [
                   |    {
                   |      "accountingPeriod": 20,
                   |      "apEndDate": "2024-09-30",
                   |      "amount": 20,
                   |      "effectiveDateOfPayment": "2024-09-30"
                   |    },
                   |    {
                   |      "accountingPeriod": 30,
                   |      "apEndDate": "2024-10-31",
                   |      "amount": 30,
                   |      "effectiveDateOfPayment": "2024-10-31"
                   |    }
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getPayAllocationElectronicDtls(1L, 2L, 3L).futureValue
      result mustBe multipleEPaymentAllocationDetails
    }

    "return an empty electronic payment allocation details" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "electronicPaymentAllocationDetails": []
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getPayAllocationElectronicDtls(1L, 2L, 3L).futureValue
      result mustBe emptyEPaymentAllocationDetails
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve electronic payment allocation details"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getPayAllocationElectronicDtls(1L, 2L, 3L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
