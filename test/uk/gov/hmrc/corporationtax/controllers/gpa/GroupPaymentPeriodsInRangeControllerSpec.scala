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

package uk.gov.hmrc.corporationtax.controllers.gpa

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
import uk.gov.hmrc.corporationtax.helpers.gpa.PeriodWithinRangeHelper
import uk.gov.hmrc.corporationtax.services.gpa.GroupPaymentPeriodsInRangeService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GroupPaymentPeriodsInRangeControllerSpec extends AnyWordSpec with Matchers with PeriodWithinRangeHelper {

  private trait Setup {
    val mockGroupPaymentPeriodsInRangeService: GroupPaymentPeriodsInRangeService =
      mock[GroupPaymentPeriodsInRangeService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/group-payment-periods-in-range")
    val controller  =
      new GroupPaymentPeriodsInRangeController(
        Helpers.stubControllerComponents(),
        mockGroupPaymentPeriodsInRangeService
      )
  }

  "GET /group-payment-periods-in-range" should {

    "return 200 and Period Within Range set to False" in new Setup {
      when(
        mockGroupPaymentPeriodsInRangeService.getGroupPaymentPeriodsInRange(any(), any(), any(), any())(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(periodWithinRangeFalse))

      val result: Future[Result] = controller.getGroupPaymentPeriodsInRange(10L, 1000L, 1, 1)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(periodWithinRangeFalse)

      verify(mockGroupPaymentPeriodsInRangeService)
        .getGroupPaymentPeriodsInRange(eqTo(10L), eqTo(1000L), eqTo(1), eqTo(1))(any[HeaderCarrier])
    }

    "return 200 and Period Within Range set to True" in new Setup {
      when(
        mockGroupPaymentPeriodsInRangeService.getGroupPaymentPeriodsInRange(any(), any(), any(), any())(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(periodWithinRangeTrue))

      val result: Future[Result] = controller.getGroupPaymentPeriodsInRange(20L, 1000L, 1, 1)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(periodWithinRangeTrue)

      verify(mockGroupPaymentPeriodsInRangeService)
        .getGroupPaymentPeriodsInRange(eqTo(20L), eqTo(1000L), eqTo(1), eqTo(1))(any[HeaderCarrier])
    }

    "return Error message" in new Setup {
      when(
        mockGroupPaymentPeriodsInRangeService.getGroupPaymentPeriodsInRange(any(), any(), any(), any())(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.failed(new RuntimeException("Failed to retrieve period within range")))

      val result: Future[Result] = controller.getGroupPaymentPeriodsInRange(999L, 1000L, 1, 1)(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve period within range"

      verify(mockGroupPaymentPeriodsInRangeService)
        .getGroupPaymentPeriodsInRange(eqTo(999L), eqTo(1000L), eqTo(1), eqTo(1))(any[HeaderCarrier])
    }

  }
}
