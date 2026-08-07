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
import uk.gov.hmrc.corporationtax.models.NonNullPayRepayReallocations
import uk.gov.hmrc.corporationtax.connectors.PayRepayReallocationConnector
import uk.gov.hmrc.corporationtax.utils.applyAmountTransform
import uk.gov.hmrc.corporationtax.utils.AmountAdjustableInstances.*
import uk.gov.hmrc.corporationtax.utils.PayRepayReallocationTransformInstances.*
import uk.gov.hmrc.corporationtax.utils.TransformToDomainModel.transform
import uk.gov.hmrc.corporationtax.utils.TransformToDomainModel
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PayRepayReallocationService @Inject() (payRepayReallocationConnector: PayRepayReallocationConnector)(implicit
  ec: ExecutionContext
) extends Logging {

  def getTotalAmounts(taxRef: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[NonNullPayRepayReallocations] = {
    logger.info(s"Calling repository for taxRef: $taxRef and accPeriod: $accPeriod")

    payRepayReallocationConnector.getTotalAmounts(taxRef, accPeriod).map { payRepayReallocation =>
      val amount = applyAmountTransform(payRepayReallocation)
      transform(amount)
    }
  }

}
