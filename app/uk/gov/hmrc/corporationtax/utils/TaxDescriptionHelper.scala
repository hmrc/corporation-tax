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

import uk.gov.hmrc.corporationtax.models.TaxTransactionsItem

object TaxDescriptionHelper {

  val selfAssessment: Map[String, String] = Map(
    "A" -> "Amended assessment",
    "D" -> "Discovery assessment",
    "E" -> "HMRC determination",
    "F" -> "Further assessment",
    "J" -> "HMRC amended self assessment",
    "M" -> "Main assessment assessment",
    "R" -> "HMRC amended self assessment",
    "S" -> "Self assessment",
    "T" -> "Taxpayer amended self assessment",
    "Z" -> "Return charge"
  )

  val selfAssessmentClaim: Map[String, String] = Map(
    "A" -> "Amended assessment (claim)",
    "D" -> "Discovery assessment(claim)",
    "E" -> "HMRC determination(claim)",
    "F" -> "Further assessment(claim)",
    "J" -> "HMRC amended self assessment(claim)",
    "M" -> "Main assessment assessment(claim)",
    "R" -> "HMRC amended self assessment(claim)",
    "S" -> "Self assessment(claim)",
    "T" -> "Taxpayer amended self assessment(claim)",
    "Z" -> "Return charge(claim)"
  )

  private val claimIndicator: String = "2"

  def deriveTaxDescription(taxTransaction: TaxTransactionsItem): Option[String] = {
    val normalisedAssessmentType: String = taxTransaction.assessmentType.trim.toUpperCase
    val isClaim                          = taxTransaction.correctionClaimSignal.contains(claimIndicator)
    if (isClaim) selfAssessmentClaim.get(normalisedAssessmentType)
    else selfAssessment.get(normalisedAssessmentType)
  }

}
