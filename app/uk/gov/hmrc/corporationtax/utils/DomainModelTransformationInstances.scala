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

import uk.gov.hmrc.corporationtax.models.BusinessConstants.OASTransfer
import uk.gov.hmrc.corporationtax.models.*
import java.time.LocalDate

object DomainModelTransformationInstances {

  implicit val toTransformedReallocationFromAccPeriod
    : TransformToDomainModel[(RdsReallocationFromAccPeriodResponse, Long), ReallocationFromAccPeriod] =
    (reallocFromAcc: RdsReallocationFromAccPeriodResponse, taxPayerReference: Long) =>
      ReallocationFromAccPeriod(
        reallocFromAcc.reallocation.map { value =>
          // Determine TransactionType for each Reallocation(BF-F31)
          val transactionType: ReallocationTransactionType =
            reallocationFromTransactionType(value.destinationTaxPayerReference, taxPayerReference)
          ReallocationFromAccDetails(
            amount = value.amount.getOrElse(BigDecimal(0.00)),
            reallocationDate = value.reallocationDate,
            destinationApEndDate = value.destinationApEndDate,
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
            case _                                                  =>
              value
          }
        }
      )

  // Determine TransactionType for each ReallocationFromAccPeriod(BF-F31)
  implicit val toReallocationToAccPeriod: TransformToDomainModel[(Reallocations, Long), ReallocationToAccPeriod] =
    (reallocFromAcc: Reallocations, taxPayerReference: Long) =>
      ReallocationToAccPeriod(
        reallocFromAcc.reallocation.map { value =>
          // Determine TransactionType for each Reallocation(BF-F32)
          val transactionType: ReallocationTransactionType =
            reallocationToTransactionType(value.sourceTaxpayerReference, taxPayerReference, value.sourceApEndDate)
          ReallocationToAccPeriodRow(
            amount = value.amount,
            reallocationDate = value.reallocationDate,
            sourceApEndDate = value.sourceApEndDate,
            sourceTaxpayerReference = value.sourceTaxpayerReference,
            transactionType = transactionType
          )
        }
      )
  // Determine TransactionType for each ReallocationFromAccPeriod(BF-F31)
  private def reallocationFromTransactionType(
    destinationTaxPayerRef: String,
    requestedTaxPayerRef: Long
  ): ReallocationTransactionType = {
    val taxRef: String = requestedTaxPayerRef.toString
    if ((destinationTaxPayerRef == OASTransfer) || (destinationTaxPayerRef != taxRef))
      MiscellaneousTransfer
    else ReallocationTo
  }

  // Determine TransactionType for each ReallocationFromAccPeriod(BF-F32)
  private def reallocationToTransactionType(
    sourceTaxRef: String,
    requestedTaxPayerRef: Long,
    sourceApEndDate: Option[LocalDate]
  ): ReallocationTransactionType = {
    val taxRef: String = requestedTaxPayerRef.toString
    if (
      (sourceTaxRef == OASTransfer) || (sourceTaxRef != taxRef) || (sourceTaxRef == taxRef && sourceApEndDate.isEmpty)
    ) MiscellaneousTransfer
    else ReallocationFrom
  }
}
