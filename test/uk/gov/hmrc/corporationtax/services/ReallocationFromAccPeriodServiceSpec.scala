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
import uk.gov.hmrc.corporationtax.connectors.ReallocationFromAccPeriodRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.ReallocationFromAccPeriodHelper
import uk.gov.hmrc.corporationtax.models.TransformedReallocationFromAccPeriod
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ReallocationFromAccPeriodServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with ReallocationFromAccPeriodHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: ReallocationFromAccPeriodRdsProxyConnector = mock[ReallocationFromAccPeriodRdsProxyConnector]
    val service                                             = new ReallocationFromAccPeriodService(mockRds)
    val taxReferenceNumber: Long                            = 1234567L
    val accPeriod: Long                                     = 3456L
  }

  "ReallocationFromAccPeriodService.getReallocationFromAccPeriod" should {

    "delegate to connector and successfully return TransformedReallocationFromAccPeriod by transforming ReallocationFromAccPeriod with amount = None transformed to 0.00" in new BaseSetup {
      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationFromAccPeriodWithNullAmount))

      val result: TransformedReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedNullAmountReallocationFromAccPeriod

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return TransformedReallocationFromAccPeriod by transforming ReallocationFromAccPeriod with non-zero amount field negated and rounded up to 2 decimal places" in new BaseSetup {
      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationFromAccPeriodWithAmount3DecimalPlaces))

      val result: TransformedReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformed3DecimalPlacesAmountReallocationFromAccPeriod

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return TransformedReallocationFromAccPeriod by transforming ReallocationFromAccPeriod with zero amount unchanged" in new BaseSetup {
      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationFromAccPeriodWithZeroAmount))

      val result: TransformedReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocationFromAccPeriodWithZeroAmount

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return TransformedReallocationFromAccPeriod by transforming ReallocationFromAccPeriod with destinationApEndDate = None to empty string " in new BaseSetup {
      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationFromAccPeriodWithZeroAmount))

      val result: TransformedReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocationFromAccPeriodWithZeroAmount

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }

    "delegate to connector and successfully return empty TransformedReallocationFromAccPeriod when ReallocationFromAccPeriod is empty" in new BaseSetup {
      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyListReallocationFromAccPeriod))

      val result: TransformedReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe emptyTransformedListReallocationFromAccPeriod

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }

    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }

  }

}
