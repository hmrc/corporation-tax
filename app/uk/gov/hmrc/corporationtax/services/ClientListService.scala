package uk.gov.hmrc.corporationtax.services


import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.ClientListDownloadStatusConnector
import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class ClientListService @Inject() (connector: ClientListDownloadStatusConnector) extends Logging {

  def getClientList(credentialId: String, pStart: Int, pCount: Int, sortOrder: String)(implicit hc: HeaderCarrier): Future[ClientDownloadStatus] = {
    val clientListDownloadStatusService: ClientListDownloadStatusService = new ClientListDownloadStatusService(connector)
    
    val response = clientListDownloadStatusService.getClientListDownloadStatus(credentialId).value
    
    if (response != ClientDownloadStatus) {
      
    }
    
  }
}
