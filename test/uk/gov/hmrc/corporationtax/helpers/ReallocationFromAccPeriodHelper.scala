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
  ReallocationFromAccDetails, ReallocationFromAccPeriod, TransformedReallocationFromAccDetails,
  TransformedReallocationFromAccPeriod
}

import java.time.LocalDate

trait ReallocationFromAccPeriodHelper {

  val emptyListReallocationFromAccPeriod: ReallocationFromAccPeriod                       = ReallocationFromAccPeriod(List.empty)
  val emptyTransformedListReallocationFromAccPeriod: TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(List.empty)

  val reallocationFromAccPeriodWithTwoElements: ReallocationFromAccPeriod                      = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(12390)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(12345)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      )
    )
  )
  val reallocationFromAccPeriodWithZeroAmount: ReallocationFromAccPeriod                       = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(0.00)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(0.00)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      )
    )
  )
  val transformedReallocationFromAccPeriodWithZeroAmount: TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(
      List(
        TransformedReallocationFromAccDetails(
          BigDecimal(0.00),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        ),
        TransformedReallocationFromAccDetails(
          BigDecimal(0.00),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        )
      )
    )

  val reallocationFromAccPeriodWithNullDestinationApEndDateAmount: ReallocationFromAccPeriod                 =
    ReallocationFromAccPeriod(
      List(
        ReallocationFromAccDetails(
          Some(BigDecimal(0.00)),
          LocalDate.of(2026, 12, 27),
          None,
          "18969779586"
        ),
        ReallocationFromAccDetails(
          Some(BigDecimal(0.00)),
          LocalDate.of(2026, 12, 27),
          None,
          "18969779586"
        )
      )
    )
  val transformedReallocationFromAccPeriodWithNullDestinationApEndDate: TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(
      List(
        TransformedReallocationFromAccDetails(
          BigDecimal(0.00),
          LocalDate.of(2026, 12, 27),
          "",
          "18969779586"
        ),
        TransformedReallocationFromAccDetails(
          BigDecimal(0.00),
          LocalDate.of(2026, 12, 27),
          "",
          "18969779586"
        )
      )
    )

  val reallocationFromAccPeriodWithThreeElements: ReallocationFromAccPeriod = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(12390.00)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(180007.00)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(89075.00)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      )
    )
  )

  val reallocationFromAccPeriodWithNullAmount: ReallocationFromAccPeriod                   = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        None,
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        None,
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      )
    )
  )
  val transformedNullAmountReallocationFromAccPeriod: TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(
      List(
        TransformedReallocationFromAccDetails(
          BigDecimal(0.00),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        ),
        TransformedReallocationFromAccDetails(
          BigDecimal(0.00),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        )
      )
    )

  val reallocationFromAccPeriodWithAmount3DecimalPlaces: ReallocationFromAccPeriod                   = ReallocationFromAccPeriod(
    List(
      ReallocationFromAccDetails(
        Some(BigDecimal(12390.986)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(180007.8654)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      ),
      ReallocationFromAccDetails(
        Some(BigDecimal(89075.3654)),
        LocalDate.of(2026, 12, 27),
        Some(LocalDate.of(2024, 2, 2)),
        "18969779586"
      )
    )
  )
  val transformed3DecimalPlacesAmountReallocationFromAccPeriod: TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(
      List(
        TransformedReallocationFromAccDetails(
          BigDecimal(-12390.99),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        ),
        TransformedReallocationFromAccDetails(
          BigDecimal(-180007.87),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        ),
        TransformedReallocationFromAccDetails(
          BigDecimal(-89075.37),
          LocalDate.of(2026, 12, 27),
          "2024-02-02",
          "18969779586"
        )
      )
    )

}
