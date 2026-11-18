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

package uk.gov.hmrc.corporationtax.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.models.CommonData
import uk.gov.hmrc.corporationtax.services.CommonDataService
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class CommonDataControllerSpec extends AnyWordSpec with Matchers {

  private trait Setup {
    val mockCommonDataService: CommonDataService = mock[CommonDataService]

    private val cc                    = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest            = FakeRequest("GET", "/common-data")
    val controller             = new CommonDataController(Helpers.stubControllerComponents(), mockCommonDataService)
    val commonData: CommonData = CommonData(asOnDate = LocalDate.of(2026, 9, 15))
  }

  "GET /common-data" should {

    "return 200 and CommonData when the request succeeds" in new Setup {
      when(mockCommonDataService.getCommonData()(any[HeaderCarrier]))
        .thenReturn(Future.successful(commonData))

      val result: Future[Result] =
        controller.getCommonData(fakeRequest)

      status(result) mustBe Status.OK

      contentAsJson(result) mustBe Json.obj(
        "asOnDate" -> "2026-09-15"
      )

      verify(mockCommonDataService).getCommonData()(any[HeaderCarrier])
    }

    "return 500 when the service fails" in new Setup {
      when(mockCommonDataService.getCommonData()(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] =
        controller.getCommonData(fakeRequest)

      status(result) mustBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] mustBe
        "Failed to retrieve Common Data"

      verify(mockCommonDataService).getCommonData()(any[HeaderCarrier])
    }
  }
}
