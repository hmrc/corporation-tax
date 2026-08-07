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

package uk.gov.hmrc.corporationtax.utils

import uk.gov.hmrc.corporationtax.models.{
  AccountingPeriodDetails, InterestAccural, PaymentTransaction, ReallocationFromAccDetails, ReallocationRow,
  TaxTransactionsItem, PayRepayReallocations
}

object AmountAdjustableInstances {

  implicit val taxReallocationRowItemAmountAdjustable: AmountAdjustable[ReallocationRow] =
    new AmountAdjustable[ReallocationRow] {
      def amountFields
        : List[(ReallocationRow => Option[BigDecimal], (ReallocationRow, BigDecimal) => ReallocationRow)] =
        List(
          (item => Some(item.amount), (item, newValue) => item.copy(amount = newValue))
        )
    }

  implicit val taxTransactionsItemAmountAdjustable: AmountAdjustable[TaxTransactionsItem] =
    new AmountAdjustable[TaxTransactionsItem] {
      def amountFields
        : List[(TaxTransactionsItem => Option[BigDecimal], (TaxTransactionsItem, BigDecimal) => TaxTransactionsItem)] =
        List(
          (item => Some(item.currentAmount), (item, newValue) => item.copy(currentAmount = newValue))
        )
    }

  implicit val paymentTransactionAmountAdjustable: AmountAdjustable[PaymentTransaction] =
    new AmountAdjustable[PaymentTransaction] {
      def amountFields
        : List[(PaymentTransaction => Option[BigDecimal], (PaymentTransaction, BigDecimal) => PaymentTransaction)] =
        List(
          (item => Some(item.amount), (item, newValue) => item.copy(amount = newValue))
        )
    }

  implicit val interestAccuralListAdjustable: AmountAdjustable[InterestAccural] =
    new AmountAdjustable[InterestAccural] {
      def amountFields
        : List[(InterestAccural => Option[BigDecimal], (InterestAccural, BigDecimal) => InterestAccural)] =
        List(
          (item => Some(item.computationAmount), (item, newValue) => item.copy(computationAmount = newValue)),
          (item => Some(item.interestRate), (item, newValue) => item.copy(interestRate = newValue)),
          (item => Some(item.interestAmount), (item, newValue) => item.copy(interestAmount = newValue))
        )
    }

  implicit val accountingPeriodDetailsAmountAdjustable: AmountAdjustable[AccountingPeriodDetails] =
    new AmountAdjustable[AccountingPeriodDetails] {
      def amountFields: List[
        (
          AccountingPeriodDetails => Option[BigDecimal],
          (AccountingPeriodDetails, BigDecimal) => AccountingPeriodDetails
        )
      ] =
        List(
          (item => Some(item.creditInterestAmount), (item, newValue) => item.copy(creditInterestAmount = newValue)),
          (item => Some(item.debitInterestAmount), (item, newValue) => item.copy(debitInterestAmount = newValue)),
          (
            item => Some(item.latePaymentInterestAmount),
            (item, newValue) => item.copy(latePaymentInterestAmount = newValue)
          ),
          (
            item => Some(item.repaymentInterestAmount),
            (item, newValue) => item.copy(repaymentInterestAmount = newValue)
          ),
          (
            item => Some(item.totalDerivedActualInterest),
            (item, newValue) => item.copy(totalDerivedActualInterest = newValue)
          ),
          (item => Some(item.amountDueForAp), (item, newValue) => item.copy(amountDueForAp = newValue))
        )
    }

  implicit val reallocationFromAccPeriod: AmountAdjustable[ReallocationFromAccDetails] =
    new AmountAdjustable[ReallocationFromAccDetails] {
      def amountFields: List[
        (
          ReallocationFromAccDetails => Option[BigDecimal],
          (ReallocationFromAccDetails, BigDecimal) => ReallocationFromAccDetails
        )
      ] =
        List((item => item.amount, (item, newValue) => item.copy(amount = Some(newValue))))
    }

  implicit val payRepayReallocationAdjustable: AmountAdjustable[PayRepayReallocations] =
    new AmountAdjustable[PayRepayReallocations] {
      def amountFields
      : List[(PayRepayReallocations => Option[BigDecimal], (PayRepayReallocations, BigDecimal) => PayRepayReallocations)] =
        List(
          (item => item.totalAmountReoRfrRto, (item, newValue) => item.copy(totalAmountReoRfrRto = Some(newValue))),
          (item => item.totalAmountPayments, (item, newValue) => item.copy(totalAmountPayments = Some(newValue)))
        )
    }
}
