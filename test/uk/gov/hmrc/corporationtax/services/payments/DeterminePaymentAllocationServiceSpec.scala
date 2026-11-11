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

package uk.gov.hmrc.corporationtax.services.payments

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.helpers.payments.DeterminePaymentAllocationHelper
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class DeterminePaymentAllocationServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with DeterminePaymentAllocationHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockPaymentSearchResultService = mock[PaymentSearchResultService]
    val mockElectronicService          = mock[ElectronicPaymentAllocationDetailsService]
    val mockChequeService              = mock[PaymentAllocationChequeDetailsService]

    val determinePaymentAllocation = new DeterminePaymentAllocationService(
      mockPaymentSearchResultService,
      mockElectronicService,
      mockChequeService
    )
  }

  "getPaymentType" should {

    "delegate to service and successfully return one item list of payment allocation from electronic payments" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicOneItemPaymentSearchResult))
      when(mockElectronicService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicPaymentAllocationDetails))

      val result = determinePaymentAllocation
        .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
        .futureValue

      result shouldBe paymentAllocation

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockElectronicService).getPayAllocationElectronicDtls(any(), any(), any())(any())
    }

    "delegate to service and successfully return one item list of payment allocation from cheque payments" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(chequeOneItemPaymentSearchResult))
      when(mockChequeService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(chequePaymentAllocationDetails))

      val result = determinePaymentAllocation
        .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
        .futureValue

      result shouldBe paymentAllocation

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockChequeService).getPaymentAllocationChequeDetails(any(), any())(any())
    }

    "delegate to service and successfully return multiple item list of payment allocation from electronic payments" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicMultiItemPaymentSearchResult))
      when(mockElectronicService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(
          Future.successful(electronicPaymentAllocationDetails),
          Future.successful(electronicPaymentAllocationDetails)
        )

      val result = determinePaymentAllocation
        .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
        .futureValue

      result shouldBe multiPaymentAllocation

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockElectronicService, times(2)).getPayAllocationElectronicDtls(any(), any(), any())(any())
    }

    "delegate to service and successfully return multiple item list of payment allocation from cheque payments" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(chequeMultiItemPaymentSearchResult))
      when(mockChequeService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(
          Future.successful(chequePaymentAllocationDetails),
          Future.successful(chequePaymentAllocationDetails)
        )

      val result = determinePaymentAllocation
        .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
        .futureValue

      result shouldBe multiPaymentAllocation

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockChequeService, times(2)).getPaymentAllocationChequeDetails(any(), any())(any())
    }

    "delegate to service and successfully return multiple item list of electronic and cheque payments" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(mixedElectAndCheqPaymentSearchResult))
      when(mockElectronicService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicPaymentAllocationDetails))
      when(mockChequeService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(chequePaymentAllocationDetails))

      val result = determinePaymentAllocation
        .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
        .futureValue

      result shouldBe multiPaymentAllocation

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockElectronicService).getPayAllocationElectronicDtls(any(), any(), any())(any())
      verify(mockChequeService).getPaymentAllocationChequeDetails(any(), any())(any())
    }

    "propagate any errors or exceptions from one service" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(mixedElectAndCheqPaymentSearchResult))
      when(mockElectronicService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(electronicPaymentAllocationDetails))
      when(mockChequeService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        determinePaymentAllocation
          .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
          .futureValue
      }

      ex.getMessage should include("error")

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockElectronicService).getPayAllocationElectronicDtls(any(), any(), any())(any())
      verify(mockChequeService).getPaymentAllocationChequeDetails(any(), any())(any())
    }

    "propagate any errors or exceptions from all services" in new Setup {
      when(mockPaymentSearchResultService.getPaymentSearchResult(any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))
      when(mockElectronicService.getPayAllocationElectronicDtls(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))
      when(mockChequeService.getPaymentAllocationChequeDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        determinePaymentAllocation
          .getPaymentType(1L, Some(LocalDate.of(2026, 7, 24)), Some(LocalDate.of(2026, 7, 24)), BigDecimal(1000))
          .futureValue
      }

      ex.getMessage should include("error")

      verify(mockPaymentSearchResultService).getPaymentSearchResult(any(), any(), any(), any())(any())
      verify(mockElectronicService, never()).getPayAllocationElectronicDtls(any(), any(), any())(any())
      verify(mockChequeService, never()).getPaymentAllocationChequeDetails(any(), any())(any())
    }
  }

}
