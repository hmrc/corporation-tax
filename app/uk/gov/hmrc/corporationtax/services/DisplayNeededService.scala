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

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.DisplayNeededConnector
import uk.gov.hmrc.corporationtax.models.{DisplayNeeded, DisplayNeededResponse}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DisplayNeededService @Inject() (
  connector: DisplayNeededConnector
)(implicit ec: ExecutionContext) {

  private def transform(e: DisplayNeededResponse): DisplayNeeded =
    DisplayNeeded(
      taxIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.taxIsDisplayNeededFlag),
      interestIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.interestIsDisplayNeededFlag),
      paymentIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.paymentIsDisplayNeededFlag),
      repayReallocIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.repayReallocIsDisplayNeededFlag)
    )

  def getDisplayNeeded(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[DisplayNeeded] = {
    logger.info(s"Calling repository with taxRef: $taxRef and accPeriod: $accPeriod")
    connector
      .getDisplayNeeded(taxRef, accPeriod)
      .map(transform)
      .map(record => record)
  }

}
