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

package uk.gov.hmrc.corporationtax.utils

import uk.gov.hmrc.corporationtax.models.{AmendedAssessment, DiscoveryAssessment, HMRCDetermination, FurtherAssessment, HMRCAmendedSelfAssessment}
import uk.gov.hmrc.corporationtax.models.{HMRCAmendedSelfAssessment2, MainAssessment, SelfAssessment, TaxpayerAmendedSelfAssessment, ReturnCharge, NotRecognised}

import scala.concurrent.Future

class TaxDescriptionHelper {

  def getTaxDescription(assessmentType: String, correctionClaim: String): Future[String] =
    Future.successful {
      val assessment = assessmentType.toLowerCase match {
        case AmendedAssessment.assessmentType => AmendedAssessment
        case DiscoveryAssessment.assessmentType => DiscoveryAssessment
        case HMRCDetermination.assessmentType => HMRCDetermination
        case FurtherAssessment.assessmentType => FurtherAssessment
        case HMRCAmendedSelfAssessment.assessmentType => HMRCAmendedSelfAssessment
        case HMRCAmendedSelfAssessment2.assessmentType => HMRCAmendedSelfAssessment2
        case MainAssessment.assessmentType => MainAssessment
        case SelfAssessment.assessmentType => SelfAssessment
        case TaxpayerAmendedSelfAssessment.assessmentType => TaxpayerAmendedSelfAssessment
        case ReturnCharge.assessmentType => ReturnCharge
        case _ => NotRecognised
      }

      val taxDescription = correctionClaim match {
        case "0" => assessment.standard
        case "2" => assessment.correctionClaim
        case _ => assessment.standard
      }

      taxDescription
    }

}
