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

import uk.gov.hmrc.corporationtax.models.payments.{
  ElectronicPaymentAllocationDetails, ElectronicPaymentAllocationInfo, PaymentAllocation,
  PaymentAllocationChequeDetails, PaymentAllocationChequeRow, PaymentAllocationInfo, PaymentSearchResult,
  PaymentSearchResultResponse
}

import java.time.LocalDate

trait DeterminePaymentAllocationHelper {

  val electronicPaymentAllocationDetails: ElectronicPaymentAllocationDetails = ElectronicPaymentAllocationDetails(
    List(
      ElectronicPaymentAllocationInfo(
        accountingPeriod = BigDecimal(10),
        apEndDate = LocalDate.of(2024, 8, 31),
        amount = BigDecimal(10.00),
        effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
      )
    )
  )

  val chequePaymentAllocationDetails: PaymentAllocationChequeDetails = PaymentAllocationChequeDetails(
    List(
      PaymentAllocationChequeRow(
        accountingPeriod = 10L,
        apEndDate = LocalDate.of(2024, 8, 31),
        amount = BigDecimal(10.00),
        effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
      )
    )
  )

  val paymentAllocation: List[PaymentAllocation] = List(
    PaymentAllocation(
      List(
        PaymentAllocationInfo(
          accountingPeriod = BigDecimal(10),
          apEndDate = LocalDate.of(2024, 8, 31),
          amount = BigDecimal(10.00),
          effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
        )
      )
    )
  )

  val multiPaymentAllocation: List[PaymentAllocation] = List(
    PaymentAllocation(
      List(
        PaymentAllocationInfo(
          accountingPeriod = BigDecimal(10),
          apEndDate = LocalDate.of(2024, 8, 31),
          amount = BigDecimal(10.00),
          effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
        )
      )
    ),
    PaymentAllocation(
      List(
        PaymentAllocationInfo(
          accountingPeriod = BigDecimal(10),
          apEndDate = LocalDate.of(2024, 8, 31),
          amount = BigDecimal(10.00),
          effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
        )
      )
    )
  )

  val multiElectronicPaymentAllocationDetails: ElectronicPaymentAllocationDetails = ElectronicPaymentAllocationDetails(
    List(
      ElectronicPaymentAllocationInfo(
        accountingPeriod = BigDecimal(10),
        apEndDate = LocalDate.of(2024, 8, 31),
        amount = BigDecimal(10.00),
        effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
      ),
      ElectronicPaymentAllocationInfo(
        accountingPeriod = BigDecimal(10),
        apEndDate = LocalDate.of(2024, 8, 31),
        amount = BigDecimal(10.00),
        effectiveDateOfPayment = LocalDate.of(2024, 8, 31)
      )
    )
  )

  val multiChequePaymentAllocationDetails: PaymentAllocationChequeDetails = PaymentAllocationChequeDetails(
    List(
      PaymentAllocationChequeRow(
        accountingPeriod = 1L,
        apEndDate = LocalDate.of(2023, 3, 31),
        amount = BigDecimal("1500.47"),
        effectiveDateOfPayment = LocalDate.of(2023, 4, 15)
      ),
      PaymentAllocationChequeRow(
        accountingPeriod = 1L,
        apEndDate = LocalDate.of(2023, 3, 31),
        amount = BigDecimal("1500.47"),
        effectiveDateOfPayment = LocalDate.of(2023, 4, 15)
      )
    )
  )

  val electronicOneItemPaymentSearchResult: PaymentSearchResultResponse =
    PaymentSearchResultResponse(paymentSearchResult =
      List(
        PaymentSearchResult(
          accountingPeriod = 1,
          payslipId = 1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 0
        )
      )
    )

  val chequeOneItemPaymentSearchResult: PaymentSearchResultResponse = PaymentSearchResultResponse(paymentSearchResult =
    List(
      PaymentSearchResult(
        accountingPeriod = -1,
        payslipId = -1,
        amount = -37500,
        effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
        paymentType = "BGP",
        bankSortCode = 114433,
        bankAccountNumber = 1004005,
        chequeNumber = 100301,
        paymentAmount = -31250,
        chequeId = 6
      )
    )
  )

  val emptyElecAndCheqPaymentSearchResult: PaymentSearchResultResponse =
    PaymentSearchResultResponse(paymentSearchResult =
      List(
        PaymentSearchResult(
          accountingPeriod = 1,
          payslipId = 1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 6
        )
      )
    )

  val electronicMultiItemPaymentSearchResult: PaymentSearchResultResponse =
    PaymentSearchResultResponse(paymentSearchResult =
      List(
        PaymentSearchResult(
          accountingPeriod = 1,
          payslipId = 1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 0
        ),
        PaymentSearchResult(
          accountingPeriod = 1,
          payslipId = 1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = -1
        )
      )
    )

  val chequeMultiItemPaymentSearchResult: PaymentSearchResultResponse =
    PaymentSearchResultResponse(paymentSearchResult =
      List(
        PaymentSearchResult(
          accountingPeriod = -1,
          payslipId = -1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 5
        ),
        PaymentSearchResult(
          accountingPeriod = -1,
          payslipId = -1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 6
        )
      )
    )

  val mixedElectAndCheqPaymentSearchResult: PaymentSearchResultResponse =
    PaymentSearchResultResponse(paymentSearchResult =
      List(
        PaymentSearchResult(
          accountingPeriod = 1,
          payslipId = 1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 0
        ),
        PaymentSearchResult(
          accountingPeriod = -1,
          payslipId = -1,
          amount = -37500,
          effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
          paymentType = "BGP",
          bankSortCode = 114433,
          bankAccountNumber = 1004005,
          chequeNumber = 100301,
          paymentAmount = -31250,
          chequeId = 6
        )
      )
    )
}
