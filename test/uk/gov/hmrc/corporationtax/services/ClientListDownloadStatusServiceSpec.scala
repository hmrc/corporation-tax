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
import uk.gov.hmrc.corporationtax.connectors.ClientListDownloadStatusConnector
import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ClientListDownloadStatusServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {
  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global
  private class Setup {
    val mockConnector: ClientListDownloadStatusConnector = mock[ClientListDownloadStatusConnector]
    val service                                          = new ClientListDownloadStatusService(mockConnector)
    val initiateDownload: ClientDownloadStatus           = ClientDownloadStatus(-1)

  }

  "getClientListDownloadStatus returns Client List Download Status retrieved from connector" in new Setup {

    when(mockConnector.getClientListDownloadStatus(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(initiateDownload))

    val result: ClientDownloadStatus = service.getClientListDownloadStatus("1").futureValue

    result mustBe initiateDownload

    verify(mockConnector).getClientListDownloadStatus("1")(hc)
  }

  "getClientListDownloadStatus returns failure from connector" in new Setup {

    val ex = new RuntimeException("boom")

    when(mockConnector.getClientListDownloadStatus(any())(any[HeaderCarrier])).thenReturn(Future.failed(ex))

    val result: Throwable = service.getClientListDownloadStatus("1").failed.futureValue

    result mustBe ex

    verify(mockConnector).getClientListDownloadStatus("1")(hc)
    verifyNoMoreInteractions(mockConnector)

  }

}
