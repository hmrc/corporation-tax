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

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class Reallocations(reallocation: List[ReallocationRow])
case class ReallocationRow(
  amount: BigDecimal,
  reallocationDate: LocalDate,
  sourceApEndDate: Option[LocalDate],
  sourceTaxpayerReference: String
)

object Reallocations {
  implicit val format: OFormat[Reallocations] = Json.format[Reallocations]
}

object ReallocationRow {
  implicit val format: OFormat[ReallocationRow] = Json.format[ReallocationRow]
}

case class ReallocationToAccPeriod(reallocation: List[ReallocationToAccPeriodRow])

object ReallocationToAccPeriod {
  implicit val format: OFormat[ReallocationToAccPeriod] = Json.format[ReallocationToAccPeriod]
}

case class ReallocationToAccPeriodRow(
  amount: BigDecimal,
  reallocationDate: LocalDate,
  sourceApEndDate: String,
  sourceTaxpayerReference: String,
  transactionType: ReallocationTransactionType
)

object ReallocationToAccPeriodRow {
  implicit val format: OFormat[ReallocationToAccPeriodRow] = Json.format[ReallocationToAccPeriodRow]
}
