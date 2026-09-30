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

package uk.gov.hmrc.corporationtax.connectors.gpa

import play.api.Logging
import uk.gov.hmrc.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.models.gpa.PeriodWithinRangeResponse
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GroupPaymentPeriodsInRangeConnector @Inject() (http: HttpClientV2, appConfig: AppConfig)(implicit
  ec: ExecutionContext
) extends Logging {

  def getGroupPaymentPeriodsInRange(gpaUTR: Long, nominatedCompanyUTR: Long, pPeriod: Long, pMonthRestriction: Long)(
    implicit hc: HeaderCarrier
  ): Future[PeriodWithinRangeResponse] = {
    val url: URL =
      url"${appConfig.rdsDatacacheProxyFullUrl}/group-payment-periods-in-range/$gpaUTR/$nominatedCompanyUTR/$pPeriod/$pMonthRestriction"

    http
      .get(url)
      .execute[PeriodWithinRangeResponse]
      .recover { case e: Throwable =>
        logger.error(s"Error: $gpaUTR :: $nominatedCompanyUTR :: $pPeriod :: $pMonthRestriction- ${e.getMessage}")
        throw new RuntimeException(e.getMessage)
      }
  }

}
