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

package uk.gov.hmrc.corporationtax.controllers.manageClient

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{verify, verifyNoInteractions, when}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.controllers.base.SpecBase
import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}
import uk.gov.hmrc.corporationtax.services.manageClient.ClientsByAgentOwnRefService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ClientsByAgentOwnRefControllerSpec extends SpecBase {

  private trait Fixture {
    val mockService: ClientsByAgentOwnRefService = mock[ClientsByAgentOwnRefService]
    private val cc: ControllerComponents         = stubControllerComponents()

    implicit val ec: ExecutionContext                    = cc.executionContext
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/clients-by-agent-own-ref")
    val allClients: AllClients                           = AllClients(
      clientCount = 3,
      clientList = List(
        ClientList(
          utr = "1234567890",
          companyRegNo = Some("01234567"),
          companyName = "ABC Limited",
          agentOwnRef = Some("CTR692001")
        ),
        ClientList(
          utr = "9876543210",
          companyRegNo = None,
          companyName = "XYZ Solutions Ltd",
          agentOwnRef = Some("CTR692001")
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
    val noResponse: AllClients                           = AllClients(
      clientCount = 0,
      clientList = List.empty,
      clientNameStartChars = List.empty
    )
    val controller                                       = new ClientsByAgentOwnRefController(cc, fakeIdentifierAction, mockService)
    val clientId                                         = "CTR692001"

  }

  "GET /clients-by-agent-own-ref " should {

    "return 200: OK with multiple Client List " in new Fixture {
      when(mockService.getClientsByAgentOwnRef(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(allClients))

      val result: Future[Result] = controller.getClientsByAgentOwnRef(clientId, 0, -1, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(allClients)

      verify(mockService)
        .getClientsByAgentOwnRef(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo("CTR692001"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }
    "return 200: OK for empty Client List and clientNameStartChars response " in new Fixture {

      val emptyAllClients: AllClients = AllClients(
        clientCount = 0,
        clientList = List.empty[ClientList],
        clientNameStartChars = List.empty[String]
      )
      when(mockService.getClientsByAgentOwnRef(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyAllClients))

      val result: Future[Result] = controller.getClientsByAgentOwnRef(clientId, 0, -1, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyAllClients)

      verify(mockService)
        .getClientsByAgentOwnRef(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo("CTR692001"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockService.getClientsByAgentOwnRef(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getClientsByAgentOwnRef(clientId, 0, -1, 0, "ASC")(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve all clients by agent own ref"

      verify(mockService)
        .getClientsByAgentOwnRef(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo("CTR692001"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }
    "return 400: BAD_REQUEST when IRAgentReference has not been provided" in new Fixture {
      override val controller: ClientsByAgentOwnRefController =
        new ClientsByAgentOwnRefController(cc, fakeIdentifierActionWithoutAgentReference, mockService)

      when(mockService.getClientsByAgentOwnRef(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(noResponse))

      val result: Future[Result] = controller.getClientsByAgentOwnRef(clientId, 0, -1, 0, "ASC")(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "message" -> "Request could not be processed: the IR agent reference and/or credential ID is missing from the authenticated session"
      )

      verifyNoInteractions(mockService)
    }

  }

}
