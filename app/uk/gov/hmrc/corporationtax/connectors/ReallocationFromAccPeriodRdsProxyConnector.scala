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

package uk.gov.hmrc.corporationtax.connectors

import play.api.Logging
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.models.RdsReallocationFromAccPeriodResponse
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReallocationFromAccPeriodRdsProxyConnector @Inject() (http: HttpClientV2, appConfig: AppConfig)(implicit
  ec: ExecutionContext
) extends Logging {

  def getReallocationFromAccPeriod(taxPayerReference: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[RdsReallocationFromAccPeriodResponse] = {
    val url: URL = url"${appConfig.rdsDatacacheProxyEndpoint}/reallocation-from-accounting-period/$taxPayerReference/$accPeriod"
    http
      .get(url)
      .execute[RdsReallocationFromAccPeriodResponse]
      .recover {
        case e: UpstreamErrorResponse =>
          logger.error(
            s"[ReallocationFromAccPeriodRdsProxyConnector][getReallocationFromAccPeriod]: Upstream error - ${e.getMessage}"
          )
          throw e
        case e: Throwable             =>
          logger.error(
            s"[ReallocationFromAccPeriodRdsProxyConnector][getReallocationFromAccPeriod]: ${e.getMessage}"
          )
          throw new RuntimeException(e.getMessage)
      }
  }

}
