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

package uk.gov.hmrc.corporationtax.services.manageClient

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
import uk.gov.hmrc.corporationtax.connectors.manageClient.ClientsByNameConnector
import uk.gov.hmrc.corporationtax.helpers.manageClient.AllClientsHelper
import uk.gov.hmrc.corporationtax.models.manageClient.AllClients
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ClientsByNameServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with AllClientsHelper {

  private trait Setup {
    private val cc: ControllerComponents = stubControllerComponents()
    implicit val hc: HeaderCarrier       = HeaderCarrier()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockConnector: ClientsByNameConnector = mock[ClientsByNameConnector]
    val service                               = new ClientsByNameService(mockConnector)
  }

  "getClientsByName" should {

    "delegate to connector and successfully return clients by name" in new Setup {
      when(mockConnector.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(allClients))

      val result = service.getClientsByName("irAgentId1", "credentialId1", "name1", 0, -1, 0, "ASC").futureValue

      result shouldBe allClients

      verify(mockConnector).getClientsByName("irAgentId1", "credentialId1", "name1", 0, -1, 0, "ASC")
    }

    "delegate to connector and successfully return multiple items of client list in clients by name" in new Setup {
      when(mockConnector.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleClientListAllClients))

      val result = service.getClientsByName("irAgentId2", "credentialId2", "name2", 0, -1, 0, "ASC").futureValue

      result shouldBe multipleClientListAllClients

      verify(mockConnector).getClientsByName("irAgentId2", "credentialId2", "name2", 0, -1, 0, "ASC")
    }

    "delegate to connector and successfully return multiple items of client name chars in clients by name" in new Setup {
      when(mockConnector.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleItemsClientNameAllClients))

      val result = service.getClientsByName("irAgentId3", "credentialId3", "name3", 0, -1, 0, "ASC").futureValue

      result shouldBe multipleItemsClientNameAllClients

      verify(mockConnector).getClientsByName("irAgentId3", "credentialId3", "name3", 0, -1, 0, "ASC")
    }

    "propagate any errors or exceptions from connector" in new Setup {
      when(mockConnector.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val ex = intercept[RuntimeException] {
        service.getClientsByName("irAgentIdNA", "credentialIdNA", "nameNA", 0, -1, 0, "ASC").futureValue
      }

      ex.getMessage should include("error")

      verify(mockConnector).getClientsByName("irAgentIdNA", "credentialIdNA", "nameNA", 0, -1, 0, "ASC")
    }
  }

}
