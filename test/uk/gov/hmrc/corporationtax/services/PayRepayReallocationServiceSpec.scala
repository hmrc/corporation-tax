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
import uk.gov.hmrc.corporationtax.connectors.PayRepayReallocationConnector
import uk.gov.hmrc.corporationtax.helpers.PayRepayReallocationHelper
import uk.gov.hmrc.corporationtax.models.PayRepayReallocationsList
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PayRepayReallocationServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with PayRepayReallocationHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: PayRepayReallocationConnector = mock[PayRepayReallocationConnector]
    val service = new PayRepayReallocationService(mockConnector)
  }

  "getTotalAmounts" should {

    "delegate to connector and successfully return payment repayment reallocation list" in new Setup {
      when(mockConnector.getTotalAmounts(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(payRepayReallocationListWithOneItem))

      val result = service.getTotalAmounts(1L, 2L).futureValue

      result shouldBe payRepayReallocationListWithOneItem

      verify(mockConnector).getTotalAmounts(1L, 2L)
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(mockConnector.getTotalAmounts(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        service.getTotalAmounts(2L, 3L).futureValue
      }

      ex.getMessage should include("error")

      verify(mockConnector).getTotalAmounts(2L, 3L)
    }
  }

}
