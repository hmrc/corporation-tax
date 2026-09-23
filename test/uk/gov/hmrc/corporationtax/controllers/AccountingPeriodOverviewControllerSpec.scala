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

import org.mockito.ArgumentMatchers.any
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
import uk.gov.hmrc.corporationtax.models.{AccountingPeriodOverview, MissingAccountingPeriodError}
import uk.gov.hmrc.corporationtax.services.AccountingPeriodOverviewService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodOverviewControllerSpec extends AnyWordSpec with Matchers with AccountingPeriodsHelper {

  private trait Setup {
    val mockAccountingPeriodsOverviewService: AccountingPeriodOverviewService = mock[AccountingPeriodOverviewService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val taxRef: Long    = 1L
    val accPeriod: Long = 86L

    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/accounting-period-overview")
    val controller                                       = new AccountingPeriodOverviewController(
      Helpers.stubControllerComponents(),
      mockAccountingPeriodsOverviewService
    )
  }

  "GET /getAccountingPeriodOverviewOverview" should {
    "return 200 and a successful response with AccountingPeriodOverviewResponse when service returns Right(AccountingPeriodOverviewResponse)" in new Setup {
      val accountingPeriodOverview: AccountingPeriodOverview = AccountingPeriodOverview(
        accountingPeriod = BigDecimal("202501"),
        apStartDate = LocalDate.of(2025, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
        apStatus = "Open",
        taxChargePresent = true,
        clericalIntSig = false,
        creditDebitInterestInd = true,
        taxTotal = BigDecimal("1000.88"),
        interestTotal = BigDecimal("125.45"),
        penaltyTotal = BigDecimal("200.00"),
        payslipTotal = BigDecimal("9875.89"),
        repayReallocTotal = BigDecimal("-1200.00"),
        adjustmentTotal = BigDecimal("50.00"),
        clericalCalculationFlag = false,
        taxIsDisplayNeededFlag = true,
        interestIsDisplayNeededFlag = true,
        paymentIsDisplayNeededFlag = true,
        repayReallocIsDisplayNeededFlag = true
      )
      when(mockAccountingPeriodsOverviewService.getAccountingPeriodOverview(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(accountingPeriodOverview)))

      val result: Future[Result] = controller.getAccountingPeriodOverview(taxRef, accPeriod)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(accountingPeriodOverview)

      verify(mockAccountingPeriodsOverviewService).getAccountingPeriodOverview(any(), any())(any[HeaderCarrier])
    }
    "return NOT_Found when the service returns Left(MissingAccountingPeriod) " in new Setup {

      when(mockAccountingPeriodsOverviewService.getAccountingPeriodOverview(any(), any())(any[HeaderCarrier]))
        .thenReturn(
          Future.successful(Left(MissingAccountingPeriodError("Cannot find the accountingPeriod information")))
        )

      val result: Future[Result] = controller.getAccountingPeriodOverview(taxRef, accPeriod)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND

      verify(mockAccountingPeriodsOverviewService).getAccountingPeriodOverview(any(), any())(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Setup {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockAccountingPeriodsOverviewService.getAccountingPeriodOverview(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.getAccountingPeriodOverview(taxRef, accPeriod)(fakeRequest)

      status(result) shouldBe Status.BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500 INTERNAL_SERVER_ERROR when there is problem with downstream services " in new Setup {
      when(mockAccountingPeriodsOverviewService.getAccountingPeriodOverview(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getAccountingPeriodOverview(taxRef, accPeriod)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve accountingPeriod overview"

      verify(mockAccountingPeriodsOverviewService).getAccountingPeriodOverview(any(), any())(any[HeaderCarrier])
    }

  }
}
