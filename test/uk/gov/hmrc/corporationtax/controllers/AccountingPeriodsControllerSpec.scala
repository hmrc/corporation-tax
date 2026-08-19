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
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.AccountingPeriodsHelper
import uk.gov.hmrc.corporationtax.models.AccountingPeriods
import uk.gov.hmrc.corporationtax.services.AccountingPeriodsService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodsControllerSpec extends AnyWordSpec with Matchers with AccountingPeriodsHelper {

  private trait Setup {
    val mockAccountingPeriodsService: AccountingPeriodsService = mock[AccountingPeriodsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/accounting-periods")
    val controller                                       = new AccountingPeriodsController(Helpers.stubControllerComponents(), mockAccountingPeriodsService)
  }

  "GET /accounting-periods" should {

    "return 200 and a successful response with multiple object in the list of AccountingPeriods " in new Setup {

      val accPeriodResponse: AccountingPeriods = accountingPeriods(
        BigDecimal(1000.88),
        BigDecimal(9875.89),
        BigDecimal(-100058.25),
        BigDecimal(0.00),
        BigDecimal(34534342.36),
        BigDecimal(-1200.00)
      )
      when(mockAccountingPeriodsService.getAccountingPeriod(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(accPeriodResponse))

      val result: Future[Result] = controller.getAccountingPeriods(1L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(accPeriodResponse)

      verify(mockAccountingPeriodsService).getAccountingPeriod(eqTo(1L))(any[HeaderCarrier])
    }
    "return 200 and a successful response with empty Response of AccountingPeriods " in new Setup {

      val accPeriodResponse: AccountingPeriods = emptyAccountingPeriods

      when(mockAccountingPeriodsService.getAccountingPeriod(any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(accPeriodResponse))

      val result: Future[Result] = controller.getAccountingPeriods(1L)(fakeRequest)

      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(accPeriodResponse)

      verify(mockAccountingPeriodsService).getAccountingPeriod(eqTo(1L))(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Setup {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockAccountingPeriodsService.getAccountingPeriod(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.getAccountingPeriods(1L)(fakeRequest)

      status(result) shouldBe Status.BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500 INTERNAL_SERVER_ERROR when there is problem with downstream services " in new Setup {
      when(mockAccountingPeriodsService.getAccountingPeriod(any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getAccountingPeriods(3L)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve AccountingPeriods"

      verify(mockAccountingPeriodsService).getAccountingPeriod(eqTo(3L))(any[HeaderCarrier])
    }

  }
}
