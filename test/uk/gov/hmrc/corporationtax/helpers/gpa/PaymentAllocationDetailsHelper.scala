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

package uk.gov.hmrc.corporationtax.helpers.gpa

import uk.gov.hmrc.corporationtax.models.gpa.{AllocationDetails, PaymentAllocationDetails, RdsPaymentAllocationDetails}

import java.time.LocalDate

trait PaymentAllocationDetailsHelper {

  val fullPaymentAllocationDetails: PaymentAllocationDetails =
    PaymentAllocationDetails(
      gppEndDate = LocalDate.of(2024, 7, 24),
      gppTotalGroupPayment = BigDecimal(10),
      gppTotalGroupTax = BigDecimal(10),
      gppStatus = "OPEN",
      gppApportionmentMethod = "HMRC",
      participatingCompanyDesc = "ABC Limited",
      participatorAccPeriodEnd = LocalDate.of(2024, 7, 24),
      participatorTaxCharge = BigDecimal(-10.00),
      participatorAllocPayments = BigDecimal(10),
      gpaUtr = BigDecimal(10),
      gppContractVersionOut = BigDecimal(10),
      allocationDetails = List(
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2024, 7, 24),
          paymentAmount = BigDecimal(-10.00)
        )
      ),
      totalNumOfRecords = BigDecimal(10)
    )

  val paymentAllocationDetailsWithMultipleAllocationDetails: PaymentAllocationDetails =
    PaymentAllocationDetails(
      gppEndDate = LocalDate.of(2025, 7, 24),
      gppTotalGroupPayment = BigDecimal(20),
      gppTotalGroupTax = BigDecimal(20),
      gppStatus = "CLOSED",
      gppApportionmentMethod = "HMRC",
      participatingCompanyDesc = "DEF Limited",
      participatorAccPeriodEnd = LocalDate.of(2025, 7, 24),
      participatorTaxCharge = BigDecimal(-20.00),
      participatorAllocPayments = BigDecimal(20),
      gpaUtr = BigDecimal(20),
      gppContractVersionOut = BigDecimal(20),
      allocationDetails = List(
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2025, 7, 24),
          paymentAmount = BigDecimal(-20.00)
        ),
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2025, 7, 24),
          paymentAmount = BigDecimal(-20.00)
        )
      ),
      totalNumOfRecords = BigDecimal(20)
    )

  val minimalPaymentAllocationDetails: PaymentAllocationDetails =
    PaymentAllocationDetails(
      gppEndDate = LocalDate.of(2026, 7, 24),
      gppTotalGroupPayment = BigDecimal(0),
      gppTotalGroupTax = BigDecimal(0),
      gppStatus = "PARTIAL",
      gppApportionmentMethod = "",
      participatingCompanyDesc = "GHI Limited",
      participatorAccPeriodEnd = LocalDate.of(2026, 7, 24),
      participatorTaxCharge = BigDecimal(-30.00),
      participatorAllocPayments = BigDecimal(30),
      gpaUtr = BigDecimal(30),
      gppContractVersionOut = BigDecimal(30),
      allocationDetails = List(
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2026, 7, 24),
          paymentAmount = BigDecimal(-30.00)
        )
      ),
      totalNumOfRecords = BigDecimal(30)
    )

  val rdsFullPaymentAllocationDetails: RdsPaymentAllocationDetails =
    RdsPaymentAllocationDetails(
      gppEndDate = LocalDate.of(2024, 7, 24),
      gppTotalGroupPayment = Some(BigDecimal(10)),
      gppTotalGroupTax = Some(BigDecimal(10)),
      gppStatus = "OPEN",
      gppApportionmentMethod = Some("HMRC"),
      participatingCompanyDesc = "ABC Limited",
      participatorAccPeriodEnd = LocalDate.of(2024, 7, 24),
      participatorTaxCharge = BigDecimal(10),
      participatorAllocPayments = BigDecimal(10),
      gpaUtr = BigDecimal(10),
      gppContractVersionOut = BigDecimal(10),
      allocationDetails = List(
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2024, 7, 24),
          paymentAmount = BigDecimal(10)
        )
      ),
      totalNumOfRecords = BigDecimal(10)
    )

  val rdsPaymentAllocationDetailsWithMultipleAllocationDetails: RdsPaymentAllocationDetails =
    RdsPaymentAllocationDetails(
      gppEndDate = LocalDate.of(2025, 7, 24),
      gppTotalGroupPayment = Some(BigDecimal(20)),
      gppTotalGroupTax = Some(BigDecimal(20)),
      gppStatus = "CLOSED",
      gppApportionmentMethod = Some("HMRC"),
      participatingCompanyDesc = "DEF Limited",
      participatorAccPeriodEnd = LocalDate.of(2025, 7, 24),
      participatorTaxCharge = BigDecimal(20),
      participatorAllocPayments = BigDecimal(20),
      gpaUtr = BigDecimal(20),
      gppContractVersionOut = BigDecimal(20),
      allocationDetails = List(
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2025, 7, 24),
          paymentAmount = BigDecimal(20)
        ),
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2025, 7, 24),
          paymentAmount = BigDecimal(20)
        )
      ),
      totalNumOfRecords = BigDecimal(20)
    )

  val rdsMinimalPaymentAllocationDetails: RdsPaymentAllocationDetails =
    RdsPaymentAllocationDetails(
      gppEndDate = LocalDate.of(2026, 7, 24),
      gppTotalGroupPayment = None,
      gppTotalGroupTax = None,
      gppStatus = "PARTIAL",
      gppApportionmentMethod = None,
      participatingCompanyDesc = "GHI Limited",
      participatorAccPeriodEnd = LocalDate.of(2026, 7, 24),
      participatorTaxCharge = BigDecimal(30),
      participatorAllocPayments = BigDecimal(30),
      gpaUtr = BigDecimal(30),
      gppContractVersionOut = BigDecimal(30),
      allocationDetails = List(
        AllocationDetails(
          effectivePaymentDate = LocalDate.of(2026, 7, 24),
          paymentAmount = BigDecimal(30)
        )
      ),
      totalNumOfRecords = BigDecimal(30)
    )
}
