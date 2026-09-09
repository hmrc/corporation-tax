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
import org.mockito.Mockito.{times, verify, verifyNoInteractions, verifyNoMoreInteractions, when}
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.helpers.InterestAccrualListWithNoOfDaysHelper
import uk.gov.hmrc.corporationtax.models.BusinessConstants.{
  APPEND_DUE_DATE_DAYS, APPEND_DUE_DATE_MONTHS, LATE_PAYMENT_INTEREST
}
import uk.gov.hmrc.corporationtax.models.{
  InterestAccrualList, InterestAccrualListWithInterestAccruedDays, MissingDataError, MissingStatueRule,
  StatuteRuleResponse
}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class InterestAccruedDaysCalculationServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with InterestAccrualListWithNoOfDaysHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockService: StatuteRuleService = mock[StatuteRuleService]
    val service                         = new InterestAccruedDaysCalculationService(mockService)
    val taxReferenceNumber: Long        = 1234567L
    val accPeriod: Long                 = 3456L
    val nonIDEInterestType: String      = "IDB"

  }

  "InterestAccruedDaysCalculationService.getInterestAccrualListWithInterestAccruedDays" should {

    "NOT-IDE InterestTypes" should {
      "return Right(InterestAccrualListWithInterestAccruedDays) with calculated interestAccruedDays" in new BaseSetup {
        val statueRuleResponse: StatuteRuleResponse  = statuteRuleResponseGen(21)
        val fromDate: LocalDate                      = LocalDate.of(2021, 3, 7)
        val toDate: LocalDate                        = LocalDate.of(2021, 5, 7)
        val apEndDate: LocalDate                     = LocalDate.of(2021, 6, 7)
        val interestAccrualList: InterestAccrualList = interestAccrualListGen(fromDate, toDate, apEndDate)

        val actualNoOfDays: Long = 62L //  noOfDays = toDate - fromDate + 1

        val expectedResult: InterestAccrualListWithInterestAccruedDays =
          interestAccrualListWithInterestAccruedDaysGen(fromDate, toDate, actualNoOfDays, apEndDate)

        when(mockService.getStatueRule(any(), any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(Some(statueRuleResponse)))

        val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] = service
          .getInterestAccrualListWithInterestAccruedDays(
            interestAccrualList,
            taxReferenceNumber,
            accPeriod,
            nonIDEInterestType
          )
          .futureValue

        result shouldBe Right(expectedResult)

        val resultUnWrapped: InterestAccrualListWithInterestAccruedDays = result.value

        resultUnWrapped.interestAccruals.head.noOfDays shouldBe actualNoOfDays

        verifyNoInteractions(mockService)

      }
    }

  }
  "IDE InterestType" should {
    "return Right(InterestAccrualListWithInterestAccruedDays), calculate normalDueDate, noOfDays where normalDueDate = fromDate" in new BaseSetup {
      val statueRuleResponseForMonths: StatuteRuleResponse = statuteRuleResponseGen(8)
      val statueRuleResponseForDays: StatuteRuleResponse   = statuteRuleResponseGen(21)

      val apEndDate: LocalDate = LocalDate.of(2026, 3, 19)

      val normalDueDate: LocalDate =
        apEndDate.plusMonths(8).plusDays(21) // 10th of December 2026 (normalDueDate = apEndDate + M months + A days)

      val toDate: LocalDate                        = LocalDate.of(2029, 5, 7)
      val fromDate: LocalDate                      = normalDueDate // fromDate = NormalDueDate
      val interestAccrualList: InterestAccrualList = interestAccrualListGen(fromDate, toDate, apEndDate)

      val actualNoOfDays: Long = 879L //  noOfDays = toDate - fromDate

      val expectedResult: InterestAccrualListWithInterestAccruedDays =
        interestAccrualListWithInterestAccruedDaysGen(fromDate, toDate, actualNoOfDays, apEndDate)

      when(
        mockService.getStatueRule(eqTo(APPEND_DUE_DATE_MONTHS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(Some(statueRuleResponseForMonths)))

      when(mockService.getStatueRule(eqTo(APPEND_DUE_DATE_DAYS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier]))
        .thenReturn(Future.successful(Some(statueRuleResponseForDays)))

      val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] = service
        .getInterestAccrualListWithInterestAccruedDays(
          interestAccrualList,
          taxReferenceNumber,
          accPeriod,
          LATE_PAYMENT_INTEREST
        )
        .futureValue

      result shouldBe Right(expectedResult)

      result.value.interestAccruals.head.noOfDays shouldBe actualNoOfDays

      verify(mockService, times(2)).getStatueRule(any(), any(), any())(any[HeaderCarrier])
      verifyNoMoreInteractions(mockService)

    }
    "return Right(InterestAccrualListWithInterestAccruedDays), calculate normalDueDate, noOfDays where normalDueDate not equal to fromDate within DST boundaries" in new BaseSetup {
      val statueRuleResponseForMonths: StatuteRuleResponse = statuteRuleResponseGen(24)
      val statueRuleResponseForDays: StatuteRuleResponse   = statuteRuleResponseGen(21)

      val apEndDate: LocalDate                     = LocalDate.of(2026, 3, 19)
      val toDate: LocalDate                        = LocalDate.of(2029, 5, 7)
      val fromDate: LocalDate                      = LocalDate.of(2025, 12, 3)
      val interestAccrualList: InterestAccrualList = interestAccrualListGen(fromDate, toDate, apEndDate)

      val actualNoOfDays: Long = 1252L //  noOfDays = (toDate - fromDate) +1L

      val expectedResult: InterestAccrualListWithInterestAccruedDays =
        interestAccrualListWithInterestAccruedDaysGen(fromDate, toDate, actualNoOfDays, apEndDate)

      when(
        mockService.getStatueRule(eqTo(APPEND_DUE_DATE_MONTHS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
      )
        .thenReturn(Future.successful(Some(statueRuleResponseForMonths)))

      when(mockService.getStatueRule(eqTo(APPEND_DUE_DATE_DAYS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier]))
        .thenReturn(Future.successful(Some(statueRuleResponseForDays)))

      val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] = service
        .getInterestAccrualListWithInterestAccruedDays(
          interestAccrualList,
          taxReferenceNumber,
          accPeriod,
          LATE_PAYMENT_INTEREST
        )
        .futureValue

      result shouldBe Right(expectedResult)

      result.value.interestAccruals.head.noOfDays shouldBe actualNoOfDays

      verify(mockService, times(2)).getStatueRule(any(), any(), any())(any[HeaderCarrier])
      verifyNoMoreInteractions(mockService)
    }
    "Error cases and exception cases " should {
      val statueRuleResponseForDays: StatuteRuleResponse   = statuteRuleResponseGen(21)
      val statueRuleResponseForMonths: StatuteRuleResponse = statuteRuleResponseGen(21)
      val apEndDate: LocalDate                             = LocalDate.of(2026, 3, 19)
      val toDate: LocalDate                                = LocalDate.of(2029, 5, 7)
      val fromDate: LocalDate                              = LocalDate.of(2025, 12, 3)
      val interestAccrualList: InterestAccrualList         = interestAccrualListGen(fromDate, toDate, apEndDate)

      "return Left(MissingStatuteRuleError), when retrieving statuteRule returns None for APPEND_DUE_DATE_MONTHS call" in new BaseSetup {
        when(
          mockService.getStatueRule(eqTo(APPEND_DUE_DATE_MONTHS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
        )
          .thenReturn(Future.successful(None))

        when(
          mockService.getStatueRule(eqTo(APPEND_DUE_DATE_DAYS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
        )
          .thenReturn(Future.successful(Some(statueRuleResponseForDays)))

        val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] = service
          .getInterestAccrualListWithInterestAccruedDays(
            interestAccrualList,
            taxReferenceNumber,
            accPeriod,
            LATE_PAYMENT_INTEREST
          )
          .futureValue

        result shouldBe Left(MissingStatueRule(s"Cannot find statue rule for ruleRateKey:$APPEND_DUE_DATE_MONTHS"))
        verify(mockService, times(2)).getStatueRule(any(), any(), any())(any[HeaderCarrier])
        verifyNoMoreInteractions(mockService)
      }
      "return Left(MissingStatuteRuleError), when retrieving statuteRule returns None for APPEND_DUE_DATE_DAYS call" in new BaseSetup {
        when(
          mockService.getStatueRule(eqTo(APPEND_DUE_DATE_MONTHS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
        )
          .thenReturn(Future.successful(Some(statueRuleResponseForMonths)))

        when(
          mockService.getStatueRule(eqTo(APPEND_DUE_DATE_DAYS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
        )
          .thenReturn(Future.successful(None))

        val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] = service
          .getInterestAccrualListWithInterestAccruedDays(
            interestAccrualList,
            taxReferenceNumber,
            accPeriod,
            LATE_PAYMENT_INTEREST
          )
          .futureValue

        result shouldBe Left(MissingStatueRule(s"Cannot find statue rule for ruleRateKey:$APPEND_DUE_DATE_DAYS"))
        verify(mockService, times(2)).getStatueRule(any(), any(), any())(any[HeaderCarrier])
        verifyNoMoreInteractions(mockService)
      }
      "propagate Upstream error,when retrieving statuteRule returns an exception" in new BaseSetup {
        when(
          mockService.getStatueRule(eqTo(APPEND_DUE_DATE_MONTHS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
        )
          .thenReturn(Future.failed(new RuntimeException("Boom")))

        when(
          mockService.getStatueRule(eqTo(APPEND_DUE_DATE_DAYS), eqTo(apEndDate), eqTo(apEndDate))(any[HeaderCarrier])
        )
          .thenReturn(Future.failed(new RuntimeException("Boom")))

        val ex: Exception = intercept[Exception] {
          service
            .getInterestAccrualListWithInterestAccruedDays(
              interestAccrualList,
              taxReferenceNumber,
              accPeriod,
              LATE_PAYMENT_INTEREST
            )
            .futureValue

        }

        ex.getMessage should include("Boom")

      }
    }

  }
}
