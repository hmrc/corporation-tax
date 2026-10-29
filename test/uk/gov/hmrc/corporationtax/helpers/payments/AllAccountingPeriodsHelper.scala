package uk.gov.hmrc.corporationtax.helpers.payments

import uk.gov.hmrc.corporationtax.models.payments.{
  AccountingPeriodList, AllAccountingPeriods, RdsAccountingPeriodList, RdsAllAccountingPeriods
}

import java.time.LocalDate

trait AllAccountingPeriodsHelper {

  val rdsEmptyAllAccountingPeriods: RdsAllAccountingPeriods = RdsAllAccountingPeriods(List.empty)

  val emptyAllAccountingPeriods: AllAccountingPeriods = AllAccountingPeriods(List.empty)

  val allAccountingPeriods: AllAccountingPeriods = AllAccountingPeriods(
    List(
      AccountingPeriodList(
        accountingPeriod = BigDecimal(10),
        apEndDate = LocalDate.of(2025, 7, 24),
        taxChargePresent = true,
        penaltyTotal = BigDecimal(10)
      )
    )
  )

  val multipleAllAccountingPeriods: AllAccountingPeriods = AllAccountingPeriods(
    List(
      AccountingPeriodList(
        accountingPeriod = BigDecimal(20),
        apEndDate = LocalDate.of(2025, 8, 24),
        taxChargePresent = true,
        penaltyTotal = BigDecimal(20)
      ),
      AccountingPeriodList(
        accountingPeriod = BigDecimal(30),
        apEndDate = LocalDate.of(2025, 8, 24),
        taxChargePresent = false,
        penaltyTotal = BigDecimal(30)
      )
    )
  )

  val rdsAllAccountingPeriods: RdsAllAccountingPeriods = RdsAllAccountingPeriods(
    List(
      RdsAccountingPeriodList(
        accountingPeriod = BigDecimal(10),
        apEndDate = LocalDate.of(2025, 7, 24),
        taxChargePresent = "Y",
        penaltyTotal = BigDecimal(10)
      )
    )
  )

  val rdsMultipleAllAccountingPeriods: RdsAllAccountingPeriods = RdsAllAccountingPeriods(
    List(
      RdsAccountingPeriodList(
        accountingPeriod = BigDecimal(20),
        apEndDate = LocalDate.of(2025, 8, 24),
        taxChargePresent = "1",
        penaltyTotal = BigDecimal(20)
      ),
      RdsAccountingPeriodList(
        accountingPeriod = BigDecimal(30),
        apEndDate = LocalDate.of(2025, 8, 24),
        taxChargePresent = "N",
        penaltyTotal = BigDecimal(30)
      )
    )
  )

}
