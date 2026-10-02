package uk.gov.hmrc.corporationtax.models

import play.api.libs.json.{Json, OFormat}

case class RdsCompanyDetailsResponse(taxpayerDetails: List[RdsCompanyDetails])

object RdsCompanyDetailsResponse {
  implicit val format: OFormat[RdsCompanyDetailsResponse] = Json.format[RdsCompanyDetailsResponse]
}
case class RdsCompanyDetails(
  orgUnitId: String,
  companyName: String,
  companyRegNo: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  postCode: Option[String]
)

object RdsCompanyDetails {
  implicit val format: OFormat[RdsCompanyDetails] = Json.format[RdsCompanyDetails]
}

// External Contract
case class CompanyDetailsResponse(taxpayerDetails: List[CompanyDetails])

object CompanyDetailsResponse {
  implicit val format: OFormat[CompanyDetailsResponse] = Json.format[CompanyDetailsResponse]
}
case class CompanyDetails(
  orgUnitId: String,
  companyName: String,
  companyRegNo: String,
  addressLine1: String,
  addressLine2: String,
  addressLine3: String,
  addressLine4: String,
  postCode: String
)

object CompanyDetails {
  implicit val format: OFormat[CompanyDetails] = Json.format[CompanyDetails]
}
