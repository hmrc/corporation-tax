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
import org.mockito.Mockito.{verify, verifyNoMoreInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.corporationtax.connectors.TaxTransactionsConnector
import uk.gov.hmrc.corporationtax.helpers.TaxTransactionsHelper
import uk.gov.hmrc.corporationtax.models.TaxTransactions
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class TaxTransactionsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with TaxTransactionsHelper {
  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global
  private class Setup {
    val mockConnector: TaxTransactionsConnector = mock[TaxTransactionsConnector]
    val service                                 = new TaxTransactionsService(mockConnector)

  }

  "getTaxTransactions returns list of Tax Transactions retrieved from connector" in new Setup {

    when(mockConnector.getTaxTransactions(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(taxTransactions))

    val result: TaxTransactions = service.getTaxTransactions(taxRef, accPeriod).futureValue

    result mustBe taxTransactionsTransformedAndSorted

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)
  }

  "getTaxTransactions returns and empty list if an empty list is returned from connector" in new Setup {

    when(mockConnector.getTaxTransactions(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(emptyTaxTransactions))

    val result: TaxTransactions = service.getTaxTransactions(taxRef, accPeriod).futureValue

    result mustBe emptyTaxTransactions

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)

  }

  "getTaxTransactions returns a single item list with transformed current amount" in new Setup {
    when(mockConnector.getTaxTransactions(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(taxTransactionsSingleItemList))

    val result: TaxTransactions = service.getTaxTransactions(taxRef, accPeriod).futureValue

    result mustBe taxTransactionsSingleItemListTransformed

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)
  }

  "getTaxTransactions returns a multiple item list with first zero amount of assessment type only kept" in new Setup {
    when(mockConnector.getTaxTransactions(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(taxTransactionsZeroAmountAssessments))

    val result: TaxTransactions = service.getTaxTransactions(taxRef, accPeriod).futureValue

    val countByType: Map[String, Int] =
      result.taxTransactions.groupBy(_.assessmentType).view.mapValues(_.size).toMap

    countByType mustBe Map("M" -> 1, "A" -> 1, "D" -> 2)

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)
  }

  "getTaxTransactions returns and empty list if an empty list is returned from connector" in new Setup {

    when(mockConnector.getTaxTransactions(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(emptyTaxTransactions))

    val result: TaxTransactions = service.getTaxTransactions(taxRef, accPeriod).futureValue

    result mustBe emptyTaxTransactions

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)

  }

  "getTaxTransactions returns a single item list with transformed current amount" in new Setup {
    when(mockConnector.getTaxTransactions(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(taxTransactionsSingleItemList))

    val result: TaxTransactions = service.getTaxTransactions(taxRef, accPeriod).futureValue

    result mustBe taxTransactionsSingleItemListTransformed

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)
  }

  "getTaxTransactions returns failure from connector" in new Setup {

    val ex = new RuntimeException("boom")

    when(mockConnector.getTaxTransactions(any(), any())(any[HeaderCarrier])).thenReturn(Future.failed(ex))

    val result: Throwable = service.getTaxTransactions(taxRef, accPeriod).failed.futureValue

    result mustBe ex

    verify(mockConnector).getTaxTransactions(taxRef, accPeriod)(hc)
    verifyNoMoreInteractions(mockConnector)

  }

}
