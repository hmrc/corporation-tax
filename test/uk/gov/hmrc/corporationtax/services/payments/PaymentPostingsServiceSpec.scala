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
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentPostingsConnector
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentPostingsHelper
import uk.gov.hmrc.corporationtax.models.payments.PaymentPostingsResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentPostingsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with PaymentPostingsHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: PaymentPostingsConnector = mock[PaymentPostingsConnector]
    val service                           = new PaymentPostingsService(mockRds)
    val taxReferenceNumber: Long          = 8674L
    val zeroValue: BigDecimal             = BigDecimal(0.00)
  }

  "PaymentPostingsService.getPaymentPostings" should {
    "return get single Payment Posting" in new BaseSetup {

      when(mockRds.getPaymentPostings(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(singlePaymentPosting))

      val result: PaymentPostingsResponse =
        service.getPaymentPostings(taxReferenceNumber).futureValue

      result shouldBe singlePaymentPosting

      verify(mockRds, times(1)).getPaymentPostings(any())(any[HeaderCarrier])
    }
  }

  "return get multiple Payment Postings" in new BaseSetup {

    when(mockRds.getPaymentPostings(any())(any[HeaderCarrier]))
      .thenReturn(Future.successful(multiplePaymentPostings))

    val result: PaymentPostingsResponse =
      service.getPaymentPostings(taxReferenceNumber).futureValue

    result shouldBe multiplePaymentPostings

    verify(mockRds, times(1)).getPaymentPostings(any())(any[HeaderCarrier])
  }

  "return get empty Payment Postings" in new BaseSetup {

    when(mockRds.getPaymentPostings(any())(any[HeaderCarrier]))
      .thenReturn(Future.successful(emptyPaymentPostings))

    val result: PaymentPostingsResponse =
      service.getPaymentPostings(taxReferenceNumber).futureValue

    result shouldBe emptyPaymentPostings

    verify(mockRds, times(1)).getPaymentPostings(any())(any[HeaderCarrier])
  }

  "propagate exception and errors from connector " in new BaseSetup {
    val exception = new RuntimeException("Error in the downstream services")

    when(mockRds.getPaymentPostings(any())(any[HeaderCarrier]))
      .thenReturn(Future.failed(exception))

    val ex: RuntimeException = intercept[RuntimeException] {
      service.getPaymentPostings(taxReferenceNumber).futureValue
    }

    ex.getMessage should include("Error in the downstream services")

    verify(mockRds).getPaymentPostings(taxReferenceNumber)(hc)
  }
}
