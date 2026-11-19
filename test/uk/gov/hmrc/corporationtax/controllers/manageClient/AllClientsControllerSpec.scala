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
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.controllers.base.SpecBase
import uk.gov.hmrc.corporationtax.helpers.manageClient.AllClientsHelper
import uk.gov.hmrc.corporationtax.services.manageClient.AllClientsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AllClientsControllerSpec extends SpecBase with AllClientsHelper {

  private trait Setup {
    val mockService: AllClientsService = mock[AllClientsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/all-clients")

    val controller =
      new AllClientsController(mockService, fakeIdentifierAction, Helpers.stubControllerComponents())
  }

  "GET /all-clients" should {

    "return 200 and a successful response of all clients" in new Setup {
      when(mockService.getAllClients(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(allClients))

      val result: Future[Result] = controller.getAllClients(0, 30, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(allClients)

      verify(mockService)
        .getAllClients(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo(0),
          eqTo(30),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response of all clients with multiple items of client list" in new Setup {
      when(mockService.getAllClients(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleClientListAllClients))

      val result: Future[Result] = controller.getAllClients(0, 30, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multipleClientListAllClients)

      verify(mockService)
        .getAllClients(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo(0),
          eqTo(30),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response of all clients with multiple items of client name chars" in new Setup {
      when(mockService.getAllClients(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleItemsClientNameAllClients))

      val result: Future[Result] = controller.getAllClients(0, 30, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multipleItemsClientNameAllClients)

      verify(mockService)
        .getAllClients(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo(0),
          eqTo(30),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 400 BadRequest" in new Setup {
      when(mockService.getAllClients(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleItemsClientNameAllClients))

      val controller400 =
        new AllClientsController(
          mockService,
          fakeIdentifierActionWithoutAgentReference,
          Helpers.stubControllerComponents()
        )

      val result: Future[Result] =
        controller400.getAllClients(0, 30, 0, "ASC")(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST

      verify(mockService, never()).getAllClients(any(), any(), any(), any(), any(), any())(any[HeaderCarrier])

    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getAllClients(any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] =
        controller.getAllClients(0, 30, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve all clients"

      verify(mockService)
        .getAllClients(
          eqTo("HX12345"),
          eqTo("fake-credential-id"),
          eqTo(0),
          eqTo(30),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }
  }
}
