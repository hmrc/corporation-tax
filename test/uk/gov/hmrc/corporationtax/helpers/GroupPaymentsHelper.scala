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

import uk.gov.hmrc.corporationtax.models.{GpaPaymentsDetails, GpaPaymentsDetailsResponse, GpaPaymentsItem, GpaPaymentsRecord, GroupReferenceNumberLstItem, GroupSummaryDetails, GroupSummaryDetailsItem, GroupSummaryDetailsRecord, GroupSummaryDetailsResponse}

import java.time.LocalDate

trait GroupPaymentsHelper {
  val groupSummaryDetItemOne = GroupSummaryDetailsItem(
    contractEndDate = LocalDate.of(2026, 1, 7),
    groupTaxCharge = Some(BigDecimal(11.01)),
    groupPayment = Some(BigDecimal(13.02)),
    groupPaymentRecordCount = 2,
    contractStatus = "ACTIVE",
    contractVersion = 2
  )

  val groupSummaryDetRecOne = GroupSummaryDetailsRecord(
    contractEndDate = LocalDate.of(2026, 1, 7),
    groupTaxCharge = BigDecimal(-11.01),
    groupPayment = BigDecimal(-13.02),
    groupPaymentRecordCount = 2,
    contractStatus = "ACTIVE",
    contractVersion = 2
  )

  val groupPaymentDetails = GroupSummaryDetails(
    gpaGrpSummaryDetails = List(
      groupSummaryDetItemOne
    ),
    gpaReferenceNumberLst = List(
      GroupReferenceNumberLstItem(112)
    ),
    nominatedCompanyName = "Some company name"
  )

  val groupPaymentDetailsResponse = GroupSummaryDetailsResponse(
    gpaGrpSummaryDetails = List(
      groupSummaryDetRecOne
    ),
    gpaReferenceNumberLst = List(
      GroupReferenceNumberLstItem(112)
    ),
    nominatedCompanyName = "Some company name"
  )

  val groupPaymentDetailsEmpty = GroupSummaryDetails(
    gpaGrpSummaryDetails = List.empty,
    gpaReferenceNumberLst = List.empty,
    nominatedCompanyName = "CompanyName"
  )


  // PaymentDetails
  val defaultPaymentDetails = GpaPaymentsDetails(
    gpaPayments = List(
      GpaPaymentsItem(
        displayDate = Some(LocalDate.of(2008, 4, 14)),
        total = Some(BigDecimal(6250)),
        tablename = Some("Payslip"),
        targetTaxpayerReference = None,
        paymentType = Some("BGP"),
        repaymentType = None,
        targetApNo = Some(0),
        targetApEndDate = None,
        contractEndDate = None,
        participatorCount = Some(0L)
      )
    ),
    totalNumOfRecords = Some(1L),
    gppEndDate = Some(LocalDate.of(2007, 12, 31)),
    gppTotalGroupPayment = Some(BigDecimal(25000)),
    gppTotalGroupTax = Some(BigDecimal(-250000)),
    gppStatus = "L",
    gppCni = None,
    gppApportionmentMethod = "METHOD"
  )

  val defaultPaymentDetailsResponse = GpaPaymentsDetailsResponse(
    gpaPayments = List(
      GpaPaymentsRecord(
        displayDate = Some(LocalDate.of(2008, 4, 14)),
        total = BigDecimal(6250),
        tablename = Some("Payslip"),
        targetTaxpayerReference = None,
        paymentType = Some("BGP"),
        repaymentType = None,
        targetApNo = Some(0),
        targetApEndDate = None,
        contractEndDate = None,
        participatorPresent = Some(false)
      )
    ),
    totalNumOfRecords = Some(1L),
    gppEndDate = Some(LocalDate.of(2007, 12, 31)),
    gppTotalGroupPayment = BigDecimal(25000),
    gppTotalGroupTax = BigDecimal(-250000),
    gppStatus = "L",
    gppCni = None,
    gppApportionmentMethod = "METHOD"
  )
}
