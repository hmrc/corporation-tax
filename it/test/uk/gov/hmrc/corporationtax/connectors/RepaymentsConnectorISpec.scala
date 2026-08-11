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
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.RepaymentsHelper
import uk.gov.hmrc.http.HeaderCarrier

class RepaymentsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with RepaymentsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: RepaymentsConnector = app.injector.instanceOf[RepaymentsConnector]

  "getRepayments" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/repayments/$taxRef/$accPeriod"

    "return a successful an empty repayment list from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "repayments": []
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getRepayments(1L, 5L).futureValue
      result mustBe emptyRepayments
    }

    "return a repayment list with one item from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "repayments": [
                   |    {
                   |      "amount": 10,
                   |      "repaymentType": "S",
                   |      "repaymentDate": "2026-07-24"
                   |    }
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getRepayments(1L, 5L).futureValue
      result mustBe repaymentsWithOneItem
    }

    "return a repayment list with multiple items from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "repayments": [
                   |    {
                   |      "amount": 20,
                   |      "repaymentType": "S",
                   |      "repaymentDate": "2027-07-24"
                   |    },
                   |    {
                   |      "amount": 30,
                   |      "repaymentType": "T",
                   |      "repaymentDate": "2028-07-24"
                   |    }
                   |  ]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getRepayments(1L, 5L).futureValue
      result mustBe repaymentsWithMultipleItems
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve the repayment list from the BE"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getRepayments(1L, 2L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
