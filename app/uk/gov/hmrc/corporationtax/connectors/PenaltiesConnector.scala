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
import uk.gov.hmrc.corporationtax.models.Penalties
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PenaltiesConnector @Inject()(http: HttpClientV2,
                                   config: ServicesConfig)(implicit ec: ExecutionContext)
  extends Logging {

  private val stubEnabled: Boolean = config.getBoolean("features.corporation-tax-stub-enabled")

  private val dataProxyPath = {
    if (stubEnabled) {
      config.baseUrl("corporation-tax-stub")
    } else {
      config.baseUrl("rds-datacache-proxy") + "/rds-datacache-proxy"
    }
  }

  def getPenaltyTransactionList(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[Penalties] = {
    val url: URL = url"$dataProxyPath/corporation-tax/penalty-transactions/$taxRef/$accPeriod"
    http
      .get(url)
      .execute[Penalties]
      .recover { case e: Throwable =>
        logger.error(s"[PenaltiesConnector][getPenaltyTransactionList]: $taxRef :: $accPeriod - ${e.getMessage}")
        throw new RuntimeException(e.getMessage)
      }
  }

}
