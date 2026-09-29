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

package uk.gov.hmrc.corporationtax.services.gpa

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.gpa.PaymentAllocationDetailsConnector
import uk.gov.hmrc.corporationtax.models.gpa.PaymentAllocationDetails
import uk.gov.hmrc.corporationtax.utils.AmountAdjustableInstances.{
  allocationDetailsAdjustable, paymentAllocationDetailsAdjustable
}
import uk.gov.hmrc.corporationtax.utils.applyAmountTransform
import uk.gov.hmrc.corporationtax.utils.applyAmountTransformToList
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationDetailsService @Inject() (paymentAllocationDetailsConnector: PaymentAllocationDetailsConnector)(
  implicit ec: ExecutionContext
) extends Logging {

  def getGPAPaymentAllocationDetail(
    gpaUtr: Long,
    gppContractVersion: Long,
    participatorUtr: Long,
    participatorAp: Long,
    startIndex: Int,
    count: Int
  )(implicit
    hc: HeaderCarrier
  ): Future[PaymentAllocationDetails] = {
    logger.info(
      s"Calling connector for gpaUtr: $gpaUtr, gppContractVersion: $gppContractVersion, participatorUtr: $participatorUtr, participatorAp: $participatorAp, startIndex: $startIndex and count: $count"
    )

    paymentAllocationDetailsConnector
      .getGPAPaymentAllocationDetail(gpaUtr, gppContractVersion, participatorUtr, participatorAp, startIndex, count)
      .map { rdsPaymentAllocationDetails =>
        val transPaymentAllocationDetails = applyAmountTransform(rdsPaymentAllocationDetails)
        val transAllocationDetails        = applyAmountTransformToList(rdsPaymentAllocationDetails.allocationDetails)
        val transApportionmentMethod      = transPaymentAllocationDetails.gppApportionmentMethod.map(_.trim)

        PaymentAllocationDetails(
          gppEndDate = transPaymentAllocationDetails.gppEndDate,
          gppTotalGroupPayment = transPaymentAllocationDetails.gppTotalGroupPayment.getOrElse(BigDecimal(0.00)),
          gppTotalGroupTax = transPaymentAllocationDetails.gppTotalGroupTax.getOrElse(BigDecimal(0.00)),
          gppStatus = transPaymentAllocationDetails.gppStatus,
          gppApportionmentMethod = transApportionmentMethod.getOrElse(""),
          participatingCompanyDesc = transPaymentAllocationDetails.participatingCompanyDesc,
          participatorAccPeriodEnd = transPaymentAllocationDetails.participatorAccPeriodEnd,
          participatorTaxCharge = transPaymentAllocationDetails.participatorTaxCharge,
          participatorAllocPayments = transPaymentAllocationDetails.participatorAllocPayments,
          gpaUtr = transPaymentAllocationDetails.gpaUtr,
          gppContractVersionOut = transPaymentAllocationDetails.gppContractVersionOut,
          allocationDetails = transAllocationDetails,
          totalNumOfRecords = transPaymentAllocationDetails.totalNumOfRecords
        )
      }
  }
}
