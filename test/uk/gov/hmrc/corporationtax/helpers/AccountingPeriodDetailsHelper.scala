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

package uk.gov.hmrc.corporationtax.helpers

import uk.gov.hmrc.corporationtax.models.{APBalancedItem, APBalancedResponse, AccountingPeriodDetails}

trait AccountingPeriodDetailsHelper {

  // before transform
  val apBalanceResponse = APBalancedResponse(
    accountingPeriodDetails = APBalancedItem(
      isApBalanced = Some("Y"),
      lpiCalcFlag = Some(""),
      crDbCalcFlag = Some(""),
      creditInterestAmount = Some(BigDecimal(123.235)),
      debitInterestAmount = Some(BigDecimal(5930.02)),
      latePaymentInterestAmount = Some(BigDecimal(3231.238)),
      repaymentInterestAmount = Some(BigDecimal(1.231)),
      amountDueForAp = Some(BigDecimal(12.23))
    )
  )

  val apBalanceEmptyResponse = APBalancedResponse(
    accountingPeriodDetails = APBalancedItem(
      isApBalanced = None,
      lpiCalcFlag = None,
      crDbCalcFlag = None,
      creditInterestAmount = None,
      debitInterestAmount = None,
      latePaymentInterestAmount = None,
      repaymentInterestAmount = None,
      amountDueForAp = None
    )
  )

  // after transformation
  val accountingPeriodDetails: AccountingPeriodDetails =
    AccountingPeriodDetails(
      isApBalanced = true,
      lpiCalcFlag = false,
      crDbCalcFlag = false,
      creditInterestAmount = -123.24,
      debitInterestAmount = -5930.02,
      latePaymentInterestAmount = -3231.24,
      repaymentInterestAmount = -1.23,
      totalDerivedActualInterest = -9297.95,
      amountDueForAp = -12.23
    )

  val accountingPeriodDetailsEmptyRecord = AccountingPeriodDetails(
    isApBalanced = false,
    lpiCalcFlag = false,
    crDbCalcFlag = false,
    creditInterestAmount = 0.0,
    debitInterestAmount = 0.0,
    latePaymentInterestAmount = 0.0,
    repaymentInterestAmount = 0.0,
    totalDerivedActualInterest = 0.0,
    amountDueForAp = 0.0
  )

  val accountingPeriodDetailsTransformedAmounts: AccountingPeriodDetails =
    AccountingPeriodDetails(
      isApBalanced = true,
      lpiCalcFlag = true,
      crDbCalcFlag = true,
      creditInterestAmount = -123.24,
      debitInterestAmount = -5930.02,
      latePaymentInterestAmount = -3231.24,
      repaymentInterestAmount = -1.23,
      totalDerivedActualInterest = -2324.12,
      amountDueForAp = -12.23
    )

}
