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
import uk.gov.hmrc.corporationtax.models.CompanyNominator
import uk.gov.hmrc.corporationtax.services.CompanyNominatorService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class CompanyNominatorControllerSpec extends AnyWordSpec with Matchers {

  private trait Setup {
    val mockService: CompanyNominatorService = mock[CompanyNominatorService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val fakeRequest = FakeRequest("GET", "/is-company-nominator")
    val controller  =
      new CompanyNominatorController(mockService, Helpers.stubControllerComponents())

    val companyNominatorTrue: CompanyNominator  = CompanyNominator(isParticipator = true)
    val companyNominatorFalse: CompanyNominator = CompanyNominator(isParticipator = false)
  }

  "GET /is-company-nominator" should {

    "return 200 and a successful response with company nominator when isParticipator is true" in new Setup {
      when(mockService.getIsCompanyNominatorOfGPA(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(companyNominatorTrue))

      val result: Future[Result] = controller.getIsCompanyNominatorOfGPA(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(companyNominatorTrue)

      verify(mockService).getIsCompanyNominatorOfGPA(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 200 and a successful response with company nominator when isParticipator is false" in new Setup {
      when(mockService.getIsCompanyNominatorOfGPA(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(companyNominatorFalse))

      val result: Future[Result] = controller.getIsCompanyNominatorOfGPA(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(companyNominatorFalse)

      verify(mockService).getIsCompanyNominatorOfGPA(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 500 INTERNAL_SERVER_ERROR" in new Setup {
      when(mockService.getIsCompanyNominatorOfGPA(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("error")))

      val result: Future[Result] = controller.getIsCompanyNominatorOfGPA(3L, 4L)(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve company nominator of GPA"

      verify(mockService).getIsCompanyNominatorOfGPA(eqTo(3L), eqTo(4L))(any[HeaderCarrier])
    }
  }
}
