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
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.RepaymentsConnector
import uk.gov.hmrc.corporationtax.helpers.RepaymentsHelper
import uk.gov.hmrc.corporationtax.models.{Repayments, RepaymentsDetails}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class RepaymentsServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with RepaymentsHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: RepaymentsConnector = mock[RepaymentsConnector]
    val service                            = new RepaymentsService(mockConnector)
  }

  "getRepayments" should {

    "delegate to connector and successfully return transformed repayment list with one item" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(beforeTransformedRepaymentsWithOneItem))

      val result = service.getRepayments(1L, 2L).futureValue

      result shouldBe afterTransformedRepaymentsWithOneItem

      verify(mockConnector).getRepayments(1L, 2L)
    }

    "delegate to connector and successfully return transformed repayment list with multiple items" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(beforeTransformedRepaymentsWithMultipleItems))

      val result = service.getRepayments(1L, 2L).futureValue

      result shouldBe afterTransformedRepaymentsWithMultipleItems

      verify(mockConnector).getRepayments(1L, 2L)
    }

    "delegate to connector and successfully return transformed repayment list when amount is None and transformed to 0" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Repayments(
          List(
            RepaymentsDetails(
              amount = None,
              repaymentType = "P",
              repaymentDate = LocalDate.of(2026, 7, 24)
            )
          )))
        )

      val result = service.getRepayments(1L, 2L).futureValue

      result shouldBe Repayments(
        List(
          RepaymentsDetails(
            amount = Some(0),
            repaymentType = "P",
            repaymentDate = LocalDate.of(2026, 7, 24)
          )
        ))

      verify(mockConnector).getRepayments(1L, 2L)
    }

    "delegate to connector and successfully return transformed repayment list when amount > 0 and transformed repaymentType = CRT" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Repayments(
          List(
            RepaymentsDetails(
              amount = Some(100),
              repaymentType = "I",
              repaymentDate = LocalDate.of(2026, 7, 24)
            )
          )))
        )

      val result = service.getRepayments(1L, 2L).futureValue

      result shouldBe Repayments(
        List(
          RepaymentsDetails(
            amount = Some(-100),
            repaymentType = "CRT",
            repaymentDate = LocalDate.of(2026, 7, 24)
          )
        ))

      verify(mockConnector).getRepayments(1L, 2L)
    }

    "delegate to connector and successfully return transformed repayment list where amount is negated" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Repayments(
          List(
            RepaymentsDetails(
              amount = Some(-50),
              repaymentType = "I",
              repaymentDate = LocalDate.of(2026, 7, 24)
            )
          )))
        )

      val result = service.getRepayments(1L, 2L).futureValue

      result shouldBe Repayments(
        List(
          RepaymentsDetails(
            amount = Some(50),
            repaymentType = "I",
            repaymentDate = LocalDate.of(2026, 7, 24)
          )
        ))

      verify(mockConnector).getRepayments(1L, 2L)
    }

    "delegate to connector and successfully return empty repayment list" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyRepayments))

      val result = service.getRepayments(1L, 2L).futureValue

      result shouldBe emptyRepayments

      verify(mockConnector).getRepayments(1L, 2L)
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(mockConnector.getRepayments(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        service.getRepayments(2L, 3L).futureValue
      }

      ex.getMessage should include("error")

      verify(mockConnector).getRepayments(2L, 3L)
    }
  }

}
