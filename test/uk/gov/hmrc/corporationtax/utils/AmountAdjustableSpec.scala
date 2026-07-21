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

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec

class AmountAdjustableSpec extends AnyWordSpec with Matchers {

  case class TestItem(amountOne: BigDecimal, amountTwo: BigDecimal, textField: String)

  "applyAmountTransformation" should {

    "apply the transform to a single field" in {
      implicit val singleFieldAdjustable: AmountAdjustable[TestItem] =
        new AmountAdjustable[TestItem] {
          def amountFields: List[(TestItem => Option[BigDecimal], (TestItem, BigDecimal) => TestItem)] =
            List(
              (item => Some(item.amountOne), (item, newValue) => item.copy(amountOne = newValue))
            )
        }

      val input  = TestItem(amountOne = 15.375, amountTwo = 12.322, textField = "unchanged")
      val result = applyAmountTransform(input)

      result.amountOne shouldBe BigDecimal(-15.38)
      result.amountTwo shouldBe BigDecimal(12.322)
      result.textField shouldBe "unchanged"
    }

    "apply the transform to multiple fields in the same object" in {
      implicit val multipleFieldAdjustable: AmountAdjustable[TestItem] =
        new AmountAdjustable[TestItem] {
          def amountFields: List[(TestItem => Option[BigDecimal], (TestItem, BigDecimal) => TestItem)] =
            List(
              (item => Some(item.amountOne), (item, newValue) => item.copy(amountOne = newValue)),
              (item => Some(item.amountTwo), (item, newValue) => item.copy(amountTwo = newValue))
            )
        }

      val input  = TestItem(amountOne = 15.375, amountTwo = 12.322, textField = "unchanged")
      val result = applyAmountTransform(input)

      result.amountOne shouldBe BigDecimal(-15.38)
      result.amountTwo shouldBe BigDecimal(-12.32)
      result.textField shouldBe "unchanged"
    }

    "return unchanged if no fields are specified for adjustment" in {
      implicit val noFieldAdjustable: AmountAdjustable[TestItem] =
        new AmountAdjustable[TestItem] {
          def amountFields: List[(TestItem => Option[BigDecimal], (TestItem, BigDecimal) => TestItem)] =
            List.empty
        }

      val input  = TestItem(amountOne = 15.375, amountTwo = 12.322, textField = "unchanged")
      val result = applyAmountTransform(input)

      result shouldBe input

    }
  }
  "applyAmountTransformToList" should {
    implicit val singleFieldAdjustable: AmountAdjustable[TestItem] =
      new AmountAdjustable[TestItem] {
        def amountFields: List[(TestItem => Option[BigDecimal], (TestItem, BigDecimal) => TestItem)] =
          List(
            (item => Some(item.amountOne), (item, newValue) => item.copy(amountOne = newValue))
          )
      }

    "apply the transformation to every item in the list" in {
      val input  = List(
        TestItem(amountOne = 10.001, amountTwo = 22.22, textField = "unchanged"),
        TestItem(amountOne = -102.006, amountTwo = 22.22, textField = "unchanged"),
        TestItem(amountOne = 0.006, amountTwo = 22.22, textField = "unchanged")
      )
      val result = applyAmountTransformToList(input)

      result.map(_.amountOne) shouldBe List(BigDecimal(-10.00), BigDecimal(102.01), BigDecimal(-0.01))
    }

    "return an empty list when given an empty list" in {
      applyAmountTransformToList(List.empty) shouldBe List.empty
    }
  }
}
