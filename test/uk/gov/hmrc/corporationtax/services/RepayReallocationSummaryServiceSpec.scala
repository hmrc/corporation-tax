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
import org.mockito.Mockito
import org.mockito.Mockito.{verify, verifyNoInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.helpers.RepayReallocationSummaryHelper
import uk.gov.hmrc.corporationtax.models.RepayReallocationSummary
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class RepayReallocationSummaryServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with RepayReallocationSummaryHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRepaymentsService: RepaymentsService                       = mock[RepaymentsService]
    val mockReallocationsFromService: ReallocationFromAccPeriodService = mock[ReallocationFromAccPeriodService]
    val mockReallocationsToService: ReallocationService                = mock[ReallocationService]
    val repayReallocationService                                       = new RepayReallocationSummaryService(
      mockRepaymentsService,
      mockReallocationsFromService,
      mockReallocationsToService
    )
  }

  "getRepayments" should {

    "delegate to services and successfully return repayment reallocation summary with one repayments item" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(repaymentsWithOneItem))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationFrom))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationTo))

      val result = repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue

      result shouldBe repayReallocationWithRepaymentsItem

      verify(mockRepaymentsService).getRepayments(1L, 2L)
      verify(mockReallocationsFromService).getReallocationFromAccPeriod(1L, 2L)
      verify(mockReallocationsToService).getByAccountingPeriod(1L, 2L)
    }

    "delegate to connector and successfully return repayment reallocation summary with one reallocationFrom item" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyRepayments))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationFromWithOneItem))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationTo))

      val result = repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue

      result shouldBe repayReallocationWithReallocationFromItem

      verify(mockRepaymentsService).getRepayments(1L, 2L)
      verify(mockReallocationsFromService).getReallocationFromAccPeriod(1L, 2L)
      verify(mockReallocationsToService).getByAccountingPeriod(1L, 2L)
    }

    "delegate to connector and successfully return repayment reallocation summary with one reallocationTo item" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyRepayments))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationFrom))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationToWithOneItem))

      val result = repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue

      result shouldBe repayReallocationWithReallocationToItem

      verify(mockRepaymentsService).getRepayments(1L, 2L)
      verify(mockReallocationsFromService).getReallocationFromAccPeriod(1L, 2L)
      verify(mockReallocationsToService).getByAccountingPeriod(1L, 2L)
    }

    "delegate to connector and successfully return repayment reallocation summary with one of each collection" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(repaymentsWithOneItem))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationFromWithOneItem))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(reallocationToWithOneItem))

      val result = repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue

      result shouldBe repayReallocationWithOneOfEachItem

      verify(mockRepaymentsService).getRepayments(1L, 2L)
      verify(mockReallocationsFromService).getReallocationFromAccPeriod(1L, 2L)
      verify(mockReallocationsToService).getByAccountingPeriod(1L, 2L)
    }

    "delegate to connector and successfully return empty repayment reallocation summary" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyRepayments))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationFrom))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationTo))

      val result = repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue

      result shouldBe emptyRepayReallocation

      verify(mockRepaymentsService).getRepayments(1L, 2L)
      verify(mockReallocationsFromService).getReallocationFromAccPeriod(1L, 2L)
      verify(mockReallocationsToService).getByAccountingPeriod(1L, 2L)
    }

    "propagate any errors or exceptions from one service" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(repaymentsWithOneItem))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyReallocationTo))

      val ex = intercept[RuntimeException](repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue)

      ex.getMessage should include("error")

      verify(mockRepaymentsService).getRepayments(1L, 2L)
      verify(mockReallocationsFromService).getReallocationFromAccPeriod(1L, 2L)
      verifyNoInteractions(mockReallocationsToService)
    }

    "propagate any errors or exceptions from all services" in new Setup {
      when(mockRepaymentsService.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))
      when(mockReallocationsFromService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))
      when(mockReallocationsToService.getByAccountingPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        repayReallocationService.getRepayReallocationSummary(1L, 2L).futureValue
      }

      ex.getMessage should include("error")

      verify(mockRepaymentsService).getRepayments(1L, 2L)
    }
  }

}
