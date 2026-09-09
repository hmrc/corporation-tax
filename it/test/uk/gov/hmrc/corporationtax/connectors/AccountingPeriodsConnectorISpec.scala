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
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.AccountingPeriodsHelper
import uk.gov.hmrc.http.HeaderCarrier

class AccountingPeriodsConnectorISpec extends
  AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with ApplicationWithWiremock
  with BeforeAndAfterEach
  with AccountingPeriodsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val accountingPeriodsConnector: AccountingPeriodsConnector = app.injector.instanceOf[AccountingPeriodsConnector]


  "AccountingPeriodsConnector.getAccountingPeriods" should {

    def getUrl(taxRef:Long):String = s"${appConfig.rdsDatacacheProxyEndpoint}/accounting-periods/$taxRef"

    "return a single RdsAccountingPeriod from proxy" in {
      stubFor(
        get(urlPathEqualTo(getUrl(1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "accountingPeriods": [
                   |    {
                   |      "accountingPeriod": 202501,
                   |      "apStartDate": "2025-01-01",
                   |      "apEndDate": "2025-12-31",
                   |      "apStatus": "Open",
                   |      "taxChargePresent": "Y",
                   |      "clericalIntSig": "Y",
                   |      "creditDebitInterestInd": "Y",
                   |      "taxTotal": 12345.67,
                   |      "interestTotal": 89.10,
                   |      "penaltyTotal": 250.00,
                   |      "payslipTotal": 5000.00,
                   |      "repayReallocTotal": 300.00,
                   |      "adjustmentTotal": 75.50
                   |    }
                   |  ]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = accountingPeriodsConnector.getAccountingPeriods(1L).futureValue

      result.accountingPeriods must contain allElementsOf accountingPeriodsWithSingleItem.accountingPeriods

    }
    "return a list of RdsAccountingPeriod from proxy" in {
      stubFor(
        get(urlPathEqualTo(getUrl(3L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |  "accountingPeriods": [
                   |    {
                   |      "accountingPeriod": 202501,
                   |      "apStartDate": "2025-01-01",
                   |      "apEndDate": "2025-12-31",
                   |      "apStatus": "Open",
                   |      "taxChargePresent": "Y",
                   |      "clericalIntSig": "Y",
                   |      "creditDebitInterestInd": "Y",
                   |      "taxTotal": 12345.67,
                   |      "interestTotal": 89.10,
                   |      "penaltyTotal": 250.00,
                   |      "payslipTotal": 5000.00,
                   |      "repayReallocTotal": 300.00,
                   |      "adjustmentTotal": 75.50
                   |    },
                   |    {
                   |      "accountingPeriod": 202501,
                   |      "apStartDate": "2025-01-01",
                   |      "apEndDate": "2025-12-31",
                   |      "apStatus": "Open",
                   |      "taxChargePresent": "Y",
                   |      "clericalIntSig": "N",
                   |      "creditDebitInterestInd": "N",
                   |      "taxTotal": 12345.67,
                   |      "interestTotal": 89.10,
                   |      "penaltyTotal": 250.00,
                   |      "payslipTotal": 5000.00,
                   |      "repayReallocTotal": 300.00,
                   |      "adjustmentTotal": 75.50
                   |    },
                   |    {
                   |      "accountingPeriod": 202501,
                   |      "apStartDate": "2025-01-01",
                   |      "apEndDate": "2025-12-31",
                   |      "apStatus": "Open",
                   |      "taxChargePresent": "N",
                   |      "clericalIntSig": "Y",
                   |      "creditDebitInterestInd": "N",
                   |      "taxTotal": 12345.67,
                   |      "interestTotal": 89.10,
                   |      "penaltyTotal": 250.00,
                   |      "payslipTotal": 5000.00,
                   |      "repayReallocTotal": 300.00,
                   |      "adjustmentTotal": 75.50
                   |    }
                   |  ]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = accountingPeriodsConnector.getAccountingPeriods(3L).futureValue

      result.accountingPeriods must contain allElementsOf accountingPeriodsWithSingleItem.accountingPeriods

    }
    "return an empty RdsAccountingPeriod from proxy" in {
      stubFor(
        get(urlPathEqualTo(getUrl(2L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"accountingPeriods":
                   |[
                   |]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = accountingPeriodsConnector.getAccountingPeriods(2L).futureValue

      result.accountingPeriods must contain allElementsOf emptyAccountingPeriods.accountingPeriods

    }
    "return INTERNAL_SERVER_ERROR when there is problem with Downstream services" in {
      stubFor(
        get(urlPathEqualTo(getUrl(20L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("Boom")
          )
      )

      val ex = intercept[Exception]{
        accountingPeriodsConnector.getAccountingPeriods(20L).futureValue
      }
      ex.getMessage must include("Boom")

    }

    "return 404 NOT_FOUND when Down stream services return NOT_FOUND" in {
      stubFor(
        get(urlPathEqualTo(getUrl(20L)))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        accountingPeriodsConnector.getAccountingPeriods(20L).futureValue
      }
      ex.getMessage must include("Not found")
    }
    "return 400 BAD_REQUEST  when Down stream services return NOT_FOUND" in {
      stubFor(
        get(urlPathEqualTo(getUrl(20L)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Bad Request")
          )
      )

      val ex = intercept[Exception] {
        accountingPeriodsConnector.getAccountingPeriods(20L).futureValue
      }
      ex.getMessage must include("Bad Request")
    }
  }

}
