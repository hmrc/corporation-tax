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

package uk.gov.hmrc.corporationtax.helpers

import uk.gov.hmrc.corporationtax.models.{
  AccountingPeriods, AccountingPeriodsRowResponse, RdsAccountingPeriod, RdsAccountingPeriodsRowResponse
}

import java.time.LocalDate

trait AccountingPeriodsHelper {

  val emptyRdsAccountingPeriods: RdsAccountingPeriod = RdsAccountingPeriod(accountingPeriods = List.empty)
  val emptyAccountingPeriods: AccountingPeriods      = AccountingPeriods(accountingPeriods = List.empty)
  val rdsAccountingPeriodWithNoAccPeriod: RdsAccountingPeriod = RdsAccountingPeriod(accountingPeriods =
    List(
      RdsAccountingPeriodsRowResponse(
        accountingPeriod = None,
        apStartDate = Some(LocalDate.of(2025, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = Some("Open"),
        taxChargePresent = Some("Y"),
        clericalIntSig = Some("N"),
        creditDebitInterestInd = Some("Y"),
        taxTotal = Some(BigDecimal(-12.45)),
        interestTotal = Some(BigDecimal(-10.45)),
        penaltyTotal = Some(BigDecimal(12.45)),
        payslipTotal = Some(BigDecimal(-12334.45)),
        repayReallocTotal = Some(BigDecimal(-12.45343)),
        adjustmentTotal = Some(BigDecimal(-1253.45))
      )
    )
  )
  def rdsAccountingPeriod(
    taxTotal: Option[BigDecimal] = None,
    interestTotal: Option[BigDecimal] = None,
    penaltyTotal: Option[BigDecimal] = None,
    payslipTotal: Option[BigDecimal] = None,
    repayReallocTotal: Option[BigDecimal] = None,
    adjustmentTotal: Option[BigDecimal] = None,
    taxChargePresent: Option[String] = None,
    clericalIntSig: Option[String] = None,
    creditDebitInterestInd: Option[String] = None
  ): RdsAccountingPeriod = RdsAccountingPeriod(accountingPeriods =
    List(
      RdsAccountingPeriodsRowResponse(
        accountingPeriod = Some(BigDecimal(202501)),
        apStartDate = Some(LocalDate.of(2025, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = Some("Open"),
        taxChargePresent = taxChargePresent,
        clericalIntSig = clericalIntSig,
        creditDebitInterestInd = creditDebitInterestInd,
        taxTotal = taxTotal,
        interestTotal = interestTotal,
        penaltyTotal = penaltyTotal,
        payslipTotal = payslipTotal,
        repayReallocTotal = repayReallocTotal,
        adjustmentTotal = adjustmentTotal
      ),
      RdsAccountingPeriodsRowResponse(
        accountingPeriod = Some(BigDecimal(20501)),
        apStartDate = Some(LocalDate.of(2025, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = Some("Closed"),
        taxChargePresent = taxChargePresent,
        clericalIntSig = clericalIntSig,
        creditDebitInterestInd = creditDebitInterestInd,
        taxTotal = taxTotal,
        interestTotal = interestTotal,
        penaltyTotal = penaltyTotal,
        payslipTotal = payslipTotal,
        repayReallocTotal = repayReallocTotal,
        adjustmentTotal = adjustmentTotal
      ),
      RdsAccountingPeriodsRowResponse(
        accountingPeriod = Some(BigDecimal(20201)),
        apStartDate = Some(LocalDate.of(2019, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = Some("Open"),
        taxChargePresent = taxChargePresent,
        clericalIntSig = clericalIntSig,
        creditDebitInterestInd = creditDebitInterestInd,
        taxTotal = taxTotal,
        interestTotal = interestTotal,
        penaltyTotal = penaltyTotal,
        payslipTotal = payslipTotal,
        repayReallocTotal = repayReallocTotal,
        adjustmentTotal = adjustmentTotal
      )
    )
  )

  def accountingPeriods(
    taxTotal: BigDecimal,
    interestTotal: BigDecimal,
    penaltyTotal: BigDecimal,
    payslipTotal: BigDecimal,
    repayReallocTotal: BigDecimal,
    adjustmentTotal: BigDecimal,
    taxChargePresent: Boolean,
    clericalIntSig: Boolean,
    creditDebitInterestInd: Boolean
  ): AccountingPeriods = AccountingPeriods(accountingPeriods =
    List(
      AccountingPeriodsRowResponse(
        accountingPeriod = BigDecimal(202501),
        apStartDate = Some(LocalDate.of(2025, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = "Open",
        taxChargePresent = taxChargePresent,
        clericalIntSig = clericalIntSig,
        creditDebitInterestInd = creditDebitInterestInd,
        taxTotal = taxTotal,
        interestTotal = interestTotal,
        penaltyTotal = penaltyTotal,
        payslipTotal = payslipTotal,
        repayReallocTotal = repayReallocTotal,
        adjustmentTotal = adjustmentTotal
      ),
      AccountingPeriodsRowResponse(
        accountingPeriod = BigDecimal(20501),
        apStartDate = Some(LocalDate.of(2025, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = "Closed",
        taxChargePresent = taxChargePresent,
        clericalIntSig = clericalIntSig,
        creditDebitInterestInd = creditDebitInterestInd,
        taxTotal = taxTotal,
        interestTotal = interestTotal,
        penaltyTotal = penaltyTotal,
        payslipTotal = payslipTotal,
        repayReallocTotal = repayReallocTotal,
        adjustmentTotal = adjustmentTotal
      ),
      AccountingPeriodsRowResponse(
        accountingPeriod = BigDecimal(20201),
        apStartDate = Some(LocalDate.of(2019, 1, 1)),
        apEndDate = Some(LocalDate.of(2025, 12, 31)),
        apStatus = "Open",
        taxChargePresent = taxChargePresent,
        clericalIntSig = clericalIntSig,
        creditDebitInterestInd = creditDebitInterestInd,
        taxTotal = taxTotal,
        interestTotal = interestTotal,
        penaltyTotal = penaltyTotal,
        payslipTotal = payslipTotal,
        repayReallocTotal = repayReallocTotal,
        adjustmentTotal = adjustmentTotal
      )
    )
  )

}
