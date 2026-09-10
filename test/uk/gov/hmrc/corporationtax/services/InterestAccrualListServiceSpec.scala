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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{times, verify, verifyNoInteractions, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.InterestAccrualListConnector
import uk.gov.hmrc.corporationtax.helpers.InterestAccrualListHelper
import uk.gov.hmrc.corporationtax.models.BusinessConstants.LATE_PAYMENT_INTEREST
import uk.gov.hmrc.corporationtax.models.{
  InterestAccrualListWithInterestAccruedDays, MissingDataError, MissingStatueRule
}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class InterestAccrualListServiceSpec
    extends AnyWordSpec
    with Matchers
    with InterestAccrualListHelper
    with ScalaFutures {

  private trait Fixture {
    val mockAccrualInterestListConnector: InterestAccrualListConnector = mock[InterestAccrualListConnector]

    val mockInterestAccruedDaysCalcService: InterestAccruedDaysCalculationService =
      mock[InterestAccruedDaysCalculationService]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val taxRef: Long               = 1L
    val accPeriod: Long            = 1L
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val service = new InterestAccrualListService(mockAccrualInterestListConnector, mockInterestAccruedDaysCalcService)
  }

  "getInterestAccrualList retrieves Interest Accrual List from connector and returns Right(InterestAccrualListWithInterestAccruedDays) for IDE interestType when retrieval of StatueRule is successful" in new Fixture {
    when(
      mockAccrualInterestListConnector.getInterestAccrualList(any(), any(), eqTo(LATE_PAYMENT_INTEREST))(
        any[HeaderCarrier]
      )
    )
      .thenReturn(Future.successful(interestAccrualList))

    when(
      mockInterestAccruedDaysCalcService.getInterestAccrualListWithInterestAccruedDays(
        any(),
        any(),
        any(),
        eqTo(LATE_PAYMENT_INTEREST)
      )(any[HeaderCarrier])
    )
      .thenReturn(Future.successful(Right(interestAccrualListWithInterestAccruedDays)))

    val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] =
      service.getInterestAccrualList(taxRef, accPeriod, LATE_PAYMENT_INTEREST).futureValue

    result shouldBe Right(interestAccrualListWithInterestAccruedDays)

    verify(mockAccrualInterestListConnector, times(1)).getInterestAccrualList(any(), any(), any())(any[HeaderCarrier])
    verify(mockInterestAccruedDaysCalcService, times(1))
      .getInterestAccrualListWithInterestAccruedDays(any(), any(), any(), any())(any[HeaderCarrier])

  }
  "getInterestAccrualList retrieves Interest Accrual List from connector and returns Right(InterestAccrualListWithInterestAccruedDays) for non IDE interestType when retrieval of StatueRule is successful" in new Fixture {
    when(mockAccrualInterestListConnector.getInterestAccrualList(any(), any(), eqTo("IDB"))(any[HeaderCarrier]))
      .thenReturn(Future.successful(interestAccrualListForNonIDE))

    when(
      mockInterestAccruedDaysCalcService
        .getInterestAccrualListWithInterestAccruedDays(any(), any(), any(), eqTo("IDB"))(any[HeaderCarrier])
    )
      .thenReturn(Future.successful(Right(interestAccrualListForNonIDEWithNoOfDays)))

    val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] =
      service.getInterestAccrualList(taxRef, accPeriod, "IDB").futureValue

    result shouldBe Right(interestAccrualListForNonIDEWithNoOfDays)

    verify(mockAccrualInterestListConnector, times(1)).getInterestAccrualList(any(), any(), any())(any[HeaderCarrier])
    verify(mockInterestAccruedDaysCalcService, times(1))
      .getInterestAccrualListWithInterestAccruedDays(any(), any(), any(), any())(any[HeaderCarrier])
  }
  "getInterestAccrualList retrieves Interest Accrual List from connector and returns Left(MissingStatueRule) when retrieval of StatueRule is unsuccessful for IDE interestType" in new Fixture {
    when(mockAccrualInterestListConnector.getInterestAccrualList(any[Long], any[Long], any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(interestAccrualList))

    when(
      mockInterestAccruedDaysCalcService.getInterestAccrualListWithInterestAccruedDays(
        any(),
        any(),
        any(),
        eqTo(LATE_PAYMENT_INTEREST)
      )(any[HeaderCarrier])
    )
      .thenReturn(Future.successful(Left(MissingStatueRule("Cannot find the statue Rule"))))

    val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] =
      service.getInterestAccrualList(taxRef, accPeriod, "IDE").futureValue

    result shouldBe Left(MissingStatueRule("Cannot find the statue Rule"))

    verify(mockAccrualInterestListConnector, times(1)).getInterestAccrualList(any(), any(), any())(any[HeaderCarrier])
    verify(mockInterestAccruedDaysCalcService, times(1))
      .getInterestAccrualListWithInterestAccruedDays(any(), any(), any(), any())(any[HeaderCarrier])

  }
  "getInterestAccrualList propagates exception from connector" in new Fixture {
    when(
      mockAccrualInterestListConnector.getInterestAccrualList(any(), any(), eqTo(LATE_PAYMENT_INTEREST))(
        any[HeaderCarrier]
      )
    )
      .thenReturn(Future.failed(new RuntimeException("Boom")))

    when(
      mockInterestAccruedDaysCalcService.getInterestAccrualListWithInterestAccruedDays(
        any(),
        any(),
        any(),
        eqTo(LATE_PAYMENT_INTEREST)
      )(any[HeaderCarrier])
    )
      .thenReturn(Future.successful(Right(interestAccrualListWithInterestAccruedDays)))

    val ex = intercept[Exception] {
      service.getInterestAccrualList(taxRef, accPeriod, LATE_PAYMENT_INTEREST).futureValue
    }

    ex.getMessage should include("Boom")

    verify(mockAccrualInterestListConnector, times(1)).getInterestAccrualList(any(), any(), any())(any[HeaderCarrier])
    verifyNoInteractions(mockInterestAccruedDaysCalcService)
  }
  "getInterestAccrualList propagates exception from InterestAccruedDaysCalculationService for IDE interestType" in new Fixture {
    when(
      mockAccrualInterestListConnector.getInterestAccrualList(any(), any(), eqTo(LATE_PAYMENT_INTEREST))(
        any[HeaderCarrier]
      )
    )
      .thenReturn(Future.successful(interestAccrualList))

    when(
      mockInterestAccruedDaysCalcService.getInterestAccrualListWithInterestAccruedDays(
        any(),
        any(),
        any(),
        eqTo(LATE_PAYMENT_INTEREST)
      )(any[HeaderCarrier])
    )
      .thenReturn(Future.failed(new RuntimeException("Boom")))

    val ex: Exception = intercept[Exception] {
      service.getInterestAccrualList(taxRef, accPeriod, LATE_PAYMENT_INTEREST).futureValue
    }

    ex.getMessage should include("Boom")

    verify(mockAccrualInterestListConnector, times(1)).getInterestAccrualList(any(), any(), any())(any[HeaderCarrier])
    verify(mockInterestAccruedDaysCalcService, times(1))
      .getInterestAccrualListWithInterestAccruedDays(any(), any(), any(), any())(any[HeaderCarrier])
  }

}
