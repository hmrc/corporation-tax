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

import uk.gov.hmrc.corporationtax.models.{ReallocationFrom, ReallocationFromAccDetails, ReallocationFromAccPeriod, ReallocationTo, ReallocationToAccPeriod, ReallocationToAccPeriodRow, RepayReallocationSummary, RepayReallocationSummaryDetails, Repayments, RepaymentsDetails}

import java.time.LocalDate

trait RepayReallocationSummaryHelper {

  val emptyRepayReallocation: RepayReallocationSummary =
    RepayReallocationSummary(List.empty)

  val repayReallocationWithOneItem: RepayReallocationSummary = RepayReallocationSummary(
    List(
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2026, 5, 24)),
        `type` = Some("CRT"),
        amount = Some(BigDecimal(10)),
        accountingPeriodEndDate = None,
        taxpayerReference = None
      )
    )
  )

  val repayReallocationWithRepaymentsItem: RepayReallocationSummary = RepayReallocationSummary(
    List(
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2026, 5, 24)),
        `type` = Some("CRT"),
        amount = Some(BigDecimal(10)),
        accountingPeriodEndDate = None,
        taxpayerReference = None
      )
    )
  )

  val repayReallocationWithReallocationFromItem: RepayReallocationSummary = RepayReallocationSummary(
    List(
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2012, 12, 2)),
        `type` = Some(ReallocationFrom.value),
        amount = Some(BigDecimal(20)),
        accountingPeriodEndDate = Some(LocalDate.of(2017, 12, 2)),
        taxpayerReference = Some("12345")
      )
    )
  )

  val repayReallocationWithReallocationToItem: RepayReallocationSummary = RepayReallocationSummary(
    List(
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2025, 5, 1)),
        `type` = Some(ReallocationTo.value),
        amount = Some(BigDecimal(30)),
        accountingPeriodEndDate = Some(LocalDate.of(2026, 7, 1)),
        taxpayerReference = Some("9369369363")
      )
    )
  )

  val repayReallocationWithOneOfEachItem: RepayReallocationSummary = RepayReallocationSummary(
    List(
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2026, 5, 24)),
        `type` = Some("CRT"),
        amount = Some(BigDecimal(10)),
        accountingPeriodEndDate = None,
        taxpayerReference = None
      ),
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2025, 5, 1)),
        `type` = Some(ReallocationTo.value),
        amount = Some(BigDecimal(30)),
        accountingPeriodEndDate = Some(LocalDate.of(2026, 7, 1)),
        taxpayerReference = Some("9369369363")
      ),
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2012, 12, 2)),
        `type` = Some(ReallocationFrom.value),
        amount = Some(BigDecimal(20)),
        accountingPeriodEndDate = Some(LocalDate.of(2017, 12, 2)),
        taxpayerReference = Some("12345")
      )
    )
  )

  val repayReallocationWithMultipleItems: RepayReallocationSummary = RepayReallocationSummary(
    List(
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2025, 5, 24)),
        `type` = Some("P"),
        amount = Some(BigDecimal(20)),
        accountingPeriodEndDate = Some(LocalDate.of(2026, 5, 24)),
        taxpayerReference = Some("taxPayerReference2")
      ),
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2024, 5, 24)),
        `type` = Some("T"),
        amount = Some(BigDecimal(30)),
        accountingPeriodEndDate = Some(LocalDate.of(2025, 5, 24)),
        taxpayerReference = Some("taxPayerReference3")
      ),
      RepayReallocationSummaryDetails(
        transactionDate = Some(LocalDate.of(2024, 5, 24)),
        `type` = Some("CRT"),
        amount = Some(BigDecimal(40)),
        accountingPeriodEndDate = None,
        taxpayerReference = None
      )
    )
  )

  val emptyRepayments: Repayments = Repayments(List.empty)

  val repaymentsWithOneItem: Repayments = Repayments(
    List(
      RepaymentsDetails(
        amount = Some(BigDecimal(10)),
        repaymentType = "CRT",
        repaymentDate = LocalDate.of(2026, 5, 24)
      )
    )
  )

  val emptyReallocationFrom: ReallocationFromAccPeriod =
    ReallocationFromAccPeriod(List.empty)

  val reallocationFromWithOneItem: ReallocationFromAccPeriod = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        amount = BigDecimal(20),
        reallocationDate = LocalDate.of(2012, 12, 2),
        destinationApEndDate = Some(LocalDate.of(2017, 12, 2)),
        destinationTaxPayerReference = "12345",
        transactionType = ReallocationFrom
      )
    )
  )

  val emptyReallocationTo: ReallocationToAccPeriod = ReallocationToAccPeriod(List.empty)

  val reallocationToWithOneItem: ReallocationToAccPeriod = ReallocationToAccPeriod(
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(30),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363",
        transactionType = ReallocationTo
      )
    )
  )
}
