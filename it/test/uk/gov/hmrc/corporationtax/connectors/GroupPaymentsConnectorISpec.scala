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
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.helpers.GroupPaymentsHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier


class GroupPaymentsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with GroupPaymentsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: GroupPaymentsConnector = app.injector.instanceOf[GroupPaymentsConnector]

  "getGroupSummary" should {

    def url(gpaUTR: Long, nomCompanyUTR: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/group-summary/$gpaUTR/$nomCompanyUTR"

    "return no GroupSummaryDetails record" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withBody(s"""{}""".stripMargin)
          )
      )

      val result = connector.getGroupSummary(1L, 2L).futureValue
      result mustBe None
    }

    "return GroupSummaryDetails default record" in {
      stubFor(
        get(urlEqualTo(url(2L, 1L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{"gpaGrpSummaryDetails":[{
                   |   "contractEndDate":"2026-01-07",
                   |   "groupTaxCharge":11.01,
                   |   "groupPayment":13.02,
                   |   "groupPaymentRecordCount":2,
                   |   "contractStatus":"ACTIVE",
                   |   "contractVersion":2}],
                   |   "gpaReferenceNumberLst":[{
                   |   "taxpayerReference":112}],
                   |   "nominatedCompanyName":"Some company name"}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getGroupSummary(2L, 1L).futureValue
      result mustBe Some(groupPaymentDetails)
    }


    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(1L, 2L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" : "Failed to retrieve penalties"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getGroupSummary(1L, 2L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }


  }
}
