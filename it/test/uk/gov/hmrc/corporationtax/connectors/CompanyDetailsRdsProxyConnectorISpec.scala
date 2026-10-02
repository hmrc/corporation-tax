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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.CompanyDetailsHelper
import uk.gov.hmrc.http.HeaderCarrier

class CompanyDetailsRdsProxyConnectorISpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with CompanyDetailsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig              = app.injector.instanceOf[AppConfig]
  private val connector: CompanyDetailsRdsProxyConnector = app.injector.instanceOf[CompanyDetailsRdsProxyConnector]

  "getCompanyDetails" should {

    def url(taxRef: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/company-details/$taxRef"

    "return an empty RdsCompanyDetails with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"taxpayerDetails":[]
                   |
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getCompanyDetails(12L).futureValue
      result shouldBe emptyCompany
    }

    "return RdsCompanyDetails with multiple objects with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(16L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                     |{
                     |"taxpayerDetails":
                     |[
                     | { 
                     | "orgUnitId":"OU-100234",
                     | "companyName":"Northbridge Logistics Ltd",
                     | "companyRegNo":"08123456",
                     | "addressLine1":"Unit 4 Riverside Park",
                     | "addressLine2": "Canal Street",
                     | "addressLine3": "Birmingham",
                     | "addressLine4": "West Midlands",
                     | "postCode": "B1 2JH"
                     |  },
                     |  { 
                     | "orgUnitId":"OU-100587",
                     | "companyName":"Harlow & Finch Consulting Ltd",
                     | "companyRegNo": "11987654",
                     | "addressLine1": "22 Market Square",
                     | "addressLine2": "Leeds",
                     | "addressLine3": "address1",
                     | "addressLine4": "address2",
                     | "postCode": "LS1 6DT"
                     |  }
                     |]
                     |}
                     |""".stripMargin
              )
          )
      )

      val result = connector.getCompanyDetails(16L).futureValue
      result shouldBe twoCompanies

    }

    "return INTERNAL_SERVER_ERROR when the servie failed" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("Boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getCompanyDetails(12).futureValue
      }

      ex.getMessage should include("Boom")
    }

    "return 404 when BE returns BAD_REQUEST" in {
      stubFor(
        get(urlPathEqualTo(url(13L)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getCompanyDetails(13).futureValue
      }

      ex.getMessage should include("Invalid Request")
    }
  }
}
