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

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.StatuteRuleConnector
import uk.gov.hmrc.corporationtax.models.*
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class StatuteRuleService @Inject() (
  connector: StatuteRuleConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  private def transform(e: StatuteRuleItem): StatuteRuleResponse = {
    val record = StatuteRuleRecord(
      ruleStartDate = e.ruleStartDate.getOrElse(throw new Error("No ruleStartDate value found")),
      ruleEndDate = e.ruleEndDate.getOrElse(throw new Error("No ruleEndDate value found")),
      numberOfDays = e.numberOfDays.getOrElse(0),
      ruleAmount = e.ruleAmount.getOrElse(BigDecimal(0)),
      ruleRate = e.ruleRate.getOrElse(BigDecimal(0))
    )
    StatuteRuleResponse(statuteRule = record)
  }

  def getStatueRule(ruleRateKey: String, startDateStr: String, endDateStr: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[StatuteRuleResponse]] = {
    logger.info(s"[StatuteRuleConnector][getStatueRule]: $ruleRateKey :: $startDateStr - $endDateStr")
    connector
      .getStatueRule(ruleRateKey, startDateStr, endDateStr)
      .collect {
        case Some(StatuteRule(item)) =>
          Some(
            transform(item)
          )
        case None                    => None
      }

  }

}
