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
import uk.gov.hmrc.corporationtax.models.{
  AmendedAssessment, DiscoveryAssessment, FurtherAssessment, HMRCAmendedSelfAssessment, HMRCDetermination
}
import uk.gov.hmrc.corporationtax.models.{
  HMRCAmendedSelfAssessment2, MainAssessment, ReturnCharge, SelfAssessment, TaxpayerAmendedSelfAssessment
}

class TaxDescriptionHelperSpec extends AnyWordSpec with Matchers with ScalaFutures {

  private val taxDescriptionHelper = new TaxDescriptionHelper();

  s"Assessment Type is A, it should return correct Tax Description" should {
    val assessmentType = "A"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = AmendedAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = AmendedAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = AmendedAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is D, it should return correct Tax Description" should {
    val assessmentType = "D"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = DiscoveryAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = DiscoveryAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = DiscoveryAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is E, it should return correct Tax Description" should {
    val assessmentType = "E"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = HMRCDetermination.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = HMRCDetermination.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = HMRCDetermination.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is F, it should return correct Tax Description" should {
    val assessmentType = "F"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = FurtherAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = FurtherAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = FurtherAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is J, it should return correct Tax Description" should {
    val assessmentType = "J"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = HMRCAmendedSelfAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = HMRCAmendedSelfAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = HMRCAmendedSelfAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is M, it should return correct Tax Description" should {
    val assessmentType = "M"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = MainAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = MainAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = MainAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is R, it should return correct Tax Description" should {
    val assessmentType = "R"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = HMRCAmendedSelfAssessment2.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = HMRCAmendedSelfAssessment2.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = HMRCAmendedSelfAssessment2.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is S, it should return correct Tax Description" should {
    val assessmentType = "S"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = SelfAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = SelfAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = SelfAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is T, it should return correct Tax Description" should {
    val assessmentType = "T"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = TaxpayerAmendedSelfAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = TaxpayerAmendedSelfAssessment.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = TaxpayerAmendedSelfAssessment.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is Z, it should return correct Tax Description" should {
    val assessmentType = "Z"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = ReturnCharge.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = ReturnCharge.standard;

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = ReturnCharge.correctionClaim;

      result mustBe expectedResult
    }
  }

  s"Assessment Type is not recognised, it should return blank"      should {
    val assessmentType = "RandomAssessmentType"

    "when Correction Claim Indicator is null" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

      val expectedResult = "";

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 0" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

      val expectedResult = "";

      result mustBe expectedResult
    }

    "when Correction Claim Indicator is 2" in {
      val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

      val expectedResult = "";

      result mustBe expectedResult
    }
  }
}
