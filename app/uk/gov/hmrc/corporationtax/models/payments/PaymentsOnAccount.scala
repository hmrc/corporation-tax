package uk.gov.hmrc.corporationtax.models.payments

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class PaymentsOnAccount(paymentsOnAccount: List[PaymentsOnAccount])

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
