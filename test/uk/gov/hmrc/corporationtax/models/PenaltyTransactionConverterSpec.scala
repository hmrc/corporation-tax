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

package uk.gov.hmrc.corporationtax.models

import org.scalatest.flatspec.AnyFlatSpec
import uk.gov.hmrc.corporationtax.models.PenaltyTransactionType.*

import java.time.LocalDate

class PenaltyTransactionConverterSpec extends AnyFlatSpec {

  it should "convert Penalty type F with isCPTF true to FX" in {
    val penalty = PenaltyTransaction(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = "F",
      postingAmount = BigDecimal(100.13)
    )
    val expected = PenaltyTransactionItem(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = FX,
      postingAmount = BigDecimal(100.13)
    )
    assert(
      Penalties.convertToItems(penalty, true) == expected
    )
  }

  it should "convert Penalty type F with isCPTF false to FT" in {
    val penalty = PenaltyTransaction(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = "F",
      postingAmount = BigDecimal(100.13)
    )
    val expected = PenaltyTransactionItem(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = FT,
      postingAmount = BigDecimal(100.13)
    )
    assert(
      Penalties.convertToItems(penalty, false) == expected
    )
  }

  it should "convert Penalty type not F with isCPTF true to TG" in {
    val penalty = PenaltyTransaction(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = "G",
      postingAmount = BigDecimal(100.13)
    )
    val expected = PenaltyTransactionItem(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = TG,
      postingAmount = BigDecimal(100.13)
    )
    assert(
      Penalties.convertToItems(penalty, true) == expected
    )
  }

  it should "convert Penalty type not F with isCPTF false to FX" in {
    val penalty = PenaltyTransaction(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = "G",
      postingAmount = BigDecimal(100.13)
    )
    val expected = PenaltyTransactionItem(
      penaltyDate = LocalDate.of(2025, 5, 1),
      `type` = TR,
      postingAmount = BigDecimal(100.13)
    )
    assert(
      Penalties.convertToItems(penalty, true) == expected
    )
  }


}
