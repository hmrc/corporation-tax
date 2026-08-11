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
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodDetailsConnector
import uk.gov.hmrc.corporationtax.helpers.AccountingPeriodDetailsHelper
import uk.gov.hmrc.corporationtax.models.AccountingPeriodDetails
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodDetailsServiceSpec
    extends AnyWordSpec
    with Matchers
    with AccountingPeriodDetailsHelper
    with ScalaFutures {

  private trait Fixture {
    val mockAccPeriodDetailsConnector: AccountingPeriodDetailsConnector = mock[AccountingPeriodDetailsConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service =
      new AccountingPeriodDetailsService(mockAccPeriodDetailsConnector)

  }

  /*
  AccountingPeriodDetails(true, false, false, -123.24, -5930.02, -3231.24, -1.23, -9297.95, -12.23) 
  AccountingPeriodDetails(true, false, true, -123.24, -5930.02, -3231.24, -1.23, -9297.95, -12.23)
   */
  "getAccountingDetails returns transformed record" in new Fixture {
    when(mockAccPeriodDetailsConnector.getAccountingPeriodDetails(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(apBalanceResponse))

    val result: AccountingPeriodDetails = service.getAccountingDetails(1L, 1L).futureValue

    result shouldBe accountingPeriodDetails

    verify(mockAccPeriodDetailsConnector).getAccountingPeriodDetails(1L, 1L)(hc)
  }

  "getAccountingDetails returns transformed empty record" in new Fixture {
    when(mockAccPeriodDetailsConnector.getAccountingPeriodDetails(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(apBalanceEmptyResponse))

    val result: AccountingPeriodDetails = service.getAccountingDetails(7L, 9L).futureValue

    result shouldBe accountingPeriodDetailsEmptyRecord

    verify(mockAccPeriodDetailsConnector).getAccountingPeriodDetails(7L, 9L)(hc)
  }

}
