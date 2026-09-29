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

package uk.gov.hmrc.corporationtax.services.gpa

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.gpa.GpaGroupTaxChargesRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.gpa.GpaGroupTaxChargesHelper
import uk.gov.hmrc.corporationtax.models.gpa.GpaGroupTaxCharges
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GpaGroupTaxChargesServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with GpaGroupTaxChargesHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: GpaGroupTaxChargesRdsProxyConnector = mock[GpaGroupTaxChargesRdsProxyConnector]
    val service                                      = new GpaGroupTaxChargesService(mockRds)
    val pGpaUtr: Long                                = 5L
    val pGppContractVersion: Int                     = 8
    val pStartIndex: Int                             = 0
    val pCount: Int                                  = 1
  }

  "GpaGroupTaxChargesService.getGpaGroupTaxCharges" should {

    "delegate to connector and successfully return GpaGroupTaxCharges" in new BaseSetup {
      when(mockRds.getGpaGroupTaxCharges(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsGpaWithNonEmptyParticipator))

      val result: GpaGroupTaxCharges =
        service.getGpaGroupTaxCharges(pGpaUtr, pGppContractVersion, pStartIndex, pCount).futureValue

      result shouldBe gpaWithNonEmptyParticipator

      verify(mockRds).getGpaGroupTaxCharges(any(), any(), any(), any())(any[HeaderCarrier])

      verify(mockRds, times(1)).getGpaGroupTaxCharges(any(), any(), any(), any())(any[HeaderCarrier])

    }

    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getGpaGroupTaxCharges(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getGpaGroupTaxCharges(pGpaUtr, pGppContractVersion, pStartIndex, pCount).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getGpaGroupTaxCharges(any(), any(), any(), any())(any[HeaderCarrier])

    }

  }

}
