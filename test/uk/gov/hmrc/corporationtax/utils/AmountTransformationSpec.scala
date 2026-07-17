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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AmountTransformationSpec extends AnyWordSpec with Matchers {

  "AmountTransformation" should {

    "return 0.00 when the input is None" in {
      AmountTransformation(None) shouldBe BigDecimal(0.00)
    }

    "round down when the digit after 2nd decimal place is less than 5" in {
      AmountTransformation(Some(BigDecimal(100.341))) shouldBe BigDecimal(100.34) * -1
    }

    "round up (HALF_UP) when the digit after 2nd decimal place is exactly 5" in {
      AmountTransformation(Some(BigDecimal(10.005))) shouldBe BigDecimal(10.01) * -1
    }

    "round up (HALF_UP) when the digit after 2nd decimal place is greater than 5" in {
      AmountTransformation(Some(BigDecimal(10.007))) shouldBe BigDecimal(10.01) * -1
    }

    "flip positive to negative" in {
      AmountTransformation(Some(BigDecimal(10.00))) shouldBe BigDecimal(-10.00)
    }

    "flip negative to positive" in {
      AmountTransformation(Some(BigDecimal(-10.00))) shouldBe BigDecimal(10.00)
    }

    "zero amount should always be positive" in {
      AmountTransformation(Some(BigDecimal(0.00))) shouldBe BigDecimal(0.00)
    }

    "zero amount should always be positive, even if coming in as negative" in {
      AmountTransformation(Some(BigDecimal(-0.00))) shouldBe BigDecimal(0.00)
    }

    "leave rounded to zero amount as positive zero" in {
      AmountTransformation(Some(BigDecimal(0.001))) shouldBe BigDecimal(0.00)
    }
  }

}
