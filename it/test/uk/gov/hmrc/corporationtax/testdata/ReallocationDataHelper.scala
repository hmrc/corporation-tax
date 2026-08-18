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

import uk.gov.hmrc.corporationtax.models.{ReallocationRow, Reallocations}

import java.time.LocalDate

trait ReallocationDataHelper {

  val reallocationEmpty: Reallocations = Reallocations(reallocation = List.empty)

  val reallocationSingleItem: Reallocations = Reallocations(reallocation =
    List(
      ReallocationRow(
        amount = BigDecimal(117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      )
    )
  )

  val reallocationsTwoItems: Reallocations = Reallocations(
    List(
      ReallocationRow(
        amount = BigDecimal(117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      ),
      ReallocationRow(
        amount = BigDecimal(-29.01),
        reallocationDate = LocalDate.of(2021, 9, 1),
        sourceApEndDate = Some(LocalDate.of(2023, 7, 1)),
        sourceTaxpayerReference = "2369369361"
      )
    )
  )

  // Expected results :: negate amount
  val reallocationsExpected: Reallocations = Reallocations(
    List(
      ReallocationRow(
        amount = BigDecimal(-117.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      ),
      ReallocationRow(
        amount = BigDecimal(29.01),
        reallocationDate = LocalDate.of(2025, 5, 1),
        sourceApEndDate = Some(LocalDate.of(2026, 7, 1)),
        sourceTaxpayerReference = "9369369363"
      )
    )
  )

}
