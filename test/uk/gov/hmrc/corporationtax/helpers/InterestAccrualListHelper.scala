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

import uk.gov.hmrc.corporationtax.models.{InterestAccrual, InterestAccrualList}

import java.time.LocalDate

trait InterestAccrualListHelper {

  val emptyInterestAccrualList: InterestAccrualList = InterestAccrualList(List.empty)

  val interestAccrualList =
    InterestAccrualList(
      List(
        InterestAccrual(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        ),
        InterestAccrual(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = LocalDate.of(2023, 3, 7),
          interestAccrualToDate = LocalDate.of(2023, 5, 7),
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.0),
          apEndDate = LocalDate.of(2023, 6, 7)
        )
      )
    )

  val interestAccrualSingleItemList =
    InterestAccrualList(
      List(
        InterestAccrual(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        )
      )
    )

  val interestAccrualListTransformed =
    InterestAccrualList(
      List(
        InterestAccrual(
          computationAmount = BigDecimal(-1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(-2.00),
          interestAmount = BigDecimal(-10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        ),
        InterestAccrual(
          computationAmount = BigDecimal(-1.00),
          interestAccrualFromDate = LocalDate.of(2023, 3, 7),
          interestAccrualToDate = LocalDate.of(2023, 5, 7),
          interestRate = BigDecimal(-2.00),
          interestAmount = BigDecimal(-10.0),
          apEndDate = LocalDate.of(2023, 6, 7)
        )
      )
    )

  val interestAccrualSingleItemListTransformed =
    InterestAccrualList(
      List(
        InterestAccrual(
          computationAmount = BigDecimal(-1.00),
          interestAccrualFromDate = LocalDate.of(2021, 3, 7),
          interestAccrualToDate = LocalDate.of(2021, 5, 7),
          interestRate = BigDecimal(-2.00),
          interestAmount = BigDecimal(-10.00),
          apEndDate = LocalDate.of(2021, 6, 7)
        )
      )
    )
}
