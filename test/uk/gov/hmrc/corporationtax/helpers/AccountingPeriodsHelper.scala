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
        accountingPeriod = BigDecimal(202501),
        apStartDate = LocalDate.of(2025, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
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
      RdsAccountingPeriodsRowResponse(
        accountingPeriod = BigDecimal(20501),
        apStartDate = LocalDate.of(2025, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
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
      RdsAccountingPeriodsRowResponse(
        accountingPeriod = BigDecimal(20201),
        apStartDate = LocalDate.of(2019, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
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
        apStartDate = LocalDate.of(2025, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
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
        apStartDate = LocalDate.of(2025, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
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
        apStartDate = LocalDate.of(2019, 1, 1),
        apEndDate = LocalDate.of(2025, 12, 31),
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
