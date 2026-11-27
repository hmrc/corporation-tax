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

package uk.gov.hmrc.corporationtax.services.overarching

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
import uk.gov.hmrc.corporationtax.connectors.overarching.EnqueueLongConnector
import uk.gov.hmrc.corporationtax.helpers.overarching.EnqueueHelper
import uk.gov.hmrc.corporationtax.models.overarching.Enqueue
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class EnqueueLongServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar with EnqueueHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: EnqueueLongConnector = mock[EnqueueLongConnector]
    val service                             = new EnqueueLongService(mockConnector)
  }

  "getEnqueueLong" should {

    "delegate to connector and successfully return enqueue long" in new Setup {
      when(
        mockConnector.getEnqueueLong(any(), any(), any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(enqueue))

      val result = service
        .getEnqueueLong(12345, "sender1", "queue1", "replyQueue1", "correlationId1", "filter1", "key1", 1, 1010)
        .futureValue

      result shouldBe enqueue

      verify(mockConnector)
        .getEnqueueLong(12345, "sender1", "queue1", "replyQueue1", "correlationId1", "filter1", "key1", 1, 1010)
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(
        mockConnector.getEnqueueLong(any(), any(), any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier])
      )
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        service
          .getEnqueueLong(65789, "sender2", "queue2", "replyQueue2", "correlationId2", "filter2", "key2", 2, 2020)
          .futureValue
      }

      ex.getMessage should include("error")

      verify(mockConnector)
        .getEnqueueLong(65789, "sender2", "queue2", "replyQueue2", "correlationId2", "filter2", "key2", 2, 2020)
    }
  }

}
