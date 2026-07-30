package uk.gov.hmrc.corporationtax.utils

import scala.concurrent.Future

class TaxDescriptionHelper {

  def getTaxDescription(assessmentType: String, correctionClaim: String): Future[String] = {
    Future.successful {
      val messageType = correctionClaim match {
        case "0" => "standard"
        case "2" => "claim"
        case _ => "standard"
      }
      
      val result = s"taxDescription.assessment.${assessmentType.toLowerCase()}.$messageType";
      
      result
    }
  }

}
