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

package uk.gov.hmrc.corporationtax.testdata

import uk.gov.hmrc.corporationtax.models.*

import java.time.LocalDate

trait StatuteRuleHelper {
  val defaultRecord = StatuteRuleItem(
    ruleStartDate = Some(LocalDate.parse("1999-01-18")),
    ruleEndDate = Some(LocalDate.parse("1999-02-14")),
    numberOfDays = Some(27),
    ruleAmount = Some(BigDecimal(100.011)),
    ruleRate = Some(BigDecimal(5.75))
  )

  val recordWithEmptyFields = StatuteRuleItem(
    ruleStartDate = None,
    ruleEndDate = None,
    numberOfDays = None,
    ruleAmount = None,
    ruleRate = None
  )

  val defaultResponseRecord = StatuteRuleRecord(
    numberOfDays = 87,
    ruleAmount = BigDecimal(145.001),
    ruleRate = BigDecimal(1.47)
  )
}
