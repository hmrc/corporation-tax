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

import uk.gov.hmrc.corporationtax.models.{GroupReferenceNumberLstItem, GroupSummaryDetails, GroupSummaryDetailsItem, GroupSummaryDetailsRecord, GroupSummaryDetailsResponse}

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

}
