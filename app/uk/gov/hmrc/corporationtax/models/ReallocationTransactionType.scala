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

sealed trait ReallocationTransactionType {
  def value: String
}

case object MiscellaneousTransfer extends ReallocationTransactionType {
  override def value: String = "MiscTFR"
}
case object ReallocationTo extends ReallocationTransactionType {
  override def value: String = "RTO"
}
case object ReallocationFrom extends ReallocationTransactionType {
  override def value: String = "RFR"
}

object ReallocationTransactionType {

  val values: Seq[ReallocationTransactionType] = Seq(MiscellaneousTransfer, ReallocationTo)

  private def fromString(value: String): Option[ReallocationTransactionType] =
    values.find(_.value == value)

  implicit val reads: Reads[ReallocationTransactionType] =
    Reads {
      case JsString(s) =>
        fromString(s) match {
          case Some(s) => JsSuccess(s)
          case None    => JsError(s"Unknown ReallocationTransactionType: $s")
        }

      case unexpected => JsError(s"Expected JsString for ReallocationTransactionType, got: $unexpected")
    }

  implicit val writes: Writes[ReallocationTransactionType] =
    Writes(transactionType => JsString(transactionType.value))
}
