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

class CommonBooleanTransformationSpec extends AnyWordSpec with Matchers {

  "CommonBooleanTransformation.toBool" should {
    "return true when the input flag is Y" in {
      CommonBooleanTransformation.toBool("Y") shouldBe true
    }
    "return true when the input flag is y" in {
      CommonBooleanTransformation.toBool("y") shouldBe true
    }
    "return true when the input flag is 1" in {
      CommonBooleanTransformation.toBool("1") shouldBe true
    }

    "return false when the input flag is N" in {
      CommonBooleanTransformation.toBool("N") shouldBe false
    }
    "return false when the input flag is n" in {
      CommonBooleanTransformation.toBool("n") shouldBe false
    }

    "return false when the input flag is 0" in {
      CommonBooleanTransformation.toBool("0") shouldBe false
    }

    "trim spaces and return true when input flag is 1" in {
      CommonBooleanTransformation.toBool("  1  ") shouldBe true
    }

    "return false when the input flag is unknown" in {
      CommonBooleanTransformation.toBool("unknown") shouldBe false
    }

  }

}
