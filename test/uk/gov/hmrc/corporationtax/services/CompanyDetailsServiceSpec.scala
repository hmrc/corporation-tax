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

package uk.gov.hmrc.corporationtax.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.CompanyDetailsRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.CompanyDetailsHelper
import uk.gov.hmrc.corporationtax.models.CompanyDetailsResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class CompanyDetailsServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with CompanyDetailsHelper {

  private trait Setup {
    val mockConnector: CompanyDetailsRdsProxyConnector = mock[CompanyDetailsRdsProxyConnector]
    implicit val hc: HeaderCarrier                     = HeaderCarrier()
    private val cc: ControllerComponents               = stubControllerComponents()
    implicit val ec: ExecutionContext                  = cc.executionContext

    val taxRef: Long = 1287892L

    val service = new CompanyDetailsService(mockConnector)

  }

  "getCompanyDetails" should {
    "return a list of CompanyDetails after with null values transformed to emptyString" in new Setup {
      when(mockConnector.getCompanyDetails(any())(any[HeaderCarrier])).thenReturn(Future.successful(rdsTwoCompanies))

      val result: CompanyDetailsResponse = service.getCompanyDetails(taxRef).futureValue

      result shouldBe twoCompanies

      verify(mockConnector, times(1)).getCompanyDetails(any())(any[HeaderCarrier])

    }

    "return an emptyList if the connector returns an empty CompanyDetails" in new Setup {

      when(mockConnector.getCompanyDetails(any())(any[HeaderCarrier])).thenReturn(Future.successful(rdsEmptyCompany))

      val result: CompanyDetailsResponse = service.getCompanyDetails(taxRef).futureValue

      result shouldBe CompanyDetailsResponse(taxpayerDetails = List.empty)

      verify(mockConnector, times(1)).getCompanyDetails(any())(any[HeaderCarrier])
    }

    "propagates exceptions from connector" in new Setup {
      val ex = new RuntimeException("Error from Downstream")

      when(mockConnector.getCompanyDetails(any())(any[HeaderCarrier])).thenReturn(Future.failed(ex))

      val result: Exception = intercept[Exception] {
        service.getCompanyDetails(taxRef).futureValue
      }

      verify(mockConnector, times(1)).getCompanyDetails(any())(any[HeaderCarrier])

    }
  }

}
