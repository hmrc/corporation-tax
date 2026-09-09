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
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.helpers.InterestAccrualListWithNoOfDaysHelper
import uk.gov.hmrc.corporationtax.models.{InterestAccrualListWithInterestAccruedDays, MissingDataError}
import uk.gov.hmrc.http.HeaderCarrier

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

    "NON-IDE InterestTypes" should {

      "return Right(InterestAccrualListWithInterestAccruedDays) with calculated interestAccruedDays" in new BaseSetup {
        when(mockService.getStatueRule(any(), any(), any())(any[HeaderCarrier]))
          .thenReturn(Future.successful(Some(statuteRuleResponse)))

        val result: Either[MissingDataError, InterestAccrualListWithInterestAccruedDays] = service
          .getInterestAccrualListWithInterestAccruedDays(
            interestAccrualList,
            taxReferenceNumber,
            accPeriod,
            nonIDEInterestType
          )
          .futureValue

        result shouldBe Right(interestAccrualListWithInterestAccruedDays)

      }

      "return Right(InterestAccrualListWithInterestAccruedDays) with calculated interestAccruedDays field considering DST boundaries" in new BaseSetup {}

    }

  }

  "IDE InterestType" should {
    "return Right(InterestAccrualListWithInterestAccruedDays), calculate normalDueDate, noOfDays where normalDueDate = fromDate within DST boundaries" in new BaseSetup {}
    "return Right(InterestAccrualListWithInterestAccruedDays), calculate normalDueDate, noOfDays where normalDueDate not equal to fromDate within DST boundaries" in new BaseSetup {}
    "return Left(MissingStatuteRuleError), when retrieving statuteRule to calculate months" in new BaseSetup {}
    "return Left(MissingStatuteRuleError), when retrieving statuteRule to calculate days" in new BaseSetup {}
  }
}
