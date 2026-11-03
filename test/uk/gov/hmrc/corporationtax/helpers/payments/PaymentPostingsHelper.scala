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

package uk.gov.hmrc.corporationtax.helpers.payments

import uk.gov.hmrc.corporationtax.models.payments.{PaymentPostingRow, PaymentPostingsResponse}
import java.time.LocalDate

trait PaymentPostingsHelper {

  val emptyPaymentPostings: PaymentPostingsResponse =
    PaymentPostingsResponse(List.empty)

  val singlePaymentPosting: PaymentPostingsResponse =
    PaymentPostingsResponse(
      List(
        PaymentPostingRow(
          accountingPeriod = 202408L,
          settlementSequenceNo = 123456,
          payslipId = 987654,
          postingAmount = 1500.75,
          effectiveDateOfPayment = LocalDate.of(2024, 9, 15),
          paymentType = "BAC"
        )
      )
    )

  val multiplePaymentPostings: PaymentPostingsResponse =
    PaymentPostingsResponse(
      List(
        PaymentPostingRow(
          accountingPeriod = 202408L,
          settlementSequenceNo = 123456,
          payslipId = 987654,
          postingAmount = 1500.75,
          effectiveDateOfPayment = LocalDate.of(2024, 9, 15),
          paymentType = "BAC"
        ),
        PaymentPostingRow(
          accountingPeriod = 202407L,
          settlementSequenceNo = 642736,
          payslipId = 983123,
          postingAmount = 2712.63,
          effectiveDateOfPayment = LocalDate.of(2024, 8, 20),
          paymentType = "BAC"
        ),
        PaymentPostingRow(
          accountingPeriod = 202404L,
          settlementSequenceNo = 99423764,
          payslipId = 985624,
          postingAmount = 2416.89,
          effectiveDateOfPayment = LocalDate.of(2024, 5, 26),
          paymentType = "BAC"
        )
      )
    )
}
