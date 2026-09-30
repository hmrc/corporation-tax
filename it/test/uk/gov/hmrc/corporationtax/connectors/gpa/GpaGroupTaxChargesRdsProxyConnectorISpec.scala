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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.gpa.GpaGroupTaxChargesHelper
import uk.gov.hmrc.http.HeaderCarrier

class GpaGroupTaxChargesRdsProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with GpaGroupTaxChargesHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig                  = app.injector.instanceOf[AppConfig]
  private val connector: GpaGroupTaxChargesRdsProxyConnector =
    app.injector.instanceOf[GpaGroupTaxChargesRdsProxyConnector]

  "getGpaGroupTaxCharges" should {

    def url(pGpaUtr: Long, pGppContractVersion: Int, pStartIndex: Int, pCount: Int) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/group-tax-charges/$pGpaUtr/$pGppContractVersion?pStartIndex=$pStartIndex&pCount=$pCount"

    "return RdsGpaGroupTaxCharges list (single item) from BE with status code OK" in {
      stubFor(
        get(urlEqualTo(url(78965432L, 8745, 12, 13)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "pGppEndDate": "2023-04-05",
                   |  "pGppTotalGroupPayment": 15000.50,
                   |  "pGppTotalGroupTax": 3200.75,
                   |  "pGppStatus": "S",
                   |  "pGppCni": "2023-03-01",
                   |  "pGppApportionmentMethod": "E",
                   |  "pGpaUtr2": 200,
                   |  "pTotalNumOfRecords": 3,
                   |  "pGroupPaymentRecordCount": 3,
                   |  "pCurGroupTaxCharges": [
                   |    {
                   |      "participatorName": "Company A Ltd",
                   |      "participatorReference": 1234567890,
                   |      "participatorApEndDate": "2023-03-31",
                   |      "participatorTaxCharge": 1066.92,
                   |      "participatorTaxChargePrsnt": "Y",
                   |      "participatorAccountingPeriod": 1,
                   |      "contractVersion": 1,
                   |      "allocatedPayment": 5000.0,
                   |      "allocatedPaymentRecordCount": 1
                   |    },
                   |    {
                   |      "participatorName": "Company B Ltd",
                   |      "participatorReference": 2345678901,
                   |      "participatorApEndDate": "2023-03-31",
                   |      "participatorTaxCharge": 1280.11,
                   |      "participatorTaxChargePrsnt": "Y",
                   |      "participatorAccountingPeriod": 1,
                   |      "contractVersion": 1,
                   |      "allocatedPayment": 6000.5,
                   |      "allocatedPaymentRecordCount": 1
                   |    }
                   |  ]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getGpaGroupTaxCharges(78965432L, 8745, 12, 13).futureValue
      result mustBe gpaWithNonEmptyParticipator
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(123L, 12, 1, 2)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getGpaGroupTaxCharges(123L, 12, 1, 2).futureValue
      }
      ex.getMessage.toLowerCase must include("boom")
    }
    "return 400 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlEqualTo(url(123L, 12, 14, 16)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getGpaGroupTaxCharges(123L, 12, 14, 16).futureValue
      }
      ex.getMessage must include("Invalid Request")
    }

    "return 404 when BE returns NOT_FOUND " in {
      stubFor(
        get(urlEqualTo(url(128, 12, 14, 16)))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        connector.getGpaGroupTaxCharges(128L, 12, 14, 16).futureValue
      }
      ex.getMessage must include("Not found")
    }
  }

}
