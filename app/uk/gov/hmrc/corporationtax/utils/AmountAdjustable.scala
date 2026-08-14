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

trait AmountAdjustable[A] {
  def amountFields: List[(A => Option[BigDecimal], (A, BigDecimal) => A)]
}

def applyAmountTransform[A: AmountAdjustable](item: A): A = {
  val adjuster = implicitly[AmountAdjustable[A]]
  adjuster.amountFields.foldLeft(item) { case (currentItem, (get, set)) =>
    set(currentItem, AmountTransformation(get(currentItem)))
  }
}

def applyAmountTransformToList[A: AmountAdjustable](items: List[A]): List[A] =
  items.map(applyAmountTransform(_))

def applyAmountNegation[A: AmountAdjustable](item: A): A = {
  val adjuster = implicitly[AmountAdjustable[A]]
  adjuster.amountFields.foldLeft(item) { case (currentItem, (get, set)) =>
    set(currentItem, AmountTransformation.negateAmount(get(currentItem)))
  }
}

def applyAmountNegateToList[A: AmountAdjustable](items: List[A]): List[A] =
  items.map(applyAmountNegation(_))
