package uk.gov.hmrc.corporationtax.models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDate

case class ReallocationFromAccPeriod(reallocation: List[ReallocationFromAccDetails])

object ReallocationFromAccPeriod {
  implicit val format: OFormat[ReallocationFromAccPeriod] = Json.format[ReallocationFromAccPeriod]
}

case class ReallocationFromAccDetails(amount: Option[BigDecimal],
                                      reallocationDate: Option[LocalDate],
                                      destinationApEndDate: Option[LocalDate],
                                      destinationTaxPayerReference: Option[String]
                                     )

object ReallocationFromAccDetails {

  implicit val format: OFormat[ReallocationFromAccDetails] = Json.format[ReallocationFromAccDetails]
}
