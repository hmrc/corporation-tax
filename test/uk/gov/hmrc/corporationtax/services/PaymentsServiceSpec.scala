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

package uk.gov.hmrc.corporationtax.Services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, verifyNoMoreInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.corporationtax.connectors.PaymentsConnector
import uk.gov.hmrc.corporationtax.helpers.PaymentsHelper
import uk.gov.hmrc.corporationtax.models.Payments
import uk.gov.hmrc.corporationtax.services.PaymentsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentsServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar with PaymentsHelper {
  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global
  private class Setup {
    val mockConnector: PaymentsConnector = mock[PaymentsConnector]
    val service                          = new PaymentsService(mockConnector)

  }

  "getPayments returns list of Payments Transactions retrieved from connector" in new Setup {

    when(mockConnector.getPayments(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(paymentTransactions))

    val result: Payments = service.getPayments(taxRef, accPeriod).futureValue

    result mustBe paymentTransactionsTransformed

    verify(mockConnector).getPayments(taxRef, accPeriod)(hc)
  }

  "getPayments returns and empty list if an empty list is returned from connector" in new Setup {

    when(mockConnector.getPayments(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(emptyPaymentTransactions))

    val result: Payments = service.getPayments(taxRef, accPeriod).futureValue

    result mustBe emptyPaymentTransactions

    verify(mockConnector).getPayments(taxRef, accPeriod)(hc)

  }

  "getPayments returns a single item list with transformed amount" in new Setup {
    when(mockConnector.getPayments(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(paymentTransactionsSingleItemList))

    val result: Payments = service.getPayments(taxRef, accPeriod).futureValue

    result mustBe paymentTransactionsSingleItemListTransformed

    verify(mockConnector).getPayments(taxRef, accPeriod)(hc)
  }

  "getPayments returns failure from connector" in new Setup {

    val ex = new RuntimeException("boom")

    when(mockConnector.getPayments(any(), any())(any[HeaderCarrier])).thenReturn(Future.failed(ex))

    val result: Throwable = service.getPayments(taxRef, accPeriod).failed.futureValue

    result mustBe ex

    verify(mockConnector).getPayments(taxRef, accPeriod)(hc)
    verifyNoMoreInteractions(mockConnector)

  }

}
