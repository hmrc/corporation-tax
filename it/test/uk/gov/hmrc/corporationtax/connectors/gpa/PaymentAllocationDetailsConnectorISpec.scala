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

package uk.gov.hmrc.corporationtax.connectors.gpa

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.gpa.PaymentAllocationDetailsHelper
import uk.gov.hmrc.http.HeaderCarrier

class PaymentAllocationDetailsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PaymentAllocationDetailsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: PaymentAllocationDetailsConnector = app.injector.instanceOf[PaymentAllocationDetailsConnector]

  "getGPAPaymentAllocationDetail" should {

    def url(gpaUtr: Long, gppContractVersion: Long, participatorUtr: Long, participatorAp: Long, startIndex: Int, count: Int) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/gpa-payment-allocation-details/$gpaUtr/$gppContractVersion/$participatorUtr/$participatorAp?startIndex=${startIndex.toString}&count=${count.toString}"

    "return a successful payment allocation details" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L, 4L, 5, 6)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "gppEndDate": "2024-07-24",
                   |  "gppTotalGroupPayment": 10,
                   |  "gppTotalGroupTax": 10,
                   |  "gppStatus": "O",
                   |  "gppApportionmentMethod": "O",
                   |  "participatingCompanyDesc": "ABC Limited",
                   |  "participatorAccPeriodEnd": "2024-07-24",
                   |  "participatorTaxCharge": 10,
                   |  "participatorAllocPayments": 10,
                   |  "gpaUtr": 10,
                   |  "gppContractVersionOut": 10,
                   |  "allocationDetails": [
                   |    {
                   |      "effectivePaymentDate": "2024-07-24",
                   |      "paymentAmount": 10
                   |    }
                   |  ],
                   |  "totalNumOfRecords": 10
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5, 6).futureValue
      result mustBe rdsFullPaymentAllocationDetails
    }

    "return a payment allocation details with multiple allocation details" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L, 4L, 5, 6)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "gppEndDate": "2025-07-24",
                   |  "gppTotalGroupPayment": 20,
                   |  "gppTotalGroupTax": 20,
                   |  "gppStatus": "C",
                   |  "gppApportionmentMethod": "C",
                   |  "participatingCompanyDesc": "DEF Limited",
                   |  "participatorAccPeriodEnd": "2025-07-24",
                   |  "participatorTaxCharge": 20,
                   |  "participatorAllocPayments": 20,
                   |  "gpaUtr": 20,
                   |  "gppContractVersionOut": 20,
                   |  "allocationDetails": [
                   |    {
                   |      "effectivePaymentDate": "2025-07-24",
                   |      "paymentAmount": 20
                   |    },
                   |    {
                   |      "effectivePaymentDate": "2025-07-24",
                   |      "paymentAmount": 20
                   |    }
                   |  ],
                   |  "totalNumOfRecords": 20
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5, 6).futureValue
      result mustBe rdsPaymentAllocationDetailsWithMultipleAllocationDetails
    }

    "return a payment allocation details with minimal details" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L, 4L, 5, 6)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "gppEndDate": "2026-07-24",
                   |  "gppStatus": "P",
                   |  "participatingCompanyDesc": "GHI Limited",
                   |  "participatorAccPeriodEnd": "2026-07-24",
                   |  "participatorTaxCharge": 30,
                   |  "participatorAllocPayments": 30,
                   |  "gpaUtr": 30,
                   |  "gppContractVersionOut": 30,
                   |  "allocationDetails": [
                   |    {
                   |      "effectivePaymentDate": "2026-07-24",
                   |      "paymentAmount": 30
                   |    }
                   |  ],
                   |  "totalNumOfRecords": 30
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5, 6).futureValue
      result mustBe rdsMinimalPaymentAllocationDetails
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L, 3L, 4L, 5, 6)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve payment allocation details"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getGPAPaymentAllocationDetail(1L, 2L, 3L, 4L, 5, 6).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
