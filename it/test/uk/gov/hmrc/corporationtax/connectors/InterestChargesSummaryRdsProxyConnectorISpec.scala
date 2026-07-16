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

import com.github.tomakehurst.wiremock.client.WireMock.{stubFor, urlPathEqualTo}
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.InterestChargesHelper
import uk.gov.hmrc.http.HeaderCarrier

class InterestChargesSummaryRdsProxyConnectorISpec extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with ApplicationWithWiremock
  with BeforeAndAfterEach
  with InterestChargesHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: InterestChargesSummaryRdsProxyConnector = app.injector.instanceOf[InterestChargesSummaryRdsProxyConnector]


  "getPenaltyTransactionList" should {

    def url(taxPayerReference:String) =
      s"/rds-datacache-proxy/corporation-tax/interest-charge-summary/$taxPayerReference"

    "return InterestCharges empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("1234")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"interestCharges":
                   |[]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getInterestChargesSummary("1234").futureValue
      result.interestCharges must contain allElementsOf emptyInterestCharges.interestCharges
    }

    "return InterestCharges list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("78965432")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"interestCharges":
                   |[
                   |  {
                   |  "accountingPeriod":"12",
                   |  "interestChargeSummary":"123.45"
                   |  },
                   |  {
                   |  "accountingPeriod":"145",
                   |  "interestChargeSummary":"-987.45"
                   |  }
                   |]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getInterestChargesSummary("78965432").futureValue
      result.interestCharges must contain allElementsOf interestChargesWithTSingleItem.interestCharges
    }

    "return InterestCharges list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url("789652")))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"interestCharges":
                   |[
                   |  {
                   |  "accountingPeriod":"12",
                   |  "interestChargeSummary":"123.45"
                   |  },
                   |  {
                   |  "accountingPeriod":"145",
                   |  "interestChargeSummary":"-987.45"
                   |  }
                   |
                   |]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getInterestChargesSummary("789652").futureValue
      result.interestCharges must contain allElementsOf interestChargesWithTwoItems.interestCharges
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url("123")))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getInterestChargesSummary("123").futureValue
      }
      ex.getMessage.toLowerCase must include("boom")
    }
    "return 400 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url("123")))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getInterestChargesSummary("123").futureValue
      }
      ex.getMessage must include("Invalid Request")
    }

    "return 404 when BE returns BAD_REQUEST " in {
      stubFor(
        get(urlPathEqualTo(url("123")))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody("Not found")
          )
      )

      val ex = intercept[Exception] {
        connector.getInterestChargesSummary("123").futureValue
      }
      ex.getMessage must include("Not found")
    }
  }




}
