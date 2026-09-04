package uk.gov.hmrc.corporationtax.models

import play.api.libs.json.{Json, OFormat}

case class DisplayNeeded(
  taxIsDisplayNeededFlag: Boolean,
  interestIsDisplayNeededFlag: Boolean,
  paymentIsDisplayNeededFlag: Boolean,
  repayReallocIsDisplayNeededFlag: Boolean
)

object DisplayNeeded {
  implicit val format: OFormat[DisplayNeeded] = Json.format[DisplayNeeded]
}

case class DisplayNeededResponse(
  taxIsDisplayNeededFlag: String,
  interestIsDisplayNeededFlag: String,
  paymentIsDisplayNeededFlag: String,
  repayReallocIsDisplayNeededFlag: String
)

object DisplayNeededResponse {
  implicit val format: OFormat[DisplayNeededResponse] = Json.format[DisplayNeededResponse]
}
