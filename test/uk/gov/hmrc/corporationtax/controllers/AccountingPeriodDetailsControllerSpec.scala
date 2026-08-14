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
import uk.gov.hmrc.corporationtax.helpers.AccountingPeriodDetailsHelper
import uk.gov.hmrc.corporationtax.models.AccountingPeriodDetailsResponse
import uk.gov.hmrc.corporationtax.services.AccountingPeriodDetailsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodDetailsControllerSpec extends AnyWordSpec with Matchers with AccountingPeriodDetailsHelper {

  private trait Setup {
    val mockAccountingPeriodDetailsService: AccountingPeriodDetailsService = mock[AccountingPeriodDetailsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/accounting-period-details")
    val controller  =
      new AccountingPeriodDetailsController(Helpers.stubControllerComponents(), mockAccountingPeriodDetailsService)
  }

  "GET /accounting-period-details" should {

    "return 200 and a successful response with one item transformed amounts" in new Setup {
      when(mockAccountingPeriodDetailsService.getAccountingDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(AccountingPeriodDetailsResponse(accountingPeriodDetails)))

      val result: Future[Result] = controller.getAccountingPeriodDetails(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(AccountingPeriodDetailsResponse(accountingPeriodDetails))

      verify(mockAccountingPeriodDetailsService).getAccountingDetails(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockAccountingPeriodDetailsService.getAccountingDetails(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getAccountingPeriodDetails(3L, 4L)(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve accounting period details"

      verify(mockAccountingPeriodDetailsService).getAccountingDetails(eqTo(3L), eqTo(4L))(any[HeaderCarrier])
    }

  }
}
