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

import uk.gov.hmrc.corporationtax.models.{
  MiscellaneousTransfer, ReallocationFrom, ReallocationRow, ReallocationToAccPeriod, ReallocationToAccPeriodRow,
  Reallocations
}

import java.time.LocalDate

trait ReallocationDataHelper {

  val reallocationEmptyList: Reallocations                      = Reallocations(reallocation = List.empty)
  val reallocationToAccPeriodEmptyList: ReallocationToAccPeriod = ReallocationToAccPeriod(reallocation = List.empty)

  val reallocationSingleItem: Reallocations                      = Reallocations(reallocation =
    List(
      ReallocationRow(
        amount = BigDecimal(117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      )
    )
  )
  val reallocationToAccPeriodSingleItem: ReallocationToAccPeriod = ReallocationToAccPeriod(reallocation =
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "9369369363",
        transactionType = MiscellaneousTransfer
      )
    )
  )

  val reallocationWithNoSourceApEndDate: Reallocations                         = Reallocations(reallocation =
    List(
      ReallocationRow(
        amount = BigDecimal(117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = None,
        sourceTaxpayerReference = "9369369363"
      )
    )
  )
  val reallocationToAccPeriodWithEmptySourceApEndDate: ReallocationToAccPeriod = ReallocationToAccPeriod(reallocation =
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "",
        sourceTaxpayerReference = "9369369363",
        transactionType = MiscellaneousTransfer
      )
    )
  )

  val reallocationsTwoItems: Reallocations                      = Reallocations(
    List(
      ReallocationRow(
        amount = BigDecimal(117.01678),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      ),
      ReallocationRow(
        amount = BigDecimal(-29.01567),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      )
    )
  )
  val reallocationsToAccPeriodTwoItems: ReallocationToAccPeriod = ReallocationToAccPeriod(
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "9369369363",
        transactionType = MiscellaneousTransfer
      ),
      ReallocationToAccPeriodRow(
        amount = BigDecimal(29.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "9369369363",
        transactionType = MiscellaneousTransfer
      )
    )
  )

  // Expected results :: negate amount and rounding applied
  val reallocationsExpected: Reallocations = Reallocations(
    List(
      ReallocationRow(
        amount = BigDecimal(-117.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      ),
      ReallocationRow(
        amount = BigDecimal(29.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      )
    )
  )

  val reallocationWithSourceTaxOASTransfer: Reallocations            = Reallocations(reallocation =
    List(
      ReallocationRow(
        amount = BigDecimal(117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "99"
      )
    )
  )
  val reallocationToAccPeriodSingleItemMisc: ReallocationToAccPeriod = ReallocationToAccPeriod(reallocation =
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "99",
        transactionType = MiscellaneousTransfer
      )
    )
  )

  val reallocationsWithSourceTaxRefDifferentToTaxRef: Reallocations            = Reallocations(
    List(
      ReallocationRow(
        amount = BigDecimal(117.01678),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      ),
      ReallocationRow(
        amount = BigDecimal(-29.01567),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "93"
      )
    )
  )
  val reallocationsToAccPeriodWithTransactionTypeMisc: ReallocationToAccPeriod = ReallocationToAccPeriod(
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "9369369363",
        transactionType = MiscellaneousTransfer
      ),
      ReallocationToAccPeriodRow(
        amount = BigDecimal(29.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "93",
        transactionType = MiscellaneousTransfer
      )
    )
  )

  val reallocationsWithSourceAPEndDateNotDefined: Reallocations                  = Reallocations(
    List(
      ReallocationRow(
        amount = BigDecimal(117.01678),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = None,
        sourceTaxpayerReference = "87"
      ),
      ReallocationRow(
        amount = BigDecimal(-29.01567),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = None,
        sourceTaxpayerReference = "87"
      )
    )
  )
  val reallocationsToAccPeriodWithTransactionTypeMiscFR: ReallocationToAccPeriod = ReallocationToAccPeriod(
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "",
        sourceTaxpayerReference = "87",
        transactionType = MiscellaneousTransfer
      ),
      ReallocationToAccPeriodRow(
        amount = BigDecimal(29.02),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "",
        sourceTaxpayerReference = "87",
        transactionType = MiscellaneousTransfer
      )
    )
  )

  val reallocationWithEqualSourceTaxRefAndTaxRef: Reallocations              = Reallocations(reallocation =
    List(
      ReallocationRow(
        amount = BigDecimal(117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "88"
      )
    )
  )
  val reallocationToAccPeriodWithTransactionTypeRFR: ReallocationToAccPeriod = ReallocationToAccPeriod(reallocation =
    List(
      ReallocationToAccPeriodRow(
        amount = BigDecimal(-117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = "2026-07-01",
        sourceTaxpayerReference = "88",
        transactionType = ReallocationFrom
      )
    )
  )

}
