package uk.gov.hmrc.corporationtax.models.gpa

import play.api.libs.json.{Json, OFormat}

case class PeriodWithinRange(
  isPeriodWithinRange: Boolean
)

object PeriodWithinRange {
  implicit val format: OFormat[PeriodWithinRange] = Json.format[PeriodWithinRange]
}

case class PeriodWithinRangeResponse(
  isPeriodWithinRange: String
)

object PeriodWithinRangeResponse {
  implicit val format: OFormat[PeriodWithinRangeResponse] = Json.format[PeriodWithinRangeResponse]
}
