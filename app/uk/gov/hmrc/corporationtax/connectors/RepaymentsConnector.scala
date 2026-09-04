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
import uk.gov.hmrc.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.models.Repayments
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RepaymentsConnector @Inject() (http: HttpClientV2, appConfig: AppConfig)(implicit
  ec: ExecutionContext
) extends Logging {

  def getRepayments(taxRef: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[Repayments] = {
    val url: URL = url"${appConfig.rdsDatacacheProxyFullUrl}/repayments/$taxRef/$accPeriod"

    http
      .get(url)
      .execute[Repayments]
      .recover { case ex: Throwable =>
        logger.error(
          s"[RepaymentsConnector][getRepayments]: $taxRef :: $accPeriod - ${ex.getMessage}"
        )
        throw new RuntimeException(ex.getMessage)
      }
  }
}
