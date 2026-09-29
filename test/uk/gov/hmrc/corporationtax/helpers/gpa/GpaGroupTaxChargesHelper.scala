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

package uk.gov.hmrc.corporationtax.helpers.gpa

import uk.gov.hmrc.corporationtax.models.gpa.{
  GpaGroupTaxCharges, ParticipatorDetails, RdsGpaGroupTaxCharges, RdsParticipatorDetails
}

import java.time.LocalDate

trait GpaGroupTaxChargesHelper {

  val rdsGpaWithNonEmptyParticipator: RdsGpaGroupTaxCharges = RdsGpaGroupTaxCharges(
    pGppEndDate = Some(LocalDate.of(2023, 4, 5)),
    pGppTotalGroupPayment = Some(BigDecimal(15000.57876)),
    pGppTotalGroupTax = Some(BigDecimal(-3200.75686)),
    pGppStatus = Some("    SUBMITTED     "),
    pGppCni = Some(LocalDate.of(2023, 3, 1)),
    pGppApportionmentMethod = None,
    pGpaUtr2 = 200L,
    pTotalNumOfRecords = None,
    pGroupPaymentRecordCount = None,
    pCurGroupTaxCharges = List(
      RdsParticipatorDetails(
        participatorName = "Company A Ltd",
        participatorReference = 1234567890L,
        participatorApEndDate = LocalDate.of(2023, 3, 31),
        participatorTaxCharge = BigDecimal(1066.92),
        participatorTaxChargePrsnt = "Y",
        participatorAccountingPeriod = 1L,
        contractVersion = 1L,
        allocatedPayment = BigDecimal(5000.00),
        allocatedPaymentRecordCount = 1
      ),
      RdsParticipatorDetails(
        participatorName = "Company B Ltd",
        participatorReference = 2345678901L,
        participatorApEndDate = LocalDate.of(2023, 3, 31),
        participatorTaxCharge = BigDecimal(1280.11),
        participatorTaxChargePrsnt = "Y",
        participatorAccountingPeriod = 1L,
        contractVersion = 1L,
        allocatedPayment = BigDecimal(6000.50),
        allocatedPaymentRecordCount = 1
      )
    )
  )
  val gpaWithNonEmptyParticipator: GpaGroupTaxCharges       = GpaGroupTaxCharges(
    pGppEndDate = Some(LocalDate.of(2023, 4, 5)),
    pGppTotalGroupPayment = BigDecimal(-15000.58),
    pGppTotalGroupTax = BigDecimal(3200.76),
    pGppStatus = "SUBMITTED",
    pGppCni = Some(LocalDate.of(2023, 3, 1)),
    pGppApportionmentMethod = "",
    pGpaUtr2 = 200L,
    pTotalNumOfRecords = 0,
    pGroupPaymentRecordCount = 0,
    pCurGroupTaxCharges = List(
      ParticipatorDetails(
        participatorName = "Company A Ltd",
        participatorReference = 1234567890L,
        participatorApEndDate = LocalDate.of(2023, 3, 31),
        participatorTaxCharge = BigDecimal(1066.92),
        participatorTaxChargePrsnt = "Y",
        participatorAccountingPeriod = 1L,
        contractVersion = 1L,
        allocatedPayment = BigDecimal(5000.00),
        allocatedPaymentRecordCount = 1
      ),
      ParticipatorDetails(
        participatorName = "Company B Ltd",
        participatorReference = 2345678901L,
        participatorApEndDate = LocalDate.of(2023, 3, 31),
        participatorTaxCharge = BigDecimal(1280.11),
        participatorTaxChargePrsnt = "Y",
        participatorAccountingPeriod = 1L,
        contractVersion = 1L,
        allocatedPayment = BigDecimal(6000.50),
        allocatedPaymentRecordCount = 1
      )
    )
  )

}
