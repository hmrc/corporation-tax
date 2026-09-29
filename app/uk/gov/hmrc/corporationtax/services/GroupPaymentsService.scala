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

package uk.gov.hmrc.corporationtax.services

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.GroupPaymentsConnector
import uk.gov.hmrc.corporationtax.models.{GpaPaymentsDetails, GpaPaymentsDetailsResponse, GroupSummaryDetails, GroupSummaryDetailsRecord, GroupSummaryDetailsResponse}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GroupPaymentsService @Inject()(connector: GroupPaymentsConnector)(implicit ec: ExecutionContext)
  extends Logging {

  private def transformSummaryDetails(rec: GroupSummaryDetails): GroupSummaryDetailsResponse = {
    val detailRecs = rec.gpaGrpSummaryDetails
      .map(rec =>
        GroupSummaryDetailsRecord(
          contractEndDate = rec.contractEndDate,
          groupTaxCharge = AmountTransformation(rec.groupTaxCharge),
          groupPayment = AmountTransformation(rec.groupPayment),
          groupPaymentRecordCount = rec.groupPaymentRecordCount,
          contractStatus = rec.contractStatus,
          contractVersion = rec.contractVersion
        )
      )
    GroupSummaryDetailsResponse(
      gpaGrpSummaryDetails = detailRecs,
      gpaReferenceNumberLst = rec.gpaReferenceNumberLst,
      nominatedCompanyName = rec.nominatedCompanyName
    )
  }


  private def transformPaymentDetails(rec: GpaPaymentsDetails): GpaPaymentsDetailsResponse = ???

  def getGroupSummary(gpaUTR: Long, nomCompanyUTR: Long)(implicit
                                                         hc: HeaderCarrier
  ): Future[Option[GroupSummaryDetailsResponse]] = {
    logger.info(s"taxRef: $gpaUTR and accPeriod: $nomCompanyUTR")
    {
      for {
        rec <- connector
          .getGroupSummary(gpaUTR, nomCompanyUTR)
      } yield rec.map(transformSummaryDetails)
    }.recover { case e: Throwable =>
      logger.error(s"$gpaUTR :: $nomCompanyUTR - ${e.getMessage}")
      throw new RuntimeException(e.getMessage)
    }
  }

  def getPaymentDetails(gpaUTR: Long,
                        contractVersion: Int, startIndex: Int, count: Int)(implicit
                                                                           hc: HeaderCarrier
                       ): Future[Option[GpaPaymentsDetailsResponse]] = {
    logger.info(s"taxRef: $gpaUTR and contractVersion: $contractVersion " +
      s"and startIndex: $startIndex and count: $count")
    {
      for {
        rec <- connector
          .getPaymentDetails(gpaUTR, contractVersion: Int, startIndex: Int, count: Int)
      } yield rec.map(transformPaymentDetails)
    }.recover { case e: Throwable =>
      logger.error(s"$gpaUTR :: $contractVersion :: $startIndex :: $count - ${e.getMessage}")
      throw new RuntimeException(e.getMessage)
    }

  }
  
}
