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

// RDS
case class ApAmountItem(
  accountingPeriod: Long,
  apEndDate: Option[LocalDate],
  amountDueForAp: Option[BigDecimal],
  apStatus: Option[String]
)

object ApAmountItem {
  implicit val format: OFormat[ApAmountItem] = Json.format[ApAmountItem]
}

case class AccountPosition(
  amountDue: Option[BigDecimal],
  asOnDate: Option[LocalDate],
  gpaLinkFlag: Option[String],
  taxpayerList: List[String],
  apAmounts: List[ApAmountItem],
  doesCompanyExist: Option[String]
)

object AccountPosition {
  implicit val format: OFormat[AccountPosition] = Json.format[AccountPosition]
}

// BE
case class ApAmountRecord(
  accountingPeriod: Long,
  apEndDate: Option[LocalDate],
  amountDueForAp: BigDecimal,
  apStatus: Option[String]
)

object ApAmountRecord {
  implicit val format: OFormat[ApAmountRecord] = Json.format[ApAmountRecord]
}

case class AccountPositionResponse(
  amountDue: BigDecimal,
  asOnDate: Option[LocalDate],
  gpaLinkFlag: Boolean,
  taxpayerList: List[String],
  apAmounts: List[ApAmountRecord],
  doesCompanyExist: Boolean
)

object AccountPositionResponse {
  implicit val format: OFormat[AccountPositionResponse] = Json.format[AccountPositionResponse]
}
