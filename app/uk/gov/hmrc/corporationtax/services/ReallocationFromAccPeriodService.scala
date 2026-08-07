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
import uk.gov.hmrc.corporationtax.connectors.ReallocationFromAccPeriodRdsProxyConnector
import uk.gov.hmrc.corporationtax.models.TransformedReallocationFromAccPeriod
import uk.gov.hmrc.corporationtax.utils.AmountAdjustableInstances.*
import uk.gov.hmrc.corporationtax.utils.DomainModelTransformationInstances.*
import uk.gov.hmrc.corporationtax.utils.TransformToDomainModel.transform
import uk.gov.hmrc.corporationtax.utils.{TransformToDomainModel, applyAmountTransformToList}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReallocationFromAccPeriodService @Inject() (
  connector: ReallocationFromAccPeriodRdsProxyConnector
) extends Logging {

  def getReallocationFromAccPeriod(taxPayerReference: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[TransformedReallocationFromAccPeriod] = {
    logger.info(
      s"[ReallocationFromAccService][getReallocationFromAccPeriod] Calling ReallocationFromAccPeriodRdsProxyConnector: taxPayerReference:$taxPayerReference, accPeriod: $accPeriod"
    )
    connector.getReallocationFromAccPeriod(taxPayerReference, accPeriod).map { reallocationFromAccPeriod =>
      val reallocationFromAccAfterAmountTransformation = reallocationFromAccPeriod
        .copy(reallocation = applyAmountTransformToList(reallocationFromAccPeriod.reallocation))
      transform(reallocationFromAccAfterAmountTransformation)
    }

  }

}
