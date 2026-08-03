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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.ReallocationFromAccPeriodHelper
import uk.gov.hmrc.http.HeaderCarrier

class ReallocationFromAccPeriodRdsProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with ReallocationFromAccPeriodHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: ReallocationFromAccPeriodRdsProxyConnector =
    app.injector.instanceOf[ReallocationFromAccPeriodRdsProxyConnector]

  "getReallocationFromAccPeriod" should {

    def url(taxPayerReference: Long, accPeriod: Long) =
      s"/rds-datacache-proxy/corporation-tax/reallocation-from-accounting-period/$taxPayerReference/$accPeriod"

    "return ReallocationFromAccPeriod empty list from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(1234L, 345L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"reallocation":
                   |[]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getReallocationFromAccPeriod(1234L, 345L).futureValue
      result.reallocation must contain allElementsOf emptyListReallocationFromAccPeriod.reallocation
    }

    "return ReallocationFromAccPeriod list (single item) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(78965432L, 8745L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"reallocation":
                   |[
                   |  {
                   |  "amount" : 12390.67,
                   |  "reallocationDate": "2026-12-27",
                   |  "destinationApEndDate": "2024-02-02",
                   |  "destinationTaxPayerReference": "18969779586"
                   |  }
                   |]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getReallocationFromAccPeriod(78965432L, 8745L).futureValue
      result.reallocation must contain allElementsOf reallocationFromAccPeriodWithSingleElement.reallocation
    }

    "return InterestCharges list (two items) from BE with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(789652L, 8745L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"reallocation":
                   |[
                   |  {
                   |  "amount" : 12390.0,
                   |  "reallocationDate": "2026-12-27",
                   |  "destinationApEndDate": "2024-02-02",
                   |  "destinationTaxPayerReference": "18969779586"
                   |  },
                   |  {
                   |  "amount" : 180007.0,
                   |  "reallocationDate":"2026-12-27",
                   |  "destinationApEndDate":"2024-02-02",
                   |  "destinationTaxPayerReference": "18969779586"
                   |  },
                   |  {
                   |  "amount":89075.0,
                   |  "reallocationDate":"2026-12-27",
                   |  "destinationApEndDate":"2024-02-02",
                   |  "destinationTaxPayerReference": "18969779586"
                   |  }
                   |]
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getReallocationFromAccPeriod(789652L, 8745L).futureValue
      result.reallocation must contain allElementsOf reallocationFromAccPeriodWithThreeElements.reallocation
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
        connector.getReallocationFromAccPeriod(123L, 12L).futureValue
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
        connector.getReallocationFromAccPeriod(123L, 12L).futureValue
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
        connector.getReallocationFromAccPeriod(128L, 12L).futureValue
      }
      ex.getMessage must include("Not found")
    }
  }

}
