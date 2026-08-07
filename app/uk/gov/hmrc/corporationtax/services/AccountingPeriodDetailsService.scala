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

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodDetailsConnector
import uk.gov.hmrc.corporationtax.models.{APBalancedResponse, AccountingPeriodDetails}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class AccountingPeriodDetailsService @Inject()(
                                                connector: AccountingPeriodDetailsConnector)
                                              (implicit ec: ExecutionContext){

  private def booleanConverter(in: String): Boolean = {
    in match {
      case "Y" => true
      case _ => false
    }
  }

  private def prepareResponse(e: APBalancedResponse) : AccountingPeriodDetails = {
    // Apply conversions :: Boolean && Amount

    AccountingPeriodDetails(
      isApBalanced = e.accountingPeriodDetails.isApBalanced.map(booleanConverter(_)).getOrElse(false),
      lpiCalcFlag = e.accountingPeriodDetails.lpiCalcFlag.map(booleanConverter(_)).getOrElse(false),
      crDbCalcFlag = e.accountingPeriodDetails.crDbCalcFlag.map(booleanConverter(_)).getOrElse(false),
      creditInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      debitInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      latePaymentInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      repaymentInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      totalDerivedActualInterest = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      amountDueForAp = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      accPeriodEndDate = None
    )
  }

  def getAccountingDetails(taxRef: Long, accPeriod: Long)
                          (implicit hc: HeaderCarrier): Future[AccountingPeriodDetails] = {
    logger.info(s"Calling connector for taxRef: $taxRef and accPeriod: $accPeriod")
    connector
      .getAccountingPeriodDetails(taxRef, accPeriod)
      .map(x => prepareResponse(x) )
  }
}
