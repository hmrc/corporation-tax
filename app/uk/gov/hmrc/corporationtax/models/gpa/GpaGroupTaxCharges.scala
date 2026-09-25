package uk.gov.hmrc.corporationtax.models.gpa

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class RdsGpaGroupTaxCharges(
  pGppEndDate: Option[LocalDate],
  pGppTotalGroupPayment: Option[BigDecimal],
  pGppTotalGroupTax: Option[BigDecimal],
  pGppStatus: Option[String],
  pGppCni: Option[LocalDate],
  pGppApportionmentMethod: Option[String],
  pGpaUtr2: Long,
  pTotalNumOfRecords: Option[Int],
  pGroupPaymentRecordCount: Option[Int],
  pCurGroupTaxCharges: List[RdsParticipatorDetails]
)

object RdsGpaGroupTaxCharges {
  implicit val format: OFormat[RdsGpaGroupTaxCharges] = Json.format[RdsGpaGroupTaxCharges]
}

case class RdsParticipatorDetails(
  participatorName: String,
  participatorReference: Long,
  participatorApEndDate: LocalDate,
  participatorTaxCharge: BigDecimal,
  participatorTaxChargePrsnt: String,
  participatorAccountingPeriod: Long,
  contractVersion: Long,
  allocatedPayment: BigDecimal,
  allocatedPaymentRecordCount: Int
)

object RdsParticipatorDetails {
  implicit val format: OFormat[RdsParticipatorDetails] = Json.format[RdsParticipatorDetails]

}
//External Contract
case class GpaGroupTaxCharges(
  pGppEndDate: Option[LocalDate],
  pGppTotalGroupPayment: BigDecimal,
  pGppTotalGroupTax: BigDecimal,
  pGppStatus: String,
  pGppCni: LocalDate,
  pGppApportionmentMethod: String,
  pGpaUtr2: Long,
  pTotalNumOfRecords: Int,
  pGroupPaymentRecordCount: Int,
  pCurGroupTaxCharges: List[RdsParticipatorDetails]
)

object GpaGroupTaxCharges {
  implicit val format: OFormat[GpaGroupTaxCharges] = Json.format[GpaGroupTaxCharges]
}

case class ParticipatorDetails(
  participatorName: String,
  participatorReference: Long,
  participatorApEndDate: LocalDate,
  participatorTaxCharge: BigDecimal,
  participatorTaxChargePrsnt: String,
  participatorAccountingPeriod: Long,
  contractVersion: Long,
  allocatedPayment: BigDecimal,
  allocatedPaymentRecordCount: Int
)

object ParticipatorDetails {
  implicit val format: OFormat[ParticipatorDetails] = Json.format[ParticipatorDetails]

}
