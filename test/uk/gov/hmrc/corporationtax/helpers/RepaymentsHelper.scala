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

import uk.gov.hmrc.corporationtax.models.{Repayments, RepaymentsDetails}
import java.time.LocalDate

trait RepaymentsHelper {

  val emptyRepayments: Repayments =
    Repayments(List.empty)

  val repaymentsWithOneItem: Repayments = Repayments(
    List(
      RepaymentsDetails(
        amount = Some(BigDecimal(10)),
        repaymentType = "S",
        repaymentDate = LocalDate.of(2026, 7, 24)
      )
    )
  )

  val repaymentsWithMultipleItems: Repayments = Repayments(
    List(
      RepaymentsDetails(
        amount = Some(BigDecimal(20)),
        repaymentType = "S",
        repaymentDate = LocalDate.of(2027, 7, 24)
      ),
      RepaymentsDetails(
        amount = Some(BigDecimal(30)),
        repaymentType = "T",
        repaymentDate = LocalDate.of(2028, 7, 24)
      )
    )
  )
}
