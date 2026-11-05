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
import play.api.mvc.ControllerComponents
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.HasClientConnector
import uk.gov.hmrc.corporationtax.models.{HasClient, HasClientResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HasClientServiceSpec extends AnyWordSpec with Matchers with ScalaFutures {

  private trait Setup {
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    private val cc: ControllerComponents = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: HasClientConnector = mock[HasClientConnector]
    val service                           = new HasClientService(mockConnector)
    val utr: Long                         = 1234567890L
    val irAgentId: String                 = "AGENT123"
    val credentialId: String              = "0903840830080480"
  }

  "hasClient" should {

    "return true when status is 1" in new Setup {
      when(mockConnector.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(HasClient(1)))

      val result: HasClientResponse = service.hasClient(utr, irAgentId, credentialId).futureValue

      result mustBe HasClientResponse(status = true)
      verify(mockConnector).hasClient(utr, irAgentId, credentialId)(hc)
      verifyNoMoreInteractions(mockConnector)
    }

    "return false when status is 0" in new Setup {
      when(mockConnector.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(HasClient(0)))

      val result: HasClientResponse = service.hasClient(utr, irAgentId, credentialId).futureValue

      result mustBe HasClientResponse(status = false)
      verify(mockConnector).hasClient(utr, irAgentId, credentialId)(hc)
      verifyNoMoreInteractions(mockConnector)
    }

    "return false when status is any value other than 1" in new Setup {
      when(mockConnector.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(HasClient(2)))

      val result: HasClientResponse = service.hasClient(utr, irAgentId, credentialId).futureValue

      result mustBe HasClientResponse(status = false)
      verify(mockConnector).hasClient(utr, irAgentId, credentialId)(hc)
      verifyNoMoreInteractions(mockConnector)
    }

    "return failure when the connector fails" in new Setup {
      val ex = new RuntimeException("Failed to retrieve Has Client")

      when(mockConnector.hasClient(any[Long], any[String], any[String])(any[HeaderCarrier]))
        .thenReturn(Future.failed(ex))

      val result: Throwable = service.hasClient(utr, irAgentId, credentialId).failed.futureValue

      result mustBe ex
      verify(mockConnector).hasClient(utr, irAgentId, credentialId)(hc)
      verifyNoMoreInteractions(mockConnector)
    }
  }
}
