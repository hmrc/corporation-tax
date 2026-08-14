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

package uk.gov.hmrc.corporationtax.utils

import uk.gov.hmrc.corporationtax.models.BusinessConstants.destinationTaxRefOASTransfer
import uk.gov.hmrc.corporationtax.models.{
  MiscellaneousTransfer, RdsReallocationFromAccPeriodResponse, ReallocationTo, Repayments,
  TransactionTypesOfGetReallocationFromAcc, TransformedReallocationFromAccDetails, TransformedReallocationFromAccPeriod
}

object DomainModelTransformationInstances {

  implicit val toTransformedReallocationFromAccPeriod
    : TransformToDomainModel[(RdsReallocationFromAccPeriodResponse, Long), TransformedReallocationFromAccPeriod] =
    (reallocFromAcc: RdsReallocationFromAccPeriodResponse, taxPayerReference: Long) =>
      TransformedReallocationFromAccPeriod(
        reallocFromAcc.reallocation.map { value =>
          // Determine TransactionType for each Reallocation(BF-F31)
          val transactionType: TransactionTypesOfGetReallocationFromAcc =
            determineTransactionType(value.destinationTaxPayerReference, taxPayerReference)
          TransformedReallocationFromAccDetails(
            amount = value.amount.getOrElse(BigDecimal(0.00)),
            reallocationDate = value.reallocationDate,
            destinationApEndDate = value.destinationApEndDate
              .map(_.toString)
              .getOrElse(""), // converting to string and assigning empty string if it's null
            destinationTaxPayerReference = value.destinationTaxPayerReference,
            transactionType = transactionType
          )
        }
      )

  implicit val toTransformedRepayments: TransformToDomainModel[Repayments, Repayments] =
    (repayments: Repayments) =>
      Repayments(
        repayments.repayments.map { value =>
          value.amount match {
            case Some(amount) if value.amount > Some(BigDecimal(0)) =>
              value.copy(repaymentType = "CRT") // Cancelled Repayment
            case _ =>
              value
          }
        }
      )

  // Determine TransactionType for each ReallocationFromAccPeriod(BF-F31)
  private def determineTransactionType(
    destinationTaxPayerRef: String,
    requestedTaxPayerRef: Long
  ): TransactionTypesOfGetReallocationFromAcc = {
    val taxRef: String = requestedTaxPayerRef.toString
    if ((destinationTaxPayerRef == destinationTaxRefOASTransfer) || (destinationTaxPayerRef != taxRef))
      MiscellaneousTransfer
    else ReallocationTo
  }
}
