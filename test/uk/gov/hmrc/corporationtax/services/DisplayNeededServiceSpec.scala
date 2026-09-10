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

package uk.gov.hmrc.corporationtax.Services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, verifyNoMoreInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.DisplayNeededConnector
import uk.gov.hmrc.corporationtax.helpers.DisplayNeededHelper
import uk.gov.hmrc.corporationtax.services.DisplayNeededService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DisplayNeededServiceSpec
  extends AnyWordSpec
    with Matchers
    with DisplayNeededHelper
    with ScalaFutures {

  private trait Fixture {
    val mockDisplayNeededConnector: DisplayNeededConnector = mock[DisplayNeededConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service =
      new DisplayNeededService(mockDisplayNeededConnector)

  }

  "getDisplay returns display needed flags all set to No" in new Fixture {
    when(mockDisplayNeededConnector.getDisplayNeeded(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(displayNeededResponseAllFalse))

    val result = service.getDisplayNeeded(10L, 1L).futureValue

    result shouldBe displayNeededAllFalse

    verify(mockDisplayNeededConnector).getDisplayNeeded(10L, 1L)(hc)
  }

  "getDisplay returns display needed flags all set to Yes" in new Fixture {
    when(mockDisplayNeededConnector.getDisplayNeeded(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(displayNeededResponseAllTrue))

    val result = service.getDisplayNeeded(20L, 9L).futureValue

    result shouldBe displayNeededAllTrue

    verify(mockDisplayNeededConnector).getDisplayNeeded(20L, 9L)(hc)
  }

  "getDisplay returns display needed with some flags set to No/Yes" in new Fixture {
    when(mockDisplayNeededConnector.getDisplayNeeded(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(displayNeededResponseMixed))

    val result = service.getDisplayNeeded(30L, 1L).futureValue

    result shouldBe displayNeededMixed

    verify(mockDisplayNeededConnector).getDisplayNeeded(30L, 1L)(hc)
  }

  "getPayments returns failure from connector" in new Fixture {

    val ex = new RuntimeException("Failed to retrieve display needed")

    when(mockDisplayNeededConnector.getDisplayNeeded(any(), any())(any[HeaderCarrier])).thenReturn(Future.failed(ex))

    val result: Throwable = service.getDisplayNeeded(999L, 1L).failed.futureValue

    result shouldBe ex

    verify(mockDisplayNeededConnector).getDisplayNeeded(999L, 1L)(hc)
    verifyNoMoreInteractions(mockDisplayNeededConnector)

  }

}
