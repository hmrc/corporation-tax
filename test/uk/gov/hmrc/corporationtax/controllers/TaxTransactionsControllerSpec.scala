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
import uk.gov.hmrc.corporationtax.helpers.TaxTransactionsHelper
import uk.gov.hmrc.corporationtax.services.TaxTransactionsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class TaxTransactionsControllerSpec extends AnyWordSpec with Matchers with TaxTransactionsHelper {

  private trait Fixture {
    val mockTaxTransactionsService: TaxTransactionsService = mock[TaxTransactionsService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val fakeRequest     = FakeRequest("GET", "/")
    val fakePostRequest = FakeRequest("GET", "/WrongUrl")
    val controller      = new TaxTransactionsController(Helpers.stubControllerComponents(), mockTaxTransactionsService)
  }

  "GET /" should {

    "return 200: OK" in new Fixture {
      when(mockTaxTransactionsService.getTaxTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.successful(taxTransactions))

      val result: Future[Result] = controller.getTaxTransactions(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK

      contentAsJson(result) shouldBe Json.toJson(taxTransactions)

      verify(mockTaxTransactionsService).getTaxTransactions(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    "return 500: INTERNAL_SERVER_ERROR" in new Fixture {
      when(mockTaxTransactionsService.getTaxTransactions(any(), any())(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("unexpected")))

      val result: Future[Result] = controller.getTaxTransactions(1L, 2L)(fakeRequest)
      status(result)                               shouldBe Status.INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "error").as[String] shouldBe "Failed to retrieve tax transactions"

      verify(mockTaxTransactionsService).getTaxTransactions(eqTo(1L), eqTo(2L))(any[HeaderCarrier])
    }

    // TODO: implement auth failed scenario

  }

}
