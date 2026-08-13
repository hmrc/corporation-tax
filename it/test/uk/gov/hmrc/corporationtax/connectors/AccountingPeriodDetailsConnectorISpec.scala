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
import uk.gov.hmrc.corporationtax.helpers.AccountingPeriodDetailsHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

class AccountingPeriodDetailsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with AccountingPeriodDetailsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val accountingPeriodDetailsConnector: AccountingPeriodDetailsConnector = app.injector.instanceOf[AccountingPeriodDetailsConnector]

  "getAccountingPeriodDetails" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/accounting-period-details/$taxRef/$accPeriod"

    "return a record" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "accountingPeriodDetails":{
                   |     "isApBalanced":"Y",
                   |     "lpiCalcFlag":"",
                   |     "crDbCalcFlag":"",
                   |     "creditInterestAmount":123.235,
                   |     "debitInterestAmount":5930.02,
                   |     "latePaymentInterestAmount":3231.238,
                   |     "repaymentInterestAmount":1.231,
                   |     "amountDueForAp":12.23}}""".stripMargin
              )
          )
      )

      val result = accountingPeriodDetailsConnector.getAccountingPeriodDetails(1L, 5L).futureValue
      result.accountingPeriodDetails mustBe apBalanceResponse.accountingPeriodDetails
    }


    "return empty record" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "accountingPeriodDetails":{
                   |    }
                   |  }""".stripMargin
              )
          )
      )

      val result = accountingPeriodDetailsConnector.getAccountingPeriodDetails(1L, 5L).futureValue
      result.accountingPeriodDetails mustBe apBalanceEmptyResponse.accountingPeriodDetails
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
        accountingPeriodDetailsConnector.getAccountingPeriodDetails(1L, 2L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
