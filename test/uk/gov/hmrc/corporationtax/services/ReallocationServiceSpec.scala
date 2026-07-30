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

package uk.gov.hmrc.corporationtax.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.ReallocationsConnector
import uk.gov.hmrc.corporationtax.helpers.ReallocationDataHelpers
import uk.gov.hmrc.corporationtax.models.Reallocations
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}


class ReallocationServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with ReallocationDataHelpers {

  private trait Fixture {
    val mockReallocationsConnector: ReallocationsConnector = mock[ReallocationsConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service = new ReallocationService(mockReallocationsConnector)

  }

  "getByAccountingPeriod returns list of Reallocations from connector" in new Fixture {

    when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(reallocationsTwoItems))

    val result: Reallocations = service.getByAccountingPeriod(1L, 1L).futureValue

    result shouldBe reallocationsExpected

    verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
  }


}
