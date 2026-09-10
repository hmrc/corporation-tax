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
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.InterestAccrualListHelper
import uk.gov.hmrc.corporationtax.models.MissingStatueRule
import uk.gov.hmrc.corporationtax.services.InterestAccrualListService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class InterestAccrualListControllerSpec extends AnyWordSpec with Matchers with InterestAccrualListHelper {

  private trait Fixture {
    val mockInterestAccrualListService: InterestAccrualListService = mock[InterestAccrualListService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val taxRef: Long    = 1L
    val accPeriod: Long = 1L

    val ide: String = "IDE"

    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    val controller                                       =
      new InterestAccrualListController(Helpers.stubControllerComponents(), mockInterestAccrualListService)
  }

  "GET /" should {

    "return 200: OK for both IDE and non-IDE interestType" in new Fixture {
      when(mockInterestAccrualListService.getInterestAccrualList(any(), any(), eqTo(ide))(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(interestAccrualListWithInterestAccruedDays)))

      val result: Future[Result] = controller.getInterestAccrualList(taxRef, accPeriod, ide)(fakeRequest)

      status(result) shouldBe OK

      contentAsJson(result) shouldBe Json.toJson(interestAccrualListWithInterestAccruedDays)

      verify(mockInterestAccrualListService, times(1)).getInterestAccrualList(eqTo(taxRef), eqTo(accPeriod), eqTo(ide))(
        any[HeaderCarrier]
      )
    }
    "return NOT_FOUND when retrieving InterestAccrualListWithInterestAccruedDays fails: for IDE interestType" in new Fixture {
      when(mockInterestAccrualListService.getInterestAccrualList(any(), any(), eqTo(ide))(any[HeaderCarrier]))
        .thenReturn(Future.successful(Left(MissingStatueRule("Cannot find statue Rule"))))

      val result: Future[Result] = controller.getInterestAccrualList(taxRef, accPeriod, ide)(fakeRequest)

      status(result) shouldBe NOT_FOUND

      verify(mockInterestAccrualListService).getInterestAccrualList(eqTo(taxRef), eqTo(accPeriod), eqTo(ide))(
        any[HeaderCarrier]
      )
    }

    "return 500: INTERNAL_SERVER_ERROR when exception is returned from the service " in new Fixture {
      when(mockInterestAccrualListService.getInterestAccrualList(any(), any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getInterestAccrualList(1L, 2L, "IDE")(fakeRequest)
      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve interest Accrual list"

      verify(mockInterestAccrualListService).getInterestAccrualList(eqTo(1L), eqTo(2L), eqTo("IDE"))(
        any[HeaderCarrier]
      )
    }

  }

}
