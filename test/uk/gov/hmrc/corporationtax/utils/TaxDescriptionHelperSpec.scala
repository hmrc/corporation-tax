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

import org.scalatest.Inspectors.forAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.prop.Tables.Table
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.ScalaFutures

class TaxDescriptionHelperSpec extends AnyWordSpec with Matchers with ScalaFutures {

  private val parametersTable = Table(
    ("A"),
    ("D"),
    ("E"),
    ("F"),
    ("J"),
    ("M"),
    ("R"),
    ("S"),
    ("T"),
    ("Z")
  )

  private val taxDescriptionHelper = new TaxDescriptionHelper();

  "TaxDescriptionHelper" should {
    forAll(parametersTable) { assessmentType =>
      s"Assessment Type is $assessmentType, it should return correct Tax Description" should {

        "when Correction Claim Indicator is null" in {
          val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

          val expectedResult = s"taxDescription.assessment.${assessmentType.toLowerCase()}.standard"

          result mustBe expectedResult
        }

        "when Correction Claim Indicator is 0" in {
          val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "0").futureValue

          val expectedResult = s"taxDescription.assessment.${assessmentType.toLowerCase()}.standard"

          result mustBe expectedResult
        }

        "when Correction Claim Indicator is 2" in {
          val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, "2").futureValue

          val expectedResult = s"taxDescription.assessment.${assessmentType.toLowerCase()}.claim"

          result mustBe expectedResult
        }
      }
    }
  }

}
