package uk.gov.hmrc.corporationtax.testdata.gpa

import uk.gov.hmrc.corporationtax.models.gpa.{RdsGpaGroupTaxCharges, RdsParticipatorDetails}

import java.time.LocalDate

trait GpaGroupTaxChargesHelper {

  val gpaWithNonEmptyParticipator: RdsGpaGroupTaxCharges = RdsGpaGroupTaxCharges(
    pGppEndDate = Some(LocalDate.of(2023, 4, 5)),
    pGppTotalGroupPayment = Some(BigDecimal(15000.50)),
    pGppTotalGroupTax = Some(BigDecimal(3200.75)),
    pGppStatus = Some("SUBMITTED"),
    pGppCni = Some(LocalDate.of(2023, 3, 1)),
    pGppApportionmentMethod = Some("EQUAL"),
    pGpaUtr2 = 200L,
    pTotalNumOfRecords = Some(3),
    pGroupPaymentRecordCount = Some(3),
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

}
