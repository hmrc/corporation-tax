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
import uk.gov.hmrc.corporationtax.connectors.InterestAccuralListConnector
import uk.gov.hmrc.corporationtax.models.InterestAccuralList
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.utils.applyAmountTransformToList
import uk.gov.hmrc.corporationtax.utils.AmountAdjustableInstances.*

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InterestAccuralListService @Inject() (
  connector: InterestAccuralListConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getInterestAccuralList(taxRef: Long, accPeriod: Long, interestType: String)(implicit
    hc: HeaderCarrier
  ): Future[InterestAccuralList] = {
    logger.info(
      s"[InterestAccuralListService][getInterestAccuralList] Calling InterestAccuralListConnector: taxRef: $taxRef, accPeriod: $accPeriod, interestType: $interestType"
    )
    // connector.getInterestAccuralList(taxRef, accPeriod, interestType)
    connector.getInterestAccuralList(taxRef, accPeriod, interestType).map { interestAccurals =>
      interestAccurals
        .copy(interestAccuralList = applyAmountTransformToList(interestAccurals.interestAccuralList))
    }
  }

}
