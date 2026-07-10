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
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.corporationtax.connectors.PenaltiesConnector
import uk.gov.hmrc.corporationtax.models.{Penalties, PenaltyTransaction}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class PenaltiesControllerSpec extends AnyWordSpec with Matchers {

  private trait Fixture {
    val mockPenaltiesConnector: PenaltiesConnector = mock[PenaltiesConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val fakeRequest = FakeRequest("GET", "/")
    val controller  = new PenaltiesController(Helpers.stubControllerComponents(), mockPenaltiesConnector)
  }

  val penalties =
    Right(
      Penalties(
        List(
          PenaltyTransaction(penaltyDate = LocalDate.of(2025, 5, 1), `type` = "F", postingAmount = BigDecimal(100.13)),
          PenaltyTransaction(penaltyDate = LocalDate.of(2021, 3, 7), `type` = "G", postingAmount = BigDecimal(27.19))
        )
      )
    )

  "GET /" should {
    "return 200" in new Fixture {
      when(mockPenaltiesConnector.getPenaltyTransactionList(any(), any()))
        .thenReturn(Future.successful(penalties))

      val result = controller.getPenaltyTransactionList(1L, 2L)(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

}
