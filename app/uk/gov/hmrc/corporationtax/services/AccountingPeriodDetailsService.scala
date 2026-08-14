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
import uk.gov.hmrc.corporationtax.models.{APBalancedResponse, AccountingPeriodDetails, AccountingPeriodDetailsResponse}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodDetailsService @Inject() (connector: AccountingPeriodDetailsConnector)(implicit
  ec: ExecutionContext
) {

  private def booleanConverter(in: String): Boolean =
    in.toUpperCase() match {
      case "Y" => true
      case _   => false
    }

  private def calcTotalDerivedActualInterest(e: APBalancedResponse): Option[BigDecimal] =
    Seq(
      e.accountingPeriodDetails.creditInterestAmount,
      e.accountingPeriodDetails.debitInterestAmount,
      e.accountingPeriodDetails.latePaymentInterestAmount,
      e.accountingPeriodDetails.repaymentInterestAmount,
      e.accountingPeriodDetails.amountDueForAp
    ).collect { case Some(amount) =>
      amount
    } match {
      case xs if xs.nonEmpty => Some(xs.sum)
      case _                 => None
    }

  private def transform(e: APBalancedResponse): AccountingPeriodDetails =
    AccountingPeriodDetails(
      isApBalanced = e.accountingPeriodDetails.isApBalanced.exists(booleanConverter),
      lpiCalcFlag = e.accountingPeriodDetails.lpiCalcFlag.exists(booleanConverter),
      crDbCalcFlag = e.accountingPeriodDetails.crDbCalcFlag.exists(booleanConverter),
      creditInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.creditInterestAmount),
      debitInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.debitInterestAmount),
      latePaymentInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.latePaymentInterestAmount),
      repaymentInterestAmount = AmountTransformation.apply(e.accountingPeriodDetails.repaymentInterestAmount),
      totalDerivedActualInterest = AmountTransformation.apply(calcTotalDerivedActualInterest(e)),
      amountDueForAp = AmountTransformation.apply(e.accountingPeriodDetails.amountDueForAp)
    )

  def getAccountingDetails(taxRef: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[AccountingPeriodDetailsResponse] = {
    logger.info(s"[AccountingPeriodDetailsService][getAccountingDetails] taxRef: $taxRef and accPeriod: $accPeriod")
    connector
      .getAccountingPeriodDetails(taxRef, accPeriod)
      .map(transform)
      .map(record => AccountingPeriodDetailsResponse(record))
  }

}
