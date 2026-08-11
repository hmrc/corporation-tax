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
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, Result}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.helpers.ReallocationFromAccPeriodHelper
import uk.gov.hmrc.corporationtax.models.{MiscellaneousTransfer, TransformedReallocationFromAccPeriod}
import uk.gov.hmrc.corporationtax.services.ReallocationFromAccPeriodService
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class ReallocationFromAccPeriodDetailsControllerSpec
    extends AnyWordSpec
    with Matchers
    with ReallocationFromAccPeriodHelper {

  private trait Fixture {
    val mockService: ReallocationFromAccPeriodService = mock[ReallocationFromAccPeriodService]
    private val cc: ControllerComponents              = stubControllerComponents()

    implicit val ec: ExecutionContext                    = cc.executionContext
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/reallocation-from-accounting-period")
    val controller                                       = new ReallocationFromAccPeriodController(cc, mockService)
    val taxReferenceNumber: Long                         = 1234567L
    val accPeriod: Long                                  = 3456L

  }

  "GET /reallocationFromAccPeriod " should {

    "return 200: OK" in new Fixture {

      val response: TransformedReallocationFromAccPeriod =
        transformedReallocationFromAccPeriod(BigDecimal(45876.87), "2026-12-03", "99", MiscellaneousTransfer)
      when(mockService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(response))

      val result: Future[Result] = controller.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(response)

      verify(mockService).getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier])
    }
    "return 200: OK for empty response " in new Fixture {
      when(mockService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyTransformedListReallocationFromAccPeriod))

      val result: Future[Result] = controller.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(emptyTransformedListReallocationFromAccPeriod)

      verify(mockService).getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier])
    }

    "returns status code BAD_GATEWAY when Upstream error is returned" in new Fixture {
      val err: UpstreamErrorResponse = UpstreamErrorResponse("Rds-cache service unavailable", BAD_GATEWAY, BAD_GATEWAY)

      when(mockService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(err))

      val result: Future[Result] = controller.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)(fakeRequest)

      status(result) shouldBe BAD_GATEWAY

      (contentAsJson(result) \ "message").as[String] shouldBe "Rds-cache service unavailable"
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockService.getReallocationFromAccPeriod(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)(fakeRequest)

      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve reallocationFromAccPeriod"

      verify(mockService).getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier])
    }

  }

}
