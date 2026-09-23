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
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.models.RdsCompanyNominator
import uk.gov.hmrc.http.HeaderCarrier

class CompanyNominatorConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: CompanyNominatorConnector = app.injector.instanceOf[CompanyNominatorConnector]

  val companyNominatorTrue: RdsCompanyNominator = RdsCompanyNominator(isParticipator = "Y")
  val companyNominatorFalse: RdsCompanyNominator = RdsCompanyNominator(isParticipator = "N")

  "getIsCompanyNominatorOfGPA" should {

    def url(gpaUtr: Long, nominatedCompanyUtr: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/is-company-nominator/$gpaUtr/$nominatedCompanyUtr"

    "return a company nominator when isParticipator is 'Y' from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "isParticipator": "Y"
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getIsCompanyNominatorOfGPA(1L, 5L).futureValue
      result mustBe companyNominatorTrue
    }

    "return a company nominator when isParticipator is 'N' from BE" in {
      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |  "isParticipator": "N"
                   |}""".stripMargin
              )
          )
      )

      val result = connector.getIsCompanyNominatorOfGPA(1L, 5L).futureValue
      result mustBe companyNominatorFalse
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
        connector.getIsCompanyNominatorOfGPA(1L, 2L).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
