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
import play.api.i18n.{DefaultMessagesApi, Lang, Messages}

class TaxDescriptionHelperSpec extends AnyWordSpec with Matchers with ScalaFutures {

//  private val parametersTable = Table(
//    ("A"),
//    ("D"),
//    ("E"),
//    ("F"),
//    ("J"),
//    ("M"),
//    ("R"),
//    ("S"),
//    ("T"),
//    ("Z")
//  )

  private val parametersTable = Table(
    ("A", "Amended assessment", "Amended assessment (claim)"),
    ("D", "Discovery assessment", "Discovery assessment (claim)"),
    ("E", "HMRC determination", "HMRC determination (claim)"),
    ("F", "Further assessment", "Further assessment (claim)"),
    ("J", "HMRC amended self assessment", "HMRC amended self assessment (claim)"),
    ("M", "Main assessment", "Main assessment (claim)"),
    ("R", "HMRC amended self assessment", "HMRC amended self assessment (claim)"),
    ("S", "Self assessment", "Self assessment (claim)"),
    ("T", "Taxpayer amended self assessment", "Taxpayer amended self assessment"),
    ("Z", "Return charge", "Return charge (claim)")
  )

  implicit val messages: Messages = new DefaultMessagesApi(
    Map(
      "en" -> Map(
        "taxDescription.assessment.a.standard" -> "Amended assessment",
        "taxDescription.assessment.a.claim" -> "Amended assessment (claim)",
        "taxDescription.assessment.d.standard" -> "Discovery assessment",
        "taxDescription.assessment.d.claim" -> "Discovery assessment (claim)",
        "taxDescription.assessment.e.standard" -> "HMRC determination",
        "taxDescription.assessment.e.claim" -> "HMRC determination (claim)",
        "taxDescription.assessment.f.standard" -> "Further assessment",
        "taxDescription.assessment.f.claim" -> "Further assessment (claim)",
        "taxDescription.assessment.j.standard" -> "HMRC amended self assessment",
        "taxDescription.assessment.j.claim" -> "HMRC amended self assessment (claim)",
        "taxDescription.assessment.m.standard" -> "Main assessment",
        "taxDescription.assessment.m.claim" -> "Main assessment (claim)",
        "taxDescription.assessment.r.standard" -> "HMRC amended self assessment",
        "taxDescription.assessment.r.claim" -> "HMRC amended self assessment (claim)",
        "taxDescription.assessment.s.standard" -> "Self assessment",
        "taxDescription.assessment.s.claim" -> "Self assessment (claim)",
        "taxDescription.assessment.t.standard" -> "Taxpayer amended self assessment",
        "taxDescription.assessment.t.claim" -> "Taxpayer amended self assessment (claim)",
        "taxDescription.assessment.z.standard" -> "Return charge",
        "taxDescription.assessment.z.claim" -> "Return charge (claim)",
      )
    )
  ).preferred(Seq(Lang("en")))

  private val taxDescriptionHelper = new TaxDescriptionHelper();

  "TaxDescriptionHelper" should {
    forAll(parametersTable) { assessmentType =>
      s"Assessment Type is $assessmentType, it should return correct Tax Description" should {

        "when Correction Claim Indicator is null" in {
          val result: String = taxDescriptionHelper.getTaxDescription(assessmentType, null).futureValue

          val expectedResult = s"taxDescription.assessment.${assessmentType.toLowerCase()}.standard"

          println(s"Correction Claim Null")
          println(s"Actual: $result")
          println(s"Expected: $expectedResult")

          result mustBe "dummy"
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
