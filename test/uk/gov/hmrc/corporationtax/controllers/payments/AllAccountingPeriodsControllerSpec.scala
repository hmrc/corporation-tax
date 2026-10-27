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

package uk.gov.hmrc.corporationtax.controllers.payments

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.payments.AllAccountingPeriodsHelper
import uk.gov.hmrc.corporationtax.services.payments.AllAccountingPeriodsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AllAccountingPeriodsControllerSpec extends AnyWordSpec with Matchers with AllAccountingPeriodsHelper {

  private trait Setup {
    val mockService: AllAccountingPeriodsService = mock[AllAccountingPeriodsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/all-accounting-period")
    val controller  =
      new AllAccountingPeriodsController(mockService, Helpers.stubControllerComponents())
  }

  "GET /all-accounting-period" should {

    "return 200 and a successful transformed response for all accounting periods" in new Setup {
      when(mockService.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(allAccountingPeriods))

      val result: Future[Result] = controller.getAccountingPeriodList(1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(allAccountingPeriods)

      verify(mockService).getAccountingPeriodList(eqTo(1L))(
        any[HeaderCarrier]
      )
    }

    "return 200 and a successful transformed response for multiple items of all accounting periods" in new Setup {
      when(mockService.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(multipleAllAccountingPeriods))

      val result: Future[Result] = controller.getAccountingPeriodList(1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(multipleAllAccountingPeriods)

      verify(mockService).getAccountingPeriodList(eqTo(1L))(
        any[HeaderCarrier]
      )
    }

    "return 200 and a successful transformed response with empty list of all accounting periods" in new Setup {
      when(mockService.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyAllAccountingPeriods))

      val result: Future[Result] = controller.getAccountingPeriodList(1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyAllAccountingPeriods)

      verify(mockService).getAccountingPeriodList(eqTo(1L))(
        any[HeaderCarrier]
      )
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getAccountingPeriodList(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getAccountingPeriodList(1L)(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve all accounting periods"

      verify(mockService).getAccountingPeriodList(eqTo(1L))(
        any[HeaderCarrier]
      )
    }
  }
}
