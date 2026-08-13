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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.corporationtax.models.TaxTransactionsItem

import java.time.LocalDate

class TaxDescriptionHelperSpec extends AnyWordSpec with Matchers with ScalaFutures {

  private val taxDescriptionHelper = new TaxDescriptionHelper();

  def createMockTaxTransaction(assessmentType: String, correctionClaimSignal: Option[String]): TaxTransactionsItem =
    TaxTransactionsItem(
      currentAmount = BigDecimal(123.44),
      assessmentType = assessmentType,
      taxDate = LocalDate.of(2026, 1, 1),
      correctionClaimSignal = correctionClaimSignal
    )

  s"Assessment Type is A, it should return correct Tax Description" should {
    val assessmentType = "A"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is D, it should return correct Tax Description" should {
    val assessmentType = "D"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is E, it should return correct Tax Description" should {
    val assessmentType = "E"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is F, it should return correct Tax Description" should {
    val assessmentType = "F"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is J, it should return correct Tax Description" should {
    val assessmentType = "J"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is M, it should return correct Tax Description" should {
    val assessmentType = "M"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is R, it should return correct Tax Description" should {
    val assessmentType = "R"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is S, it should return correct Tax Description" should {
    val assessmentType = "S"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is T, it should return correct Tax Description" should {
    val assessmentType = "T"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }

  s"Assessment Type is Z, it should return correct Tax Description" should {
    val assessmentType = "Z"

    "when Correction Claim Indicator is null" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some(null)));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("0")));

      val expectedResult = taxDescriptionHelper.selfAssessment.get(assessmentType)

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: Option[String] =
        taxDescriptionHelper.deriveTaxDescription(createMockTaxTransaction(assessmentType, Some("2")));

      val expectedResult = taxDescriptionHelper.selfAssessmentClaim.get(assessmentType)

      result mustBe expectedResult
    }
  }
}
