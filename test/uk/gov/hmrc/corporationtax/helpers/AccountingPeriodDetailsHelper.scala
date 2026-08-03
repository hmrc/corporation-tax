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

import uk.gov.hmrc.corporationtax.models.AccountingPeriodDetails

import java.time.LocalDate

trait AccountingPeriodDetailsHelper {

  val accountingPeriodDetails: AccountingPeriodDetails =
    AccountingPeriodDetails(
      isApBalanced = true,
      lpiCalcFlag = true,
      crDbCalcFlag = true,
      creditInterestAmount = 123.235,
      debitInterestAmount = 5930.02,
      latePaymentInterestAmount = 3231.238,
      repaymentInterestAmount = 1.231,
      totalDerivedActualInterest = 2324.12,
      amountDueForAp = 12.23,
      accPeriodEndDate = Some(LocalDate.of(2026, 1, 1))
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
      amountDueForAp = -12.23,
      accPeriodEndDate = Some(LocalDate.of(2026, 1, 1))
    )

}
