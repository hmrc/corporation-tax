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

import org.apache.pekko.actor.ActorSystem
import play.api.Logging
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.connectors.clientExchangeProxy.ClientExchangeProxyConnector
import uk.gov.hmrc.corporationtax.services.clientExchangeProxy.ClientExchangeProxyService
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientListDownloadStatusTransformed
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.connectors.manageClient.AllClientsConnector
import uk.gov.hmrc.corporationtax.services.manageClient.AllClientsService
import uk.gov.hmrc.corporationtax.models.manageClient.AllClients
import uk.gov.hmrc.corporationtax.connectors.ClientListDownloadStatusConnector
import uk.gov.hmrc.corporationtax.services.ClientListDownloadStatusService

import scala.concurrent.ExecutionContext
import uk.gov.hmrc.corporationtax.utils.ClientListStatusHelper

import javax.inject.Inject
import scala.concurrent.Future

class ClientListService @Inject() (
  clientListDownloadStatusConnector: ClientListDownloadStatusConnector,
  clientExchangeProxyConnector: ClientExchangeProxyConnector,
  allClientsConnector: AllClientsConnector,
  appConfig: AppConfig,
  actorSystem: ActorSystem
)(implicit hc: HeaderCarrier, ec: ExecutionContext)
    extends Logging {

  def getClientList(
    irAgentId: String,
    credentialId: String,
    start: Int,
    count: Int,
    sort: Int,
    order: String
  ): Future[AllClients] = {
    val clientListDownloadStatusService: ClientListDownloadStatusService = new ClientListDownloadStatusService(
      clientListDownloadStatusConnector
    )

    val clientExchangeProxyService: ClientExchangeProxyService = new ClientExchangeProxyService(
      clientExchangeProxyConnector,
      clientListDownloadStatusService,
      appConfig,
      actorSystem
    )

    val downloadStatusTransformed: Future[ClientListDownloadStatusTransformed] =
      clientExchangeProxyService.getStatus(credentialId)

    val status : Int = downloadStatusTransformed.map(item => item.asInt)

    // DIDN'T SUCCEED?
    if (!ClientListStatusHelper.hasSucceeded(status)) {
      return null
    }

    val allClientsService: AllClientsService = new AllClientsService(allClientsConnector)

    allClientsService.getAllClients(irAgentId, credentialId, start, count, sort, order)
  }
}
