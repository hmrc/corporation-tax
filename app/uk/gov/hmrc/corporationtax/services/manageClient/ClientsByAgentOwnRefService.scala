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

package uk.gov.hmrc.corporationtax.services.manageClient

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.manageClient.ClientsByAgentOwnRefConnector
import uk.gov.hmrc.corporationtax.models.manageClient.AllClients
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class ClientsByAgentOwnRefService @Inject()(
  connector: ClientsByAgentOwnRefConnector
) extends Logging {

  def getClientsByAgentOwnRef(irAgentReference: String, 
                              credentialId: String,
                              clientId: String,
                              start: Int,
                              count: Int,
                              sort: Int,
                              order: String)(implicit
    hc: HeaderCarrier
  ): Future[AllClients] = {
    logger.info(
      s"Calling connector for irAgentId: $irAgentReference, credentialId: $credentialId, clientId: $clientId, start: $start, count: $count, sort: $sort, order: $order"
    )
    connector
      .getClientsByAgentOwnRef(irAgentReference, credentialId, clientId, start, count, sort, order)
  }

}
