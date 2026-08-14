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

import org.mockito.Mockito
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.AdjustmentTransactionsConnector
import uk.gov.hmrc.corporationtax.helpers.AdjustmentTransactionsHelper
import uk.gov.hmrc.corporationtax.models.{AdjustmentTransactions, AdjustmentTransactionsList}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AdjustmentTransactionsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with AdjustmentTransactionsHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockAdjustmentTransactionsConnector: AdjustmentTransactionsConnector = mock[AdjustmentTransactionsConnector]
    val adjustmentTransactionsService                                        = new AdjustmentTransactionsService(mockAdjustmentTransactionsConnector)
  }

  "getAdjustmentTransactions" should {

    "delegate to connector and successfully return transformed AdjustmentTransactionsList with one item" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(adjustmentTransactionsListWithOneItem))

      val result = adjustmentTransactionsService.getAdjustmentTransactions(1L, 2L).futureValue

      result shouldBe transformedAdjustmentTransactionsListWithOneItem

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(1L, 2L)
    }

    "delegate to connector and successfully return transformed AdjustmentTransactionsList with multiple items" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(adjustmentTransactionsListWithMultipleItems))

      val result = adjustmentTransactionsService.getAdjustmentTransactions(1L, 2L).futureValue

      result shouldBe transformedAdjustmentTransactionsListWithMultipleItems

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(1L, 2L)
    }

    "delegate to connector and successfully return empty AdjustmentTransactionsList" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyAdjustmentTransactionsList))

      val result = adjustmentTransactionsService.getAdjustmentTransactions(1L, 2L).futureValue

      result shouldBe emptyAdjustmentTransactionsList

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(1L, 2L)
    }

    "delegate to connector and successfully return transformed AdjustmentTransactionsList with negated value" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(
          AdjustmentTransactionsList(
          List(
            AdjustmentTransactions(
              amount = BigDecimal(50.00),
              `type` = "N"
            )
          )
        )))

      val result = adjustmentTransactionsService.getAdjustmentTransactions(1L, 2L).futureValue

      result shouldBe AdjustmentTransactionsList(
        List(
          AdjustmentTransactions(
            amount = BigDecimal(-50.00),
            `type` = "N"
          )
        )
      )

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(1L, 2L)
    }

    "delegate to connector and successfully return transformed AdjustmentTransactionsList with correct rounding" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(
          AdjustmentTransactionsList(
            List(
              AdjustmentTransactions(
                amount = BigDecimal(50.1245),
                `type` = "N"
              )
            )
          )))

      val result = adjustmentTransactionsService.getAdjustmentTransactions(1L, 2L).futureValue

      result shouldBe AdjustmentTransactionsList(
        List(
          AdjustmentTransactions(
            amount = BigDecimal(-50.12),
            `type` = "N"
          )
        )
      )

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(1L, 2L)
    }

    "delegate to connector and successfully return transformed AdjustmentTransactionsList with not negating 0 value" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(
          AdjustmentTransactionsList(
            List(
              AdjustmentTransactions(
                amount = BigDecimal(0.00001),
                `type` = "N"
              )
            )
          )))

      val result = adjustmentTransactionsService.getAdjustmentTransactions(1L, 2L).futureValue

      result shouldBe AdjustmentTransactionsList(
        List(
          AdjustmentTransactions(
            amount = BigDecimal(0),
            `type` = "N"
          )
        )
      )

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(1L, 2L)
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(mockAdjustmentTransactionsConnector.getAdjustmentTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        adjustmentTransactionsService.getAdjustmentTransactions(2L, 3L).futureValue
      }

      ex.getMessage should include("error")

      verify(mockAdjustmentTransactionsConnector).getAdjustmentTransactions(2L, 3L)
    }
  }

}
