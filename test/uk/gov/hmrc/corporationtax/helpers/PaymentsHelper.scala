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

import uk.gov.hmrc.corporationtax.models.{PaymentTransaction, Payments}

import java.time.LocalDate

trait PaymentsHelper {

  val taxRef: Long    = 1234567L
  val accPeriod: Long = 1L

  val emptyPaymentTransactions: Payments = Payments(List.empty)

  val paymentTransactions: Payments =
    Payments(
      List(
        PaymentTransaction(
          amount = BigDecimal(123.44),
          paymentType = "A",
          effectiveDateOfPayment = LocalDate.of(2026, 1, 1)
        ),
        PaymentTransaction(
          amount = BigDecimal(123.44),
          paymentType = "A",
          effectiveDateOfPayment = LocalDate.of(2026, 2, 1)
        ),
        PaymentTransaction(
          amount = BigDecimal(123.44),
          paymentType = "E",
          effectiveDateOfPayment = LocalDate.of(2026, 3, 1)
        )
      )
    )

  val paymentTransactionsSingleItemList: Payments = Payments(
    List(
      PaymentTransaction(
        amount = BigDecimal(123.44),
        paymentType = "E",
        effectiveDateOfPayment = LocalDate.of(2026, 3, 1)
      )
    )
  )

  val paymentTransactionsSingleItemListTransformed: Payments = Payments(
    List(
      PaymentTransaction(
        amount = BigDecimal(-123.44),
        paymentType = "E",
        effectiveDateOfPayment = LocalDate.of(2026, 3, 1)
      )
    )
  )

  val paymentTransactionsTransformed: Payments =
    Payments(
      List(
        PaymentTransaction(
          amount = BigDecimal(-123.44),
          paymentType = "A",
          effectiveDateOfPayment = LocalDate.of(2026, 1, 1)
        ),
        PaymentTransaction(
          amount = BigDecimal(-123.44),
          paymentType = "A",
          effectiveDateOfPayment = LocalDate.of(2026, 2, 1)
        ),
        PaymentTransaction(
          amount = BigDecimal(-123.44),
          paymentType = "E",
          effectiveDateOfPayment = LocalDate.of(2026, 3, 1)
        )
      )
    )

}
