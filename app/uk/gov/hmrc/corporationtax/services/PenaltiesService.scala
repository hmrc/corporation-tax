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

import uk.gov.hmrc.corporationtax.models.*
import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.{
  AccountingPeriodDetailsConnector, AdminRuleRdsProxyConnector, PenaltiesConnector
}
import uk.gov.hmrc.http.HeaderCarrier
import PenaltyTransaction.*

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class PenaltiesService @Inject() (
  penaltiesConnector: PenaltiesConnector,
  adminRuleRdsProxyConnector: AdminRuleRdsProxyConnector,
  accountingPeriodDetailsConnector: AccountingPeriodDetailsConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  private val adminRuleKey: String = "START-OF-CTSA"

  private def getCTPFStatus(accountingPeriod: LocalDate, adminRuleDate: LocalDate): Boolean =
    accountingPeriod.toEpochDay < adminRuleDate.toEpochDay

  def getCTPFStatusAsync(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[Boolean] =
    for {
      adminRulesResult        <- adminRuleRdsProxyConnector.getAdminRule(adminRuleKey)
      accountingPeriodDetails <- accountingPeriodDetailsConnector.getAccountingPeriodDetails(taxRef, accPeriod)
    } yield (adminRulesResult.ruleDate, accountingPeriodDetails.accPeriodEndDate) match {
      case (Some(adminRuleDate), Some(accountingPeriodDate)) =>
        getCTPFStatus(adminRuleDate, accountingPeriodDate)
      case (_, _)                                            => true
    }

  def getPenaltyTransactionList(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[PenaltyItems] =
    for {
      penalties <- penaltiesConnector.getPenaltyTransactionList(taxRef, accPeriod)
      isCTPF    <- getCTPFStatusAsync(taxRef = taxRef, accPeriod = accPeriod)
    } yield PenaltyItems(penalties.penaltyTransactions.map(p => convertToItems(p, isCTPF)))

}
