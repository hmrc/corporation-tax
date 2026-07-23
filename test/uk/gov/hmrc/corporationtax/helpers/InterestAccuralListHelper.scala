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

import uk.gov.hmrc.corporationtax.models.{InterestAccural, InterestAccuralList}

import java.time.LocalDate

trait InterestAccuralListHelper {

  val emptyInterestAccuralList: InterestAccuralList = InterestAccuralList(List.empty)

  val interestAccuralList =
    InterestAccuralList(
      List(
        InterestAccural(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        ),
        InterestAccural(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = LocalDate.of(2023, 3, 7),
          interestAccrualToDate = LocalDate.of(2023, 5, 7),
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.0),
          apEndDate = LocalDate.of(2023, 6, 7)
        )
      )
    )

  val interestAccuralSingleItemList =
    InterestAccuralList(
      List(
        InterestAccural(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        )
      )
    )

  val interestAccuralListTransformed =
    InterestAccuralList(
      List(
        InterestAccural(
          computationAmount = BigDecimal(-1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(-2.00),
          interestAmount = BigDecimal(-10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        ),
        InterestAccural(
          computationAmount = BigDecimal(-1.00),
          interestAccrualFromDate = LocalDate.of(2023, 3, 7),
          interestAccrualToDate = LocalDate.of(2023, 5, 7),
          interestRate = BigDecimal(-2.00),
          interestAmount = BigDecimal(-10.0),
          apEndDate = LocalDate.of(2023, 6, 7)
        )
      )
    )

  val interestAccuralSingleItemListTransformed =
    InterestAccuralList(
      List(
        InterestAccural(
          computationAmount = BigDecimal(-1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate =  BigDecimal(-2.00),
          interestAmount = BigDecimal(-10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        )
      )
    )
}
