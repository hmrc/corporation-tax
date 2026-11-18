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
//import uk.gov.hmrc.corporationtax.connectors.ClientListDownloadStatusConnector
//import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus
import uk.gov.hmrc.corporationtax.models.manageClient.AllClients
import uk.gov.hmrc.corporationtax.utils.ClientListStubs

import javax.inject.Inject
//import scala.concurrent.Future

// connector: ClientListDownloadStatusConnector
class ClientListService @Inject() () extends Logging {

  def getClientList(credentialId: String, pStart: Int, pCount: Int, sortOrder: String): Option[AllClients] = {
//    val clientListDownloadStatusService: ClientListDownloadStatusService = new ClientListDownloadStatusService(
//      connector
//    )

//    val result: Int = clientListDownloadStatusService
//      .getClientListDownloadStatus(credentialId)
//      .map(response => response.status)

    val result: Int = 1

    // DIDN'T SUCCEED?
    if (result != 1) {
      return Some(null)
    }

    val lastIndex: Int = pStart + pCount

    val clientList = sortOrder.toLowerCase() match {
      case "asc"  => ClientListStubs
        .paginatedClients
        .clientList
        .sortBy(item => item.companyName)
        .slice(pStart, lastIndex)
      case "desc" => ClientListStubs
        .paginatedClients
        .clientList
        .sortBy(item => item.companyName).reverse
        .slice(pStart, lastIndex)
      case _ => ClientListStubs
        .paginatedClients
        .clientList
        .slice(pStart, lastIndex)
    }

    val filteredClients: AllClients = AllClients(
      clientCount = clientList.size,
      clientList = clientList,
      clientNameStartChars = ClientListStubs.paginatedClients.clientNameStartChars
    )

    Some(filteredClients)
  }
}
