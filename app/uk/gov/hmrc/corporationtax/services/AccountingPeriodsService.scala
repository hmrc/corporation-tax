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

package uk.gov.hmrc.corporationtax.services

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.AccountingPeriodsConnector
import uk.gov.hmrc.corporationtax.models.{AccountingPeriods, AccountingPeriodsRowResponse, RdsAccountingPeriod}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.toBool
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodsService @Inject (
  connector: AccountingPeriodsConnector,
  payRepayService: PayRepayReallocationService
)(implicit
  ec: ExecutionContext
) extends Logging {

  def getAccountingPeriod(
    taxRef: Long
  )(implicit hc: HeaderCarrier): Future[AccountingPeriods] =
    connector
      .getAccountingPeriods(taxRef)
      .map { rdsAccountingPeriod =>
        val accPeriodBeforeBF21 = toAccountingPeriods(rdsAccountingPeriod)
        alternatePaymentRepayment(accPeriodBeforeBF21, taxRef)
      }
      .flatten

  private def toAccountingPeriods(
    rdsAccountingPeriod: RdsAccountingPeriod
  ): AccountingPeriods =
    AccountingPeriods(
      accountingPeriods = rdsAccountingPeriod.accountingPeriods.map { value =>
        AccountingPeriodsRowResponse(
          accountingPeriod = value.accountingPeriod,
          apStartDate = value.apStartDate,
          apEndDate = value.apEndDate,
          apStatus = value.apStatus,
          taxChargePresent = value.taxChargePresent.exists(toBool),
          clericalIntSig = value.clericalIntSig.exists(toBool),
          creditDebitInterestInd = value.creditDebitInterestInd.exists(toBool),
          taxTotal = AmountTransformation(value.taxTotal),
          interestTotal = AmountTransformation(value.interestTotal),
          penaltyTotal = AmountTransformation(value.penaltyTotal),
          payslipTotal = AmountTransformation(value.payslipTotal),
          repayReallocTotal = AmountTransformation(value.repayReallocTotal),
          adjustmentTotal = AmountTransformation(value.adjustmentTotal)
        )
      }
    )

  private def alternatePaymentRepayment(accPeriod: AccountingPeriods, taxRef: Long)(implicit
    hc: HeaderCarrier
  ): Future[AccountingPeriods] = {
    val zeroAmount = BigDecimal(0.00)
    def processAlternatePaymentRepayment(
      remaining: List[AccountingPeriodsRowResponse],
      taxRef: Long,
      accumulator: List[AccountingPeriodsRowResponse]
    ): Future[List[AccountingPeriodsRowResponse]] =
      remaining match {
        case Nil          => Future.successful(accumulator)
        case head :: tail =>
          if (!head.taxChargePresent && (head.interestTotal == zeroAmount) && (head.penaltyTotal == zeroAmount)) {
            payRepayService.getTotalAmounts(taxRef, head.accountingPeriod.toLong).flatMap { payRepayServiceResponse =>
              val calcPaySlipTotal   = payRepayServiceResponse.totalAmountPayments
              val calcRepRfrRtoTotal = payRepayServiceResponse.totalAmountRepRfrRto
              processAlternatePaymentRepayment(
                tail,
                taxRef,
                accumulator :+ head.copy(payslipTotal = calcPaySlipTotal).copy(repayReallocTotal = calcRepRfrRtoTotal)
              )
            }

          } else {
            processAlternatePaymentRepayment(tail, taxRef, accumulator :+ head)
          }

      }
    processAlternatePaymentRepayment(accPeriod.accountingPeriods, taxRef, Nil).map { accPeriods =>
      accPeriod.copy(accountingPeriods = accPeriods)
    }
  }

}
