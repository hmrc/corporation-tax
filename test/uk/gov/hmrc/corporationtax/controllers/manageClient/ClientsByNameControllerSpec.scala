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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{Request, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.corporationtax.helpers.manageClient.AllClientsHelper
import uk.gov.hmrc.corporationtax.services.manageClient.ClientsByNameService
import uk.gov.hmrc.corporationtax.controllers.actions.FakeIdentifierAction
import uk.gov.hmrc.corporationtax.models.auth.IdentifierRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ClientsByNameControllerSpec extends AnyWordSpec with Matchers with AllClientsHelper {

  private trait Setup {
    val mockService: ClientsByNameService = mock[ClientsByNameService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest          = FakeRequest("GET", "/clients-by-name")
    val fakeIdentifierAction = FakeIdentifierAction(cc.parsers)

    val fake400IdentifierAction = new FakeIdentifierAction(cc.parsers) {
      override def invokeBlock[A](
                                   request: Request[A],
                                   block: IdentifierRequest[A] => Future[Result]
                                 ): Future[Result] =
        block(IdentifierRequest(request, "fakeCredId", None, AffinityGroup.Agent))
    }

    val controller =
      new ClientsByNameController(mockService, fakeIdentifierAction, Helpers.stubControllerComponents())
  }

  "GET /clients-by-name" should {

    "return 200 and a successful response of clients by name" in new Setup {
      when(mockService.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(allClients))

      val result: Future[Result] = controller.getClientsByName(0, -1, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(allClients)

      verify(mockService)
        .getClientsByName(
          eqTo("fake-irAgent-reference"),
          eqTo("fake-credential-id"),
          eqTo("fake-name"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response of clients by name with multiple items of client list" in new Setup {
      when(mockService.getClientsByName,(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleClientListAllClients))

      val result: Future[Result] = controller.getClientsByName(0, -1, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multipleClientListAllClients)

      verify(mockService)
        .getClientsByName(
          eqTo("fake-irAgent-reference"),
          eqTo("fake-credential-id"),
          eqTo("fake-name"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 200 and a successful response of clients by name with multiple items of client name chars" in new Setup {
      when(mockService.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleItemsClientNameAllClients))

      val result: Future[Result] = controller.getClientsByName(0, -1, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multipleItemsClientNameAllClients)

      verify(mockService)
        .getClientsByName(
          eqTo("fake-irAgent-reference"),
          eqTo("fake-credential-id"),
          eqTo("fake-name"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }

    "return 400 BadRequest" in new Setup {
      when(mockService.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleItemsClientNameAllClients))

      val controller400 =
        new ClientsByNamesController(mockService, fake400IdentifierAction, Helpers.stubControllerComponents())

      val result: Future[Result] =
        controller400.getClientsByName(0, -1, 0, "ASC")(fakeRequest)

      status(result) shouldBe Status.BAD_REQUEST

      verify(mockService, never()).getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier])

    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getClientsByName(any(), any(), any(), any(), any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] =
        controller.getClientsByName(0, -1, 0, "ASC")(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve clients by name"

      verify(mockService)
        .getClientsByName(
          eqTo("fake-irAgent-reference"),
          eqTo("fake-credential-id"),
          eqTo("fake-name"),
          eqTo(0),
          eqTo(-1),
          eqTo(0),
          eqTo("ASC")
        )(
          any[HeaderCarrier]
        )
    }
  }
}