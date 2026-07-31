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
import uk.gov.hmrc.corporationtax.testdata.PayRepayReallocationHelper
import uk.gov.hmrc.http.HeaderCarrier

class PayRepayReallocationConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PayRepayReallocationHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: PayRepayReallocationConnector = app.injector.instanceOf[PayRepayReallocationConnector]

  "getTotalAmounts" should {

    def url(taxRef: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/total-amount-payment-repayment-reallocation/$taxRef/$accPeriod"

    "return a successful an empty payment repayment reallocation list from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"payRepayReallocationList":
                   |[
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getTotalAmounts(1L, 5L).futureValue
      result mustBe emptyPayRepayReallocationList
    }

    "return a payment repayment reallocation list with single item from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"payRepayReallocationList":
                   |[
                   |  {"totalAmountReoRfrRto":10.00,"totalAmountPayments":20.00}
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getTotalAmounts(1L, 5L).futureValue
      result mustBe payRepayReallocationListWithOneItem
    }

    "return a payment repayment reallocation list with multiple items from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"payRepayReallocationList":
                   |[
                   |  {"totalAmountReoRfrRto":30.00,"totalAmountPayments":40.00},
                   |  {"totalAmountReoRfrRto":50.00,"totalAmountPayments":60.00}
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getTotalAmounts(1L, 2L).futureValue
      result mustBe payRepayReallocationListWithMultipleItems
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve the payment repayment allocations list from the BE"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getTotalAmounts(1L, 2L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
