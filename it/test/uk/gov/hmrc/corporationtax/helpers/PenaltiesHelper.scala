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

import uk.gov.hmrc.corporationtax.models.{Penalties, PenaltyTransaction}

import java.time.LocalDate

trait PenaltiesHelper {

  val penaltiesEmptyList: Penalties = Penalties(penaltyTransactions = List.empty)

  val penaltiesSingleItemList: Penalties = Penalties(
    List(
      PenaltyTransaction(
        penaltyDate = LocalDate.of(2025, 5, 1),
        `type` = "F",
        postingAmount = BigDecimal(100.13)
      )
    )
  )

  val penaltiesTwoItemList: Penalties = Penalties(
    List(
      PenaltyTransaction(
        penaltyDate = LocalDate.of(2025, 5, 1),
        `type` = "F",
        postingAmount = BigDecimal(100.13)
      ),
      PenaltyTransaction(
        penaltyDate = LocalDate.of(2021, 3, 7),
        `type` = "G", postingAmount = BigDecimal(27.19))
    )
  )
  
}
