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
import uk.gov.hmrc.corporationtax.connectors.AccountPositionConnector
import uk.gov.hmrc.corporationtax.models.{AccountPosition, AccountPositionResponse, ApAmountRecord}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountPositionService @Inject (
  connector: AccountPositionConnector
)(implicit
  ec: ExecutionContext
) extends Logging {
  import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.*

  private def transform(rec: AccountPosition): AccountPositionResponse = {
    val apAmounts = rec.apAmounts.map(r =>
      ApAmountRecord(
        accountingPeriod = r.accountingPeriod,
        apEndDate = r.apEndDate,
        amountDueForAp = AmountTransformation(r.amountDueForAp),
        apStatus = r.apStatus
      )
    )

    AccountPositionResponse(
      amountDue = AmountTransformation(rec.amountDue),
      asOnDate = rec.asOnDate,
      gpaLinkFlag = rec.gpaLinkFlag.exists(toBool),
      taxpayerList = rec.taxpayerList,
      apAmounts = apAmounts,
      doesCompanyExist = rec.doesCompanyExist.exists(toBool)
    )
  }

  def getAccountPosition(taxRef: Long)(implicit hc: HeaderCarrier): Future[AccountPositionResponse] = {
    logger.info(s"taxRef: $taxRef")
    connector
      .getAccountPosition(taxRef)
      .map(transform)
  }

}
