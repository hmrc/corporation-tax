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
import uk.gov.hmrc.corporationtax.connectors.CommonDataConnector
import uk.gov.hmrc.corporationtax.models.CommonData
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.Future

class CommonDataServiceSpec extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  private trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val mockConnector: CommonDataConnector = mock[CommonDataConnector]
    val service                            = new CommonDataService(mockConnector)
    val commonData: CommonData             = CommonData(asOnDate = LocalDate.of(2026, 9, 15))
  }

  "getCommonData" should {

    "return CommonData from connector" in new Setup {
      when(mockConnector.getCommonData()(any[HeaderCarrier]))
        .thenReturn(Future.successful(commonData))

      val result: CommonData = service.getCommonData().futureValue

      result mustBe commonData

      verify(mockConnector).getCommonData()(hc)
      verifyNoMoreInteractions(mockConnector)
    }

    "return failure when the connector fails" in new Setup {
      val ex = new RuntimeException("Failed to retrieve Common Data")

      when(mockConnector.getCommonData()(any[HeaderCarrier]))
        .thenReturn(Future.failed(ex))

      val result: Throwable = service.getCommonData().failed.futureValue

      result mustBe ex

      verify(mockConnector).getCommonData()(hc)
      verifyNoMoreInteractions(mockConnector)
    }
  }
}
