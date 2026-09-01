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
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodsConnector
import uk.gov.hmrc.corporationtax.models.{AccountingPeriods, AccountingPeriodsRowResponse, RdsAccountingPeriod}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.toBool
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodsService @Inject (connector: AccountingPeriodsConnector)(implicit
  ec: ExecutionContext
) extends Logging {

  def getAccountingPeriod(
    taxRef: Long
  )(implicit hc: HeaderCarrier): Future[AccountingPeriods] =
    connector
      .getAccountingPeriods(taxRef)
      .map { rdsAccountingPeriod =>
        toAccountingPeriods(rdsAccountingPeriod)
      }

  private def toAccountingPeriods(
    rdsAccountingPeriod: RdsAccountingPeriod
  ): AccountingPeriods =
    AccountingPeriods(
      accountingPeriods = rdsAccountingPeriod.accountingPeriods.map { value =>
        AccountingPeriodsRowResponse(
          accountingPeriod = value.accountingPeriod,
          apStartDate = value.apStartDate,
          apEndDate = value.apEndDate,
          apStatus = value.apStatus,
          taxChargePresent = value.taxChargePresent.exists(toBool),
          clericalIntSig = value.clericalIntSig.exists(toBool),
          creditDebitInterestInd = value.creditDebitInterestInd.exists(toBool),
          taxTotal = AmountTransformation(value.taxTotal),
          interestTotal = AmountTransformation(value.interestTotal),
          penaltyTotal = AmountTransformation(value.penaltyTotal),
          payslipTotal = AmountTransformation(value.payslipTotal),
          repayReallocTotal = AmountTransformation(value.repayReallocTotal),
          adjustmentTotal = AmountTransformation(value.adjustmentTotal)
        )
      }
    )

}
