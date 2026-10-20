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

package uk.gov.hmrc.corporationtax.testdata

import uk.gov.hmrc.corporationtax.models.payments.{RdsPaymentAllocationChequeDetails, RdsPaymentAllocationChequeRow}

import java.time.LocalDate

trait PaymentAllocationChequeDetailsHelper {

  val emptyRdsPaymentAllocationChequeDetails: RdsPaymentAllocationChequeDetails =
    RdsPaymentAllocationChequeDetails(paymentAllocationDetails = List.empty)

  val fullRdsPaymentAllocationChequeDetails: RdsPaymentAllocationChequeDetails =
    RdsPaymentAllocationChequeDetails(
      paymentAllocationDetails = List(
        RdsPaymentAllocationChequeRow(
          accountingPeriod = 1L,
          apEndDate = LocalDate.of(2023, 3, 31),
          amount = Some(BigDecimal("1500.00")),
          effectiveDateOfPayment = LocalDate.of(2023, 4, 15)
        ),
        RdsPaymentAllocationChequeRow(
          accountingPeriod = 2L,
          apEndDate = LocalDate.of(2024, 3, 31),
          amount = Some(BigDecimal("2750.50")),
          effectiveDateOfPayment = LocalDate.of(2024, 4, 20)
        ),
        RdsPaymentAllocationChequeRow(
          accountingPeriod = 3L,
          apEndDate = LocalDate.of(2025, 3, 31),
          amount = Some(BigDecimal("980.25")),
          effectiveDateOfPayment = LocalDate.of(2025, 5, 1)
        ),
        RdsPaymentAllocationChequeRow(
          accountingPeriod = 4L,
          apEndDate = LocalDate.of(2026, 3, 31),
          amount = Some(BigDecimal("3200.00")),
          effectiveDateOfPayment = LocalDate.of(2026, 4, 10)
        )
      )
    )

}
