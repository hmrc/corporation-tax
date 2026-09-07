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

package uk.gov.hmrc.corporationtax.helpers

import uk.gov.hmrc.corporationtax.models.{DisplayNeeded, DisplayNeededResponse}

trait DisplayNeededHelper {

  val displayNeededAllFalse: DisplayNeeded = DisplayNeeded(
    taxIsDisplayNeededFlag = false,
    interestIsDisplayNeededFlag = false,
    paymentIsDisplayNeededFlag = false,
    repayReallocIsDisplayNeededFlag = false
  )

  val displayNeededAllTrue: DisplayNeeded = DisplayNeeded(
    taxIsDisplayNeededFlag = true,
    interestIsDisplayNeededFlag = true,
    paymentIsDisplayNeededFlag = true,
    repayReallocIsDisplayNeededFlag = true
  )

  val displayNeededMixed: DisplayNeeded = DisplayNeeded(
    taxIsDisplayNeededFlag = true,
    interestIsDisplayNeededFlag = false,
    paymentIsDisplayNeededFlag = true,
    repayReallocIsDisplayNeededFlag = false
  )

  val displayNeededResponseAllFalse: DisplayNeededResponse = DisplayNeededResponse(
    taxIsDisplayNeededFlag = "N",
    interestIsDisplayNeededFlag = "N",
    paymentIsDisplayNeededFlag = "N",
    repayReallocIsDisplayNeededFlag = "N"
  )

  val displayNeededResponseAllTrue: DisplayNeededResponse = DisplayNeededResponse(
    taxIsDisplayNeededFlag = "Y",
    interestIsDisplayNeededFlag = "Y",
    paymentIsDisplayNeededFlag = "Y",
    repayReallocIsDisplayNeededFlag = "Y"
  )

  val displayNeededResponseMixed: DisplayNeededResponse = DisplayNeededResponse(
    taxIsDisplayNeededFlag = "Y",
    interestIsDisplayNeededFlag = "N",
    paymentIsDisplayNeededFlag = "Y",
    repayReallocIsDisplayNeededFlag = "N"
  )

}
