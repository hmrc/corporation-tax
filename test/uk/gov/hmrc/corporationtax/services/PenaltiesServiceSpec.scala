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
import uk.gov.hmrc.corporationtax.connectors.{
  AdminRuleRdsProxyConnector, PenaltiesConnector
}
import uk.gov.hmrc.http.HeaderCarrier
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.helpers.PenaltiesHelper
import uk.gov.hmrc.corporationtax.models.{AdminRule, PenaltyItems}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class PenaltiesServiceSpec extends AnyWordSpec with Matchers
  with PenaltiesHelper with ScalaFutures {

  private trait Fixture {
    val mockPenaltiesConnector: PenaltiesConnector                             = mock[PenaltiesConnector]
    val mockAdminRuleRdsProxyConnector: AdminRuleRdsProxyConnector             = mock[AdminRuleRdsProxyConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service =
      new PenaltiesService(mockPenaltiesConnector, mockAdminRuleRdsProxyConnector)

    // Move under data helper:
    val adminRule: AdminRule                 = AdminRule(ruleNumber = Some(111), ruleDate = Some(LocalDate.of(2025, 1, 1)))
    val adminRuleSecond: AdminRule           = AdminRule(ruleNumber = Some(111), ruleDate = Some(LocalDate.of(2026, 1, 1)))
    val adminRuleWithRuleDateNone: AdminRule = AdminRule(ruleNumber = Some(111), ruleDate = None)

  }

  "getPenaltyTransactionList returns list of Penalties from connector: isCTPF is true" in new Fixture {
    when(mockPenaltiesConnector.getPenaltyTransactionList(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(penalties))
    when(mockAdminRuleRdsProxyConnector.getAdminRule(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(adminRuleSecond))

    val result: PenaltyItems = service.getPenaltyTransactionList(1L, 1L, Some(LocalDate.of(2025, 2, 1))).futureValue

    result shouldBe penaltyItems

    verify(mockPenaltiesConnector).getPenaltyTransactionList(1L, 1L)(hc)
    verify(mockAdminRuleRdsProxyConnector).getAdminRule("START-OF-CTSA")(hc)
  }

  "getPenaltyTransactionList returns list of Penalties from connector: isCTPF is false" in new Fixture {
    when(mockPenaltiesConnector.getPenaltyTransactionList(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(penalties))
    when(mockAdminRuleRdsProxyConnector.getAdminRule(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(adminRule))

    val result: PenaltyItems = service.getPenaltyTransactionList(1L, 1L, Some(LocalDate.of(2025, 2, 1))).futureValue

    result shouldBe penaltyItemsSecond

    verify(mockPenaltiesConnector).getPenaltyTransactionList(1L, 1L)(hc)
    verify(mockAdminRuleRdsProxyConnector).getAdminRule("START-OF-CTSA")(hc)
  }

  "getPenaltyTransactionList returns list of Penalties from connector: isCTPF is true and adminRule is None" in new Fixture {
    when(mockPenaltiesConnector.getPenaltyTransactionList(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(penalties))
    when(mockAdminRuleRdsProxyConnector.getAdminRule(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(adminRuleWithRuleDateNone))

    val result: PenaltyItems = service.getPenaltyTransactionList(1L, 1L, None).futureValue

    result shouldBe penaltyItems

    verify(mockPenaltiesConnector).getPenaltyTransactionList(1L, 1L)(hc)
    verify(mockAdminRuleRdsProxyConnector).getAdminRule("START-OF-CTSA")(hc)
  }



  "getPenaltyTransactionList returns list of Penalties from connector: isCTPF is true and accountPeriodDate is None" in new Fixture {
    when(mockPenaltiesConnector.getPenaltyTransactionList(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(penalties))
    when(mockAdminRuleRdsProxyConnector.getAdminRule(any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(adminRule))

    val result: PenaltyItems = service.getPenaltyTransactionList(1L, 1L, None).futureValue

    result shouldBe penaltyItems

    verify(mockPenaltiesConnector).getPenaltyTransactionList(1L, 1L)(hc)
    verify(mockAdminRuleRdsProxyConnector).getAdminRule("START-OF-CTSA")(hc)
  }

}
