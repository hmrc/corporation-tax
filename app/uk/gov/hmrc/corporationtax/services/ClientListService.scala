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
import uk.gov.hmrc.corporationtax.services.clientExchangeProxy.ClientExchangeProxyService
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientListDownloadStatusTransformed
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.services.manageClient.AllClientsService
import uk.gov.hmrc.corporationtax.models.manageClient.AllClients
import uk.gov.hmrc.corporationtax.models.manageClient.{ClientsError, FailedDownloadStatusError, FailedRetrieveAllClients}
import scala.util.control.NonFatal

import scala.concurrent.ExecutionContext

import javax.inject.Inject
import scala.concurrent.Future

class ClientListService @Inject() (
                                    clientExchangeProxyService: ClientExchangeProxyService,
                                    allClientsService: AllClientsService
)(implicit hc: HeaderCarrier, ec: ExecutionContext)
    extends Logging {

  def getClientList(
    irAgentId: String,
    credentialId: String,
    start: Int,
    count: Int,
    sort: Int,
    order: String
  ): Future[Either[ClientsError, AllClients]] = {

    clientExchangeProxyService.process(credentialId).flatMap {
      case ClientListDownloadStatusTransformed.Succeeded =>
        allClientsService
          .getAllClients(irAgentId, credentialId, start, count, sort, order)
          .map(Right(_))
          .recover { case NonFatal(e) =>
            Left(FailedRetrieveAllClients("Error message"))
          }

      case _ =>
        Future.successful(Left(FailedDownloadStatusError("Error message")))
    }
  }
}
