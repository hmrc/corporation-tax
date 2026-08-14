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
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.ReallocationsConnector
import uk.gov.hmrc.corporationtax.helpers.ReallocationDataHelper
import uk.gov.hmrc.corporationtax.models.ReallocationToAccPeriod
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ReallocationServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with ReallocationDataHelper {

  private trait Fixture {
    val mockReallocationsConnector: ReallocationsConnector = mock[ReallocationsConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service = new ReallocationService(mockReallocationsConnector)

  }
  "ReallocationService.getByAccountingPeriod" should {
    "returns list of ReallocationsToAccPeriod from connector " in new Fixture {

      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationsTwoItems))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(1L, 1L).futureValue

      result shouldBe reallocationsToAccPeriodTwoItems

      verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
    }
    "returns empty of ReallocationsToAccPeriod from connector" in new Fixture {
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationEmptyList))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(1L, 1L).futureValue

      result shouldBe reallocationToAccPeriodEmptyList

      verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
    }
    "returns ReallocationsToAccPeriod with sourceApEndDate = emptyString when sourceApEndDate is not defined in Reallocations " in new Fixture {
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationWithNoSourceApEndDate))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(1L, 1L).futureValue

      result shouldBe reallocationToAccPeriodWithEmptySourceApEndDate

      verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
    }
    // Business Function -F32 - Get Reallocation To Accounting Period
    "returns ReallocationToAccPeriod with transactionType = MiscTFR when sourceTaxPayerReference is OASTransfer" in new Fixture {
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationWithSourceTaxOASTransfer))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(1L, 1L).futureValue

      result shouldBe reallocationToAccPeriodSingleItemMisc

      verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)

    }
    "returns ReallocationToAccPeriod with transactionType = MiscTFR when sourceTaxPayerReference is different from requestedTaxPayerReference" in new Fixture {
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationsWithSourceTaxRefDifferentToTaxRef))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(1542L, 1L).futureValue

      result shouldBe reallocationsToAccPeriodWithTransactionTypeMisc

      verify(mockReallocationsConnector).getByAccountingPeriod(1542L, 1L)(hc)

    }
    "returns ReallocationToAccPeriod with transactionType = MiscTFR when sourceTaxPayerReference = requestedTaxPayerReference and sourceApEndDate is empty" in new Fixture {
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationsWithSourceAPEndDateNotDefined))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(87L, 1L).futureValue

      result shouldBe reallocationsToAccPeriodWithTransactionTypeMiscFR

      verify(mockReallocationsConnector).getByAccountingPeriod(87L, 1L)(hc)

    }
    "returns ReallocationToAccPeriod with transactionType = RFR when sourceTaxPayerReference = requestedTaxPayerReference and sourceApEndDate is not empty" in new Fixture {
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationWithEqualSourceTaxRefAndTaxRef))

      val result: ReallocationToAccPeriod = service.getByAccountingPeriod(88, 1L).futureValue

      result shouldBe reallocationToAccPeriodWithTransactionTypeRFR

      verify(mockReallocationsConnector).getByAccountingPeriod(88L, 1L)(hc)

    }
    "returns failure from connector" in new Fixture {
      val error = new RuntimeException("Simulate error")
      when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
        .thenReturn(Future.failed(error))

      val result: Throwable = service.getByAccountingPeriod(1L, 1L).failed.futureValue

      result shouldBe error

      verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
    }
  }

}
