package uk.gov.hmrc.corporationtax.utils

import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus

object ClientListStatusHelper {

  def hasSucceeded(status: Int): Boolean = {
    val result: Boolean = status match {
      case -1 => false // initiate download
      case 0 => false // in progress
      case 1 => true // succeeded
      case 2 => false // failed
      case _ => false // default
    }

    result
  }

}
