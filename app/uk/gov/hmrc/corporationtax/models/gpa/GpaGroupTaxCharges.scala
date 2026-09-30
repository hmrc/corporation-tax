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
  participatorTaxCharge: Option[BigDecimal],
  participatorTaxChargePrsnt: String,
  participatorAccountingPeriod: Long,
  contractVersion: Long,
  allocatedPayment: Option[BigDecimal],
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
  pGppCni: Option[LocalDate],
  pGppApportionmentMethod: String,
  pGpaUtr2: Long,
  pTotalNumOfRecords: Int,
  pGroupPaymentRecordCount: Int,
  pCurGroupTaxCharges: List[ParticipatorDetails]
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
