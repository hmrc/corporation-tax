package uk.gov.hmrc.corporationtax.utils

import play.api.i18n.Messages

import scala.concurrent.Future

class TaxDescriptionHelper {

  def getTaxDescription(assessmentType: String, correctionClaim: String)(implicit messages: Messages): Future[String] = {
    Future.successful {
      val messageType = correctionClaim match {
        case "0" => "standard"
        case "2" => "claim"
        case _ => "standard"
      }
      
      val result = messages(s"taxDescription.assessment.${assessmentType.toLowerCase()}.$messageType");
      
      result
    }
  }

}
