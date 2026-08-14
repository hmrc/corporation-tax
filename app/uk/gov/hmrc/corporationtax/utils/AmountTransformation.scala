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

import scala.math.BigDecimal.RoundingMode

object AmountTransformation {
  def apply(amount: Option[BigDecimal]): BigDecimal = {
    val currentAmount = amount.getOrElse(BigDecimal(0.00))
    val rounded       = currentAmount.setScale(2, RoundingMode.HALF_UP)

    if (rounded.signum == 0) rounded else rounded * -1
  }

  def negateAmount(amount: Option[BigDecimal]): BigDecimal = {
    val currentAmount = amount.getOrElse(BigDecimal(0.00))

    println("CURRENT AMOUNT: " + currentAmount)
    println("CURRENT AMOUNT NEGATE: " + currentAmount * -1)
    currentAmount * -1
  }
}
