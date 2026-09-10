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
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.helpers.FormDataHelper
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate


class FormDataConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with FormDataHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val formDataConnector: FormDataConnector = app.injector.instanceOf[FormDataConnector]
  private val taxRef : Long = 1L
  private val accountingPeriod : Long = 1L
  private val startDate: LocalDate = LocalDate.of(2026, 1, 1)
  private val endDate: LocalDate = LocalDate.of(2026, 12, 31)

  "getFormData" should {

    def url(taxRef: Long, accPeriod: Long,
            startDate: LocalDate, endDate: LocalDate) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/ct-form-data/$taxRef/$accPeriod?startDate=${startDate.toString}&endDate=${endDate.toString}"

    "return a record" in {
      stubFor(
        get(urlEqualTo(url(taxRef, accountingPeriod, startDate, endDate)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{ "ct600XmlData":"data",
                   |   "formList":[ {"formType":"Y","xmlData":"xml_data"}] }
                   |  """.stripMargin
              )
          )
      )

      val result = formDataConnector.getFormData(taxRef, accountingPeriod, startDate, endDate).futureValue
      result mustBe defaultDataItem
    }


    "return empty record" in {
      stubFor(
        get(urlEqualTo(url(taxRef, accountingPeriod, startDate, endDate)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |   "formList":[ ] }
                   |  """.stripMargin
              )
          )
      )

      val result = formDataConnector.getFormData(taxRef, accountingPeriod, startDate, endDate).futureValue
      result mustBe fullyEmptyDataItem
    }


    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(taxRef, accountingPeriod, startDate, endDate)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |error" : "Failed to retrieve FormData"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        val result = formDataConnector.getFormData(taxRef, accountingPeriod, startDate, endDate).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }


  }
}
