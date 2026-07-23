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

import uk.gov.hmrc.corporationtax.models.{InterestAccural, PaymentTransaction, TaxTransactionsItem}

object AmountAdjustableInstances {
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

}
