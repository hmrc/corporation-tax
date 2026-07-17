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

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.{JsError, JsNumber, JsString, JsSuccess}

class PenaltyTransactionJsonSpec extends AnyFlatSpec {

  val penaltyTransactionTypeSeq: Seq[JsString] = Seq(
    JsString("FX"),
    JsString("FT"),
    JsString("TG"),
    JsString("TR")
  )

  val penaltyTransactionTypeConversionResult: Seq[JsSuccess[PenaltyTransactionType]] = Seq(
    JsSuccess(PenaltyTransactionType.FX),
    JsSuccess(PenaltyTransactionType.FT),
    JsSuccess(PenaltyTransactionType.TG),
    JsSuccess(PenaltyTransactionType.TR)
  )

  it should "convert JsValue to known PenaltyTransactionType's" in {
    assert(
      penaltyTransactionTypeSeq.map(penaltyTransactionType.reads(_)) == penaltyTransactionTypeConversionResult
    )
  }

  it should "fail conversion for invalid value in JsString" in {
    assert(
      penaltyTransactionType.reads(JsString("InvalidPenaltyType")) == JsError("Invalid PenaltyTransactionType string")
    )
  }

  it should "fail conversion if not JsString parameter provided" in {
    assert(
      penaltyTransactionType.reads(JsNumber(BigDecimal("1"))) == JsError("penaltyTransactionType::Expected JsString")
    )
  }

}
