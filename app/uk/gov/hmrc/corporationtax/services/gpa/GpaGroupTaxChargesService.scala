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
import uk.gov.hmrc.corporationtax.connectors.gpa.GpaGroupTaxChargesRdsProxyConnector
import uk.gov.hmrc.corporationtax.models.gpa.{GpaGroupTaxCharges, ParticipatorDetails}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.corporationtax.utils.EmptyAndZeroConstants.{emptyString, zeroValue}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GpaGroupTaxChargesService @Inject() (
  connector: GpaGroupTaxChargesRdsProxyConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getGpaGroupTaxCharges(pGpaUtr: Long, pGppContractVersion: Int, pStartIndex: Int, pCount: Int)(implicit
    hc: HeaderCarrier
  ): Future[GpaGroupTaxCharges] = {
    logger.info(
      s"Retrieving GpaGroupTaxCharges: pGpaUtr:$pGpaUtr, pGppContractVersion: $pGppContractVersion"
    )
    connector.getGpaGroupTaxCharges(pGpaUtr, pGppContractVersion, pStartIndex, pCount).map { rdsGpaGroupTaxCharges =>
      GpaGroupTaxCharges(
        pGppEndDate = rdsGpaGroupTaxCharges.pGppEndDate,
        pGppTotalGroupPayment = AmountTransformation(rdsGpaGroupTaxCharges.pGppTotalGroupPayment),
        pGppTotalGroupTax = AmountTransformation(rdsGpaGroupTaxCharges.pGppTotalGroupTax),
        pGppStatus = rdsGpaGroupTaxCharges.pGppStatus.map(_.trim).getOrElse(emptyString),
        pGppCni = rdsGpaGroupTaxCharges.pGppCni,
        pGppApportionmentMethod = rdsGpaGroupTaxCharges.pGppApportionmentMethod.map(_.trim).getOrElse(emptyString),
        pGpaUtr2 = rdsGpaGroupTaxCharges.pGpaUtr2,
        pTotalNumOfRecords = rdsGpaGroupTaxCharges.pTotalNumOfRecords.getOrElse(zeroValue),
        pGroupPaymentRecordCount = rdsGpaGroupTaxCharges.pGroupPaymentRecordCount.getOrElse(zeroValue),
        pCurGroupTaxCharges = rdsGpaGroupTaxCharges.pCurGroupTaxCharges.map { value =>
          ParticipatorDetails(
            participatorName = value.participatorName.trim,
            participatorReference = value.participatorReference,
            participatorApEndDate = value.participatorApEndDate,
            participatorTaxCharge = AmountTransformation(value.participatorTaxCharge),
            participatorTaxChargePrsnt = value.participatorTaxChargePrsnt.trim,
            participatorAccountingPeriod = value.participatorAccountingPeriod,
            contractVersion = value.contractVersion,
            allocatedPayment = AmountTransformation(value.allocatedPayment),
            allocatedPaymentRecordCount = value.allocatedPaymentRecordCount
          )

        }
      )

    }

  }

}
