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

import uk.gov.hmrc.corporationtax.models.{InterestAccrual, InterestAccrualList, InterestAccrualListWithInterestAccruedDays, InterestAccrualWithInterestAccruedDays, StatuteRuleRecord, StatuteRuleResponse}

import java.time.LocalDate

trait InterestAccrualListWithNoOfDaysHelper {
  
  def statuteRuleResponseGen(numberOfDays: Int): StatuteRuleResponse = StatuteRuleResponse(
    statuteRule = StatuteRuleRecord(
      ruleStartDate = Some(LocalDate.of(2011, 1, 1)),
      ruleEndDate = Some(LocalDate.of(2012, 1, 1)),
      numberOfDays = numberOfDays,
      ruleAmount = BigDecimal(145.001),
      ruleRate = BigDecimal(1.47)
    )
  )
  def interestAccrualListGen(fromDate:LocalDate, toDate:LocalDate, apEndDate:LocalDate): InterestAccrualList                                                        =
    InterestAccrualList(
      List(
        InterestAccrual(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = fromDate,
          interestAccrualToDate = toDate,
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.00),
          apEndDate = apEndDate
        )
      )
    )
  def interestAccrualListWithInterestAccruedDaysGen(fromDate:LocalDate, toDate:LocalDate, noOfDays:Long, apEndDate:LocalDate): InterestAccrualListWithInterestAccruedDays =
    InterestAccrualListWithInterestAccruedDays(
      List(
        InterestAccrualWithInterestAccruedDays(
          computationAmount = BigDecimal(1.00),
          interestAccrualFromDate = fromDate,
          interestAccrualToDate = toDate,
          interestRate = BigDecimal(2.00),
          interestAmount = BigDecimal(10.00),
          apEndDate = apEndDate,
          noOfDays = noOfDays
        )
      )
    )

}
