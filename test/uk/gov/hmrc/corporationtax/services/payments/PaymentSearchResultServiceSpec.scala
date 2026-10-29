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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentSearchResultConnector
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentSearchResultHelper
import uk.gov.hmrc.corporationtax.models.payments.PaymentSearchResultResponse
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class PaymentSearchResultServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with PaymentSearchResultHelper {

  private trait Setup {
    val mockConnector: PaymentSearchResultConnector = mock[PaymentSearchResultConnector]
    implicit val hc: HeaderCarrier                  = HeaderCarrier()
    private val cc: ControllerComponents            = stubControllerComponents()
    implicit val ec: ExecutionContext               = cc.executionContext

    val taxRef: Long = 1287892L

    val service = new PaymentSearchResultService(mockConnector)

  }

  "getPaymentSearchResult" should {
    "return a transformed list of PaymentSearchResults" in new Setup {
      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsMultiplePaymentSearchResult))
      val result: PaymentSearchResultResponse = service.getPaymentSearchResult(taxRef, None, None).futureValue

      result shouldBe multiplePaymentSearchResult

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])

    }

    "return a transformed list of PaymentSearchResults when fields are missing" in new Setup {
      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsMultiplePaymentSearchResultMissingFields))
      val result: PaymentSearchResultResponse = service.getPaymentSearchResult(taxRef, None, None).futureValue

      result shouldBe multiplePaymentSearchResultMissingFields

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])

    }

    "return an emptyList if the connector returns an empty CompanyDetails" in new Setup {

      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsEmptyPaymentSearchResult))

      val result: PaymentSearchResultResponse = service.getPaymentSearchResult(taxRef, None, None).futureValue

      result shouldBe PaymentSearchResultResponse(paymentSearchResult = List.empty)

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])
    }

    "propagates exceptions from connector" in new Setup {
      val ex = new RuntimeException("Error from Downstream")

      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.failed(ex))

      val result: Exception = intercept[Exception] {
        service.getPaymentSearchResult(taxRef, None, None).futureValue
      }

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])

    }
  }

}
