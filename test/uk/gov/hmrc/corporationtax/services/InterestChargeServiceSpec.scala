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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.InterestChargesSummaryRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.InterestChargesHelper
import uk.gov.hmrc.corporationtax.models.InterestCharges
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class InterestChargeServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with InterestChargesHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: InterestChargesSummaryRdsProxyConnector = mock[InterestChargesSummaryRdsProxyConnector]
    val service                                          = new InterestChargeService(mockRds)
    val taxPayerReference: String                        = "0986542"
  }

  "InterestChargeService.getInterestChargesSummary" should {

    "delegate to connector and successfully return InterestCharges" in new BaseSetup {
      when(mockRds.getInterestChargesSummary(eqTo(taxPayerReference))(any[HeaderCarrier]))
        .thenReturn(Future.successful(interestCharges))

      val result: InterestCharges = service.getInterestChargesSummary(taxPayerReference).futureValue

      result shouldBe interestCharges

      verify(mockRds).getInterestChargesSummary(taxPayerReference)

      verify(mockRds, times(1)).getInterestChargesSummary(taxPayerReference)

    }

    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getInterestChargesSummary(eqTo(taxPayerReference))(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getInterestChargesSummary(taxPayerReference).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getInterestChargesSummary(taxPayerReference)

    }

  }

}
