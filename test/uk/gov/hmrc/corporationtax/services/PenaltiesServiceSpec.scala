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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.verify
import uk.gov.hmrc.corporationtax.connectors.{AccountingPeriodDetailsConnector, AdminRuleRdsProxyConnector, PenaltiesConnector}
import uk.gov.hmrc.http.HeaderCarrier
import org.mockito.ArgumentMatchers.any; //, eq as eqTo}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.helpers.PenaltiesHelper
import uk.gov.hmrc.corporationtax.models.{AccountingPeriodDetails, AdminRule, PenaltyItems}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class PenaltiesServiceSpec extends AnyWordSpec with Matchers with PenaltiesHelper with ScalaFutures {

  private trait Fixture {
    val mockPenaltiesConnector: PenaltiesConnector = mock[PenaltiesConnector]
    val mockAdminRuleRdsProxyConnector: AdminRuleRdsProxyConnector = mock[AdminRuleRdsProxyConnector]
    val mockAccountingPeriodDetailsConnector: AccountingPeriodDetailsConnector = mock[AccountingPeriodDetailsConnector]


    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service = new PenaltiesService(mockPenaltiesConnector, mockAdminRuleRdsProxyConnector, mockAccountingPeriodDetailsConnector)

    // Move under data helper:
    val adminRule :AdminRule = AdminRule(ruleNumber = Some(111), ruleDate = Some(LocalDate.of(2025, 1, 1)))
    val accountPeriodDetails = AccountingPeriodDetails(
        isApBalanced = true,
        lpiCalcFlag = true,
        crDbCalcFlag = true,
        creditInterestAmount = -123.24,
        debitInterestAmount = -5930.02,
        latePaymentInterestAmount = -3231.24,
        repaymentInterestAmount = -1.23,
        totalDerivedActualInterest = -2324.12,
        amountDueForAp = -12.23,
        accPeriodEndDate = Some(LocalDate.of(2025, 2, 1))
      )
  }

  "getPenaltyTransactionList returns list of Penalties from connector" in new Fixture {
    when(mockPenaltiesConnector.getPenaltyTransactionList(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(penalties))
    when(mockAdminRuleRdsProxyConnector.getAdminRule(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(adminRule))
    when(mockAccountingPeriodDetailsConnector.getAccountingPeriodDetails(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(accountPeriodDetails))

    val result: PenaltyItems = service.getPenaltyTransactionList(1L, 1L).futureValue

    result shouldBe penaltyItems

    verify(mockPenaltiesConnector).getPenaltyTransactionList(1L, 1L)(hc)
    verify(mockAdminRuleRdsProxyConnector).getAdminRule("START-OF-CTSA")(hc)
    verify(mockAccountingPeriodDetailsConnector).getAccountingPeriodDetails(1L, 1L)(hc)
  }

  // TODO: extend testing to cover CTPF scenarios

}
