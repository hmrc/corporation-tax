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

package uk.gov.hmrc.corporationtax.testdata.payments

import uk.gov.hmrc.corporationtax.models.payments.{ElectronicPaymentAllocationDetails, ElectronicPaymentAllocationInfo}

import java.time.LocalDate

trait ElectronicPaymentAllocationDetailsHelper {

  val emptyEPaymentAllocationDetails: ElectronicPaymentAllocationDetails = ElectronicPaymentAllocationDetails(List.empty)

  val ePaymentAllocationDetails: ElectronicPaymentAllocationDetails = ElectronicPaymentAllocationDetails(
    List(
      ElectronicPaymentAllocationInfo(
        accountingPeriod       = BigDecimal(10),
        apEndDate              = LocalDate.of(2024, 8, 31),
        amount                 = BigDecimal(10),
        effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
      )
    )
  )

  val multipleEPaymentAllocationDetails: ElectronicPaymentAllocationDetails = ElectronicPaymentAllocationDetails(
    List(
      ElectronicPaymentAllocationInfo(
        accountingPeriod       = BigDecimal(20),
        apEndDate              = LocalDate.of(2024, 9, 30),
        amount                 = BigDecimal(20),
        effectiveDateOfPayment = LocalDate.of(2024, 9, 30)
      ),
      ElectronicPaymentAllocationInfo(
        accountingPeriod       = BigDecimal(30),
        apEndDate              = LocalDate.of(2024, 10, 31),
        amount                 = BigDecimal(30),
        effectiveDateOfPayment = LocalDate.of(2024, 10, 31)
      )
    )
  )
}
