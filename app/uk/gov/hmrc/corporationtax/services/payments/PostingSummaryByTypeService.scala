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

package uk.gov.hmrc.corporationtax.services.payments

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.payments.PostingSummaryByTypeConnector
import uk.gov.hmrc.corporationtax.models.payments.{
  PostingSummaryByTypeResponse, PostingSummaryByTypeRow, RdsPostingSummaryByTypeResponse
}
import uk.gov.hmrc.corporationtax.utils.EmptyAndZeroConstants.zeroValue
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostingSummaryByTypeService @Inject() (
  connector: PostingSummaryByTypeConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getPostingSummaryByType(taxRef: Long, postingType: String)(implicit
    hc: HeaderCarrier
  ): Future[PostingSummaryByTypeResponse] = {
    logger.info(s"Calling Connector to getPostingSummaryByType: $taxRef :: $postingType")
    connector
      .getPostingSummaryByType(taxRef, postingType)
      .map { rdsPostingSummaryByTypeResponse =>
        toPostingSummaryByTypeResponse(rdsPostingSummaryByTypeResponse)
      }

  }

  private def toPostingSummaryByTypeResponse(
    rdsPostingSummaryByTypeResponse: RdsPostingSummaryByTypeResponse
  ): PostingSummaryByTypeResponse =
    PostingSummaryByTypeResponse(
      postingSummary = rdsPostingSummaryByTypeResponse.postingSummary.map { value =>
        PostingSummaryByTypeRow(
          accountingPeriod = value.accountingPeriod,
          settlementSequenceNo = value.settlementSequenceNo.getOrElse(BigDecimal(zeroValue)),
          postingAmountSum = value.postingAmountSum
        )
      }
    )

}
