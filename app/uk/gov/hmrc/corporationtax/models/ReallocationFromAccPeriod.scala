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

case class RdsReallocationFromAccPeriodResponse(reallocation: List[RdsReallocationFromAccDetails])

object RdsReallocationFromAccPeriodResponse {
  implicit val format: OFormat[RdsReallocationFromAccPeriodResponse] = Json.format[RdsReallocationFromAccPeriodResponse]
}

case class RdsReallocationFromAccDetails(
  amount: Option[BigDecimal],
  reallocationDate: LocalDate,
  destinationApEndDate: Option[LocalDate],
  destinationTaxPayerReference: String
)

object RdsReallocationFromAccDetails {

  implicit val format: OFormat[RdsReallocationFromAccDetails] = Json.format[RdsReallocationFromAccDetails]
}

case class ReallocationFromAccPeriod(reallocation: List[ReallocationFromAccDetails])

object ReallocationFromAccPeriod {
  implicit val format: OFormat[ReallocationFromAccPeriod] = Json.format[ReallocationFromAccPeriod]
}

case class ReallocationFromAccDetails(
  amount: BigDecimal,
  reallocationDate: LocalDate,
  destinationApEndDate: Option[LocalDate],
  destinationTaxPayerReference: String,
  transactionType: ReallocationTransactionType
)

object ReallocationFromAccDetails {

  implicit val format: OFormat[ReallocationFromAccDetails] =
    Json.format[ReallocationFromAccDetails]
}
