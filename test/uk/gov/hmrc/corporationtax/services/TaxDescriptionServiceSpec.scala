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

package uk.gov.hmrc.corporationtax.Services

import org.scalatest.Inspectors.forAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.prop.Tables.Table
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.corporationtax.services.TaxDescriptionService
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext

class TaxDescriptionServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {
  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  private val parametersTable = Table(
    "A",
    "D",
    "E",
    "F",
    "J",
    "M",
    "R",
    "S",
    "T",
    "Z"
  )

  private class Setup {
    val service = new TaxDescriptionService()
  }

  it should "check all assessment types" {
    forAll(parametersTable) { assessmentType =>
      s"Assessment Type is $assessmentType, it should return correct Tax Description" should {
        "when Correction Claim Indicator is null" in new Setup {
          val result: String = service.getTaxDescription(assessmentType, null).futureValue

          result mustBe "dummy text"
        }

        "when Correction Claim Indicator is 0" in new Setup {
          val result: String = service.getTaxDescription(assessmentType, "0").futureValue

          result mustBe "dummy text"
        }

        "when Correction Claim Indicator is 2" in new Setup {
          val result: String = service.getTaxDescription(assessmentType, "2").futureValue

          result mustBe "dummy text"
        }
      }
    }
  }

//  "Assessment Type is A, it should return correct Tax Description" should {
//    "when Correction Claim Indicator is null" in new Setup {
//      val result: String = service.getTaxDescription("A", null).futureValue
//
//      result mustBe "dummy text"
//    }
//
//    "when Correction Claim Indicator is 0" in new Setup {
//      val result: String = service.getTaxDescription("A", "0").futureValue
//
//      result mustBe "dummy text"
//    }
//
//    "when Correction Claim Indicator is 2" in new Setup {
//      val result: String = service.getTaxDescription("A", "2").futureValue
//
//      result mustBe "dummy text"
//    }
//  }

}
