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

package uk.gov.hmrc.corporationtax.controllers.clientExchangeProxy

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers
import play.api.test.Helpers.*
import uk.gov.hmrc.corporationtax.controllers.base.SpecBase
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientListDownloadStatusTransformed
import uk.gov.hmrc.corporationtax.services.clientExchangeProxy.{
  ClientExchangeProxyService, NoBusinessIntervalsException
}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class ClientExchangeProxyControllerSpec extends SpecBase {

  private class Setup {

    val mockService: ClientExchangeProxyService = mock[ClientExchangeProxyService]

    val controller = new ClientExchangeProxyController(cc, fakeIdentifierAction, mockService)
  }

  "GET /initiateClientListDownload" should {

    "return 200: OK and status of 1 when service.process completes successfully" in new Setup {

      val downloadStatus: ClientListDownloadStatusTransformed = ClientListDownloadStatusTransformed.Succeeded
      when(mockService.process(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(downloadStatus))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result) mustBe Status.OK

      contentAsJson(result) mustBe Json.obj("result" -> 1)

      verify(mockService).process(any())(any[HeaderCarrier])

    }

    "return 200: OK and In Progress status when service.process throws ClientListDownloadInProgressException" in new Setup {

      val downloadStatus: ClientListDownloadStatusTransformed = ClientListDownloadStatusTransformed.InProgress
      when(mockService.process(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(downloadStatus))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result) mustBe Status.OK

      contentAsJson(result) mustBe Json.obj("result" -> 0)

      verify(mockService).process(any())(any[HeaderCarrier])

    }

    "return 200: OK and In Progress status when service.process throws ClientListDownloadFailedException" in new Setup {

      val downloadStatus: ClientListDownloadStatusTransformed = ClientListDownloadStatusTransformed.Failed
      when(mockService.process(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(downloadStatus))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result) mustBe Status.OK

      contentAsJson(result) mustBe Json.obj("result" -> 2)

      verify(mockService).process(any())(any[HeaderCarrier])

    }

    "return 200: OK and Initiate download status when service.process throws SystemException" in new Setup {

      val downloadStatus: ClientListDownloadStatusTransformed = ClientListDownloadStatusTransformed.InitiateDownload
      when(mockService.process(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(downloadStatus))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result) mustBe Status.OK

      contentAsJson(result) mustBe Json.obj("result" -> -1)

      verify(mockService).process(any())(any[HeaderCarrier])

    }

    "return 500: InternalServerError and System Error status when service.process throws NoBusinessIntervalsException" in new Setup {

      when(mockService.process(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(NoBusinessIntervalsException("no business intervals")))

      val result: Future[Result] = controller.initiateClientListDownload()(fakeRequest)

      status(result) mustBe Status.INTERNAL_SERVER_ERROR

      contentAsJson(result) mustBe Json.obj("result" -> "system-error")

      verify(mockService).process(any())(any[HeaderCarrier])

    }

  }

}
