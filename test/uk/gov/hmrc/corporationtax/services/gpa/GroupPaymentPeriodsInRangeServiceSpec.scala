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

package uk.gov.hmrc.corporationtax.Services.gpa

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, verifyNoMoreInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.gpa.GroupPaymentPeriodsInRangeConnector
import uk.gov.hmrc.corporationtax.helpers.gpa.PeriodWithinRangeHelper
import uk.gov.hmrc.corporationtax.models.gpa.PeriodWithinRange
import uk.gov.hmrc.corporationtax.services.gpa.GroupPaymentPeriodsInRangeService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GroupPaymentPeriodsInRangeServiceSpec
    extends AnyWordSpec
    with Matchers
    with PeriodWithinRangeHelper
    with ScalaFutures {

  private trait Fixture {
    val mockGroupPaymentPeriodsInRangeConnector: GroupPaymentPeriodsInRangeConnector =
      mock[GroupPaymentPeriodsInRangeConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service =
      new GroupPaymentPeriodsInRangeService(mockGroupPaymentPeriodsInRangeConnector)

  }

  "getGroupPaymentPeriodsInRange returns Period Within Range set to No" in new Fixture {
    when(
      mockGroupPaymentPeriodsInRangeConnector.getGroupPaymentPeriodsInRange(any[Long], any[Long], any[Int], any[Int])(
        any[HeaderCarrier]
      )
    )
      .thenReturn(Future.successful(periodWithinRangeResponseFalse))

    val result: PeriodWithinRange = service.getGroupPaymentPeriodsInRange(10L, 1000L, 1, 1).futureValue

    result shouldBe periodWithinRangeFalse

    verify(mockGroupPaymentPeriodsInRangeConnector).getGroupPaymentPeriodsInRange(10L, 1000L, 1, 1)(hc)
  }

  "getGroupPaymentPeriodsInRange returns Period Within Range set to Yes" in new Fixture {
    when(
      mockGroupPaymentPeriodsInRangeConnector.getGroupPaymentPeriodsInRange(any[Long], any[Long], any[Int], any[Int])(
        any[HeaderCarrier]
      )
    )
      .thenReturn(Future.successful(periodWithinRangeResponseTrue))

    val result: PeriodWithinRange = service.getGroupPaymentPeriodsInRange(20L, 1000L, 1, 1).futureValue

    result shouldBe periodWithinRangeTrue

    verify(mockGroupPaymentPeriodsInRangeConnector).getGroupPaymentPeriodsInRange(20L, 1000L, 1, 1)(hc)
  }

  "getPayments returns failure from connector" in new Fixture {

    val ex = new RuntimeException("Error while retrieving period within range")

    when(
      mockGroupPaymentPeriodsInRangeConnector.getGroupPaymentPeriodsInRange(any(), any(), any(), any())(
        any[HeaderCarrier]
      )
    ).thenReturn(Future.failed(ex))

    val result: Throwable = service.getGroupPaymentPeriodsInRange(999L, 1000L, 1, 1).failed.futureValue

    result shouldBe ex

    verify(mockGroupPaymentPeriodsInRangeConnector).getGroupPaymentPeriodsInRange(999L, 1000L, 1, 1)(hc)
    verifyNoMoreInteractions(mockGroupPaymentPeriodsInRangeConnector)

  }

}
