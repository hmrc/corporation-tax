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

package uk.gov.hmrc.corporationtax.connectors.clientExchangeProxy

import play.api.Logging
import uk.gov.hmrc.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.AsynchronousProcessWaitTime
import uk.gov.hmrc.corporationtax.services.clientExchangeProxy.WaitTimeXmlMapper
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ClientExchangeProxyConnector @Inject (http: HttpClientV2, appConfig: AppConfig)(implicit ec: ExecutionContext)
    extends Logging {

  def initiateClientListDownload(
    credentialId: String
  )(implicit hc: HeaderCarrier): Future[AsynchronousProcessWaitTime] = {
    val url: URL = url"${appConfig.clientExchangeProxyFullUrl}/CT/$credentialId/agent/clientlist"
    http
      .get(url)
      .execute[HttpResponse]
      .flatMap { response =>
        if (is2xx(response.status)) {
          WaitTimeXmlMapper
            .parse(response.body)
            .fold(
              err => {
                logger.error(s"[ClientExchangeProxyConnector] parse error: $err; body: ${response.body}")
                Future.failed(
                  UpstreamErrorResponse(s"client-exchange-proxy parse error: $err", 502, 502)
                )
              },
              ok => Future.successful(ok)
            )
        } else {
          logger.error(s"[ClientExchangeProxyConnector] non-2xx ${response.status}; body: ${response.body}")
          Future.failed(
            UpstreamErrorResponse(
              s"client-exchange-proxy HTTP ${response.status}",
              response.status,
              response.status
            )
          )
        }
      }
  }

  private def is2xx(s: Int): Boolean = s >= 200 && s < 300

}
