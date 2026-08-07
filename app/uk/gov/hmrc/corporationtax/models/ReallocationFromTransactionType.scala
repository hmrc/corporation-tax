package uk.gov.hmrc.corporationtax.models

import play.api.libs.json.*

sealed trait TransactionTypesOfGetReallocationFromAcc {
  def value: String
}

case object MiscellaneousTransfer extends TransactionTypesOfGetReallocationFromAcc {
  override def value: String = "MiscTFR"
}
case object ReallocationTo extends TransactionTypesOfGetReallocationFromAcc {
  override def value: String = "RTO"
}

object TransactionTypesOfGetReallocationFromAcc {

  val values: Seq[TransactionTypesOfGetReallocationFromAcc] = Seq(MiscellaneousTransfer, ReallocationTo)

  private def fromString(value: String): Option[TransactionTypesOfGetReallocationFromAcc] =
    values.find(_.value == value)

  implicit val reads: Reads[TransactionTypesOfGetReallocationFromAcc] =
    Reads {
      case JsString(s) =>
        fromString(s) match {
          case Some(s) => JsSuccess(s)
          case None    => JsError(s"Unknown TransactionTypesOfGetReallocationFromAcc: $s")
        }

      case unexpected => JsError(s"Expected JsString for TransactionTypesOfGetReallocationFromAcc, got: $unexpected")
    }

  implicit val writes: Writes[TransactionTypesOfGetReallocationFromAcc] =
    Writes(transactionType => JsString(transactionType.value))
}
