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

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.gpa.GroupPaymentPeriodsInRangeConnector
import uk.gov.hmrc.corporationtax.models.gpa.{PeriodWithinRange, PeriodWithinRangeResponse}
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GroupPaymentPeriodsInRangeService @Inject() (
  connector: GroupPaymentPeriodsInRangeConnector
)(implicit ec: ExecutionContext) {

  private def transform(e: PeriodWithinRangeResponse): PeriodWithinRange =
    PeriodWithinRange(
      isPeriodWithinRange = CommonBooleanTransformation.toBool(e.isPeriodWithinRange)
    )

  def getGroupPaymentPeriodsInRange(gpaUTR: Long, nominatedCompanyUTR: Long, pPeriod: Int, pMonthRestriction: Int)(
    implicit hc: HeaderCarrier
  ): Future[PeriodWithinRange] = {
    logger.info(
      s"Calling repository for gpaUTR: $gpaUTR, nominatedCompanyUTR: $nominatedCompanyUTR, pPeriod: $pPeriod, pMonthRestriction: $pMonthRestriction"
    )
    connector
      .getGroupPaymentPeriodsInRange(gpaUTR, nominatedCompanyUTR, pPeriod, pMonthRestriction)
      .map(transform)
  }

}
