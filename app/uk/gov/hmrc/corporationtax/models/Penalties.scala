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

import play.api.libs.json.*

import java.time.LocalDate

case class Penalties(penaltyTransactions: List[PenaltyTransaction])

object Penalties {
  implicit val format: OFormat[Penalties] = Json.format[Penalties]
}

case class PenaltyTransaction(
  penaltyDate: LocalDate,
  `type`: String,
  postingAmount: BigDecimal
)

object PenaltyTransaction {
  implicit val format: OFormat[PenaltyTransaction] = Json.format[PenaltyTransaction]

  def convertToItems(penalty: PenaltyTransaction, isCTPF: Boolean): PenaltyTransactionItem =
    (penalty.`type`.toUpperCase(), isCTPF) match {
      case ("F", true)  =>
        PenaltyTransactionItem(
          penaltyDate = penalty.penaltyDate,
          `type` = PenaltyTransactionType.FX,
          postingAmount = penalty.postingAmount
        )
      case ("F", false) =>
        PenaltyTransactionItem(
          penaltyDate = penalty.penaltyDate,
          `type` = PenaltyTransactionType.FT,
          postingAmount = penalty.postingAmount
        )
      case (_, true)    =>
        PenaltyTransactionItem(
          penaltyDate = penalty.penaltyDate,
          `type` = PenaltyTransactionType.TG,
          postingAmount = penalty.postingAmount
        )
      case (_, false)   =>
        PenaltyTransactionItem(
          penaltyDate = penalty.penaltyDate,
          `type` = PenaltyTransactionType.TR,
          postingAmount = penalty.postingAmount
        )
    }

}

enum PenaltyTransactionType:
  case FX, FT, TG, TR

implicit val penaltyTransactionType: Format[PenaltyTransactionType] = new Format[PenaltyTransactionType] {
  def reads(json: JsValue): JsResult[PenaltyTransactionType] = json match {
    case JsString(s) =>
      s.toLowerCase() match {
        case "FX" => JsSuccess(PenaltyTransactionType.FX)
        case "FT" => JsSuccess(PenaltyTransactionType.FT)
        case "TG" => JsSuccess(PenaltyTransactionType.TG)
        case "TR" => JsSuccess(PenaltyTransactionType.TR)
        case _    => JsError("Invalid PenaltyTransactionType string")
      }
    case _           => JsError("Expected JsString")
  }

  def writes(penaltyType: PenaltyTransactionType): JsValue = JsString(penaltyType.toString.toUpperCase())
}

case class PenaltyTransactionItem(
  penaltyDate: LocalDate,
  `type`: PenaltyTransactionType,
  postingAmount: BigDecimal
)

object PenaltyTransactionItem {
  implicit val format: OFormat[PenaltyTransactionItem] = Json.format[PenaltyTransactionItem]
}

case class PenaltyItems(penaltyTransactions: List[PenaltyTransactionItem])

object PenaltyItems {
  implicit val format: OFormat[PenaltyItems] = Json.format[PenaltyItems]
}
