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

package uk.gov.hmrc.corporationtax.models.payments

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class PaymentsOnAccount(paymentsOnAccount: List[PaymentsOnAccountRow])

object PaymentsOnAccount {
  implicit val format: OFormat[PaymentsOnAccount] = Json.format[PaymentsOnAccount]
}

case class PaymentsOnAccountRow(
  accPeriod: Long,
  effectiveDateOfPayment: LocalDate,
  apEndDate: Option[LocalDate],
  paymentType: String,
  postingAmount: BigDecimal,
  isPartPayment: Boolean
)

object PaymentsOnAccountRow {
  implicit val format: OFormat[PaymentsOnAccountRow] = Json.format[PaymentsOnAccountRow]
}
