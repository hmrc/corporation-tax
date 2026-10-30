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

package uk.gov.hmrc.corporationtax.testdata.payments

import uk.gov.hmrc.corporationtax.models.payments.{RdsPostingSummaryByTypeResponse, RdsPostingSummaryByTypeRow, PostingSummaryByTypeResponse, PostingSummaryByTypeRow}

trait PostingSummaryByTypeHelper {

  val rdsPostingSummaryByTypeResponseEmpty: RdsPostingSummaryByTypeResponse = RdsPostingSummaryByTypeResponse(List.empty)

  val rdsPostingSummaryByTypeResponseMultiple: RdsPostingSummaryByTypeResponse = RdsPostingSummaryByTypeResponse(postingSummary =
    List(
      RdsPostingSummaryByTypeRow(
        accountingPeriod       = 1,
        settlementSequenceNo = Some(123), 
        postingAmountSum = -456789
      ),
      RdsPostingSummaryByTypeRow(
        accountingPeriod       = 2,
        settlementSequenceNo = Some(456),
        postingAmountSum = -789012
      ),
      RdsPostingSummaryByTypeRow(
        accountingPeriod       = 3,
        settlementSequenceNo = Some(789),
        postingAmountSum = 123456
      )
    )
  )

  val rdsPostingSummaryByTypeResponseNone: RdsPostingSummaryByTypeResponse = RdsPostingSummaryByTypeResponse(postingSummary =
    List(
      RdsPostingSummaryByTypeRow(
        accountingPeriod = 2,
        settlementSequenceNo = None,
        postingAmountSum = -789012
      )
    )
  )
}
