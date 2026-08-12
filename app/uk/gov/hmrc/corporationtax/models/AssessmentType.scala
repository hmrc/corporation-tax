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

package uk.gov.hmrc.corporationtax.models

sealed abstract class AssessmentType(val assessmentType: String, val standard: String, val correctionClaim: String)

case object AmendedAssessment extends AssessmentType("a", "Amended Assessment", "Amended assessment (claim)");
case object DiscoveryAssessment extends AssessmentType("d", "Discovery Assessment", "Discovery assessment (claim)");
case object HMRCDetermination extends AssessmentType("e", "HMRC determination", "HMRC determination (claim)");
case object FurtherAssessment extends AssessmentType("f", "Further assessment", "Further assessment (claim)");
case object HMRCAmendedSelfAssessment
    extends AssessmentType("j", "HMRC amended self assessment", "HMRC amended self assessment (claim)");
case object MainAssessment extends AssessmentType("m", "Main assessment", "Main assessment (claim)");
case object HMRCAmendedSelfAssessment2
    extends AssessmentType("r", "HMRC amended self assessment", "HMRC amended self assessment (claim)");
case object SelfAssessment extends AssessmentType("s", "Self assessment", "Self assessment (claim)");
case object TaxpayerAmendedSelfAssessment
    extends AssessmentType("t", "Taxpayer amended self assessment", "Taxpayer amended self assessment (claim)");
case object ReturnCharge extends AssessmentType("z", "Return charge", "Return charge (claim)");
case object NotRecognised extends AssessmentType("", "", "");
