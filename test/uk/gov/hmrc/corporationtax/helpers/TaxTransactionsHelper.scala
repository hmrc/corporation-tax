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

import uk.gov.hmrc.corporationtax.models.{TaxTransactions, TaxTransactionsItem}

import java.time.LocalDate

trait TaxTransactionsHelper {

  val emptyTaxTransactions: TaxTransactions = TaxTransactions(List.empty)

  val taxTransactions =
    TaxTransactions(
      List(
        TaxTransactionsItem(
          currentAmount = BigDecimal(123.44),
          assessmentType = "A",
          taxDate = LocalDate.of(2026, 1, 1),
          correctionClaimSignal = Some("0")
        ),
        TaxTransactionsItem(
          currentAmount = BigDecimal(123.44),
          assessmentType = "D",
          taxDate = LocalDate.of(2026, 2, 1),
          correctionClaimSignal = Some("2")
        ),
        TaxTransactionsItem(
          currentAmount = BigDecimal(123.44),
          assessmentType = "E",
          taxDate = LocalDate.of(2026, 3, 1),
          correctionClaimSignal = None
        )
      )
    )

  val taxTransactionsSingleItemList = TaxTransactions(
    List(
      TaxTransactionsItem(
        currentAmount = BigDecimal(123.44),
        assessmentType = "E",
        taxDate = LocalDate.of(2026, 3, 1),
        correctionClaimSignal = None
      )
    )
  )
}
