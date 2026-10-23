package uk.gov.hmrc.corporationtax.models.payments

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class PaymentPostingResponse(paymentPostings: List[PaymentPostingRow])

object PaymentPostingResponse {
  implicit val format: OFormat[PaymentPostingResponse] = Json.format[PaymentPostingResponse]
}

case class PaymentPostingRow(
  accountingPeriod: Long,
  settlementSequenceNo: Long,
  payslipId: Long,
  postingAmount: BigDecimal,
  effectiveDateOfPayment: LocalDate,
  paymentType: String
)

object PaymentPostingRow {
  implicit val format: OFormat[PaymentPostingRow] = Json.format[PaymentPostingRow]
}
