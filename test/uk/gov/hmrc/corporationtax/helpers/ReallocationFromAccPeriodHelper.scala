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
  RdsReallocationFromAccDetails, RdsReallocationFromAccPeriodResponse, TransactionTypesOfGetReallocationFromAcc,
  TransformedReallocationFromAccDetails, TransformedReallocationFromAccPeriod
}

import java.time.LocalDate

trait ReallocationFromAccPeriodHelper {

  val emptyString: String = ""

  val emptyListReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse            = RdsReallocationFromAccPeriodResponse(
    List.empty
  )
  val emptyTransformedListReallocationFromAccPeriod: TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(List.empty)

  def rdsReallocationFromAccPeriodResponse(
    amount: Option[BigDecimal] = None,
    destinationApEndDate: Option[LocalDate] = None,
    destinationTaxPayerReference: String
  ): RdsReallocationFromAccPeriodResponse =
    RdsReallocationFromAccPeriodResponse(
      List(
        RdsReallocationFromAccDetails(
          amount,
          LocalDate.of(2026, 12, 2),
          destinationApEndDate,
          destinationTaxPayerReference
        ),
        RdsReallocationFromAccDetails(
          amount,
          LocalDate.of(2026, 12, 2),
          destinationApEndDate,
          destinationTaxPayerReference
        ),
        RdsReallocationFromAccDetails(
          amount,
          LocalDate.of(2026, 12, 2),
          destinationApEndDate,
          destinationTaxPayerReference
        )
      )
    )

  def transformedReallocationFromAccPeriod(
    amount: BigDecimal,
    destinationApEndDate: String,
    destinationTaxPayerReference: String,
    transactionType: TransactionTypesOfGetReallocationFromAcc
  ): TransformedReallocationFromAccPeriod =
    TransformedReallocationFromAccPeriod(
      List(
        TransformedReallocationFromAccDetails(
          amount,
          LocalDate.of(2026, 12, 2),
          destinationApEndDate,
          destinationTaxPayerReference,
          transactionType
        ),
        TransformedReallocationFromAccDetails(
          amount,
          LocalDate.of(2026, 12, 2),
          destinationApEndDate,
          destinationTaxPayerReference,
          transactionType
        ),
        TransformedReallocationFromAccDetails(
          amount,
          LocalDate.of(2026, 12, 2),
          destinationApEndDate,
          destinationTaxPayerReference,
          transactionType
        )
      )
    )

}
