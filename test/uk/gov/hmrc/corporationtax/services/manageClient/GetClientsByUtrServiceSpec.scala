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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.manageClient.GetClientsByUtrRdsProxyConnector
import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetClientsByUtrServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: GetClientsByUtrRdsProxyConnector = mock[GetClientsByUtrRdsProxyConnector]
    val service                                   = new GetClientsByUtrService(mockRds)

    val irAgentReference = "SN003"
    val credentialId     = "637763"
    val utr              = "HX7373"

    val allClients: AllClients = AllClients(
      clientCount = 3,
      clientList = List(
        ClientList(
          utr = "1234567890",
          companyRegNo = Some("01234567"),
          companyName = "ABC Limited",
          agentOwnRef = Some("AGENT001")
        ),
        ClientList(
          utr = "9876543210",
          companyRegNo = None,
          companyName = "XYZ Solutions Ltd",
          agentOwnRef = Some("AGENT002")
        ),
        ClientList(
          utr = "5678901234",
          companyRegNo = Some("09876543"),
          companyName = "Global Technologies Ltd",
          agentOwnRef = None
        )
      ),
      clientNameStartChars = List("A", "G", "X")
    )
  }

  "GetClientByUtrService.getClientByUtr" should {

    "delegate to connector and successfully return allClients" in new BaseSetup {

      when(mockRds.getClientByUtr(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(allClients))

      val result: AllClients = service.getClientByUtr(irAgentReference, credentialId, utr).futureValue

      result shouldBe allClients

      verify(mockRds).getClientByUtr(any(), any(), any())(any[HeaderCarrier])

      verify(mockRds, times(1)).getClientByUtr(any(), any(), any())(any[HeaderCarrier])

    }
    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getClientByUtr(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getClientByUtr(irAgentReference, credentialId, utr).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getClientByUtr(any(), any(), any())(any[HeaderCarrier])

    }
  }
}
