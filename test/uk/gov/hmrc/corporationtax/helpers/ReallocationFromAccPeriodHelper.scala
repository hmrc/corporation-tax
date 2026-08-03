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

import uk.gov.hmrc.corporationtax.models.{ReallocationFromAccDetails, ReallocationFromAccPeriod}

import java.time.LocalDate

trait ReallocationFromAccPeriodHelper {

  val emptyListReallocationFromAccPeriod: ReallocationFromAccPeriod = ReallocationFromAccPeriod(List.empty)

  val reallocationFromAccPeriodWithTwoElements: ReallocationFromAccPeriod = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(12390)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(12345)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )
  val reallocationFromAccPeriodWithZeroAmount: ReallocationFromAccPeriod  = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(0.00)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(0.00)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )

  val reallocationFromAccPeriodWithThreeElements: ReallocationFromAccPeriod = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(12390.00)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(180007.00)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(89075.00)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )

  val reallocationFromAccPeriodWithNullAmount: ReallocationFromAccPeriod        = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        None,
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        None,
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )
  val transformedNullAmountReallocationFromAccPeriod: ReallocationFromAccPeriod = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(0.00),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(0.00),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )

  val reallocationFromAccPeriodWithAmount3DecimalPlaces: ReallocationFromAccPeriod        = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(12390.986)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(180007.8654)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(89075.3654)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )
  val transformed3DecimalPlacesAmountReallocationFromAccPeriod: ReallocationFromAccPeriod = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(-12390.99)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(-180007.87)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(-89075.37)),
        Some(LocalDate.of(2026, 12, 27)),
        Some(LocalDate.of(2024, 2, 2)),
        Some("18969779586")
      )
    )
  )

}
