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
import uk.gov.hmrc.corporationtax.models.{
  AccountingPeriods, AccountingPeriodsRowResponse, MissingAccountingPeriodError, RdsAccountingPeriod
}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.toBool
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodService @Inject (
  connector: AccountingPeriodsConnector,
  payRepayService: PayRepayReallocationService
)(implicit
  ec: ExecutionContext
) extends Logging {

  def getAccountingPeriod(
    taxRef: Long,
    accPeriod: Long
  )(implicit hc: HeaderCarrier): Future[Either[MissingAccountingPeriodError, AccountingPeriodsRowResponse]] =
    connector
      .getAccountingPeriods(taxRef)
      .flatMap { rdsAccountingPeriod =>
        val accPeriodList = toAccountingPeriods(rdsAccountingPeriod)
        alternatePaymentRepayment(accPeriodList, taxRef, accPeriod)
      }

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

  private def alternatePaymentRepayment(accPeriodList: AccountingPeriods, taxRef: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[Either[MissingAccountingPeriodError, AccountingPeriodsRowResponse]] = {
    val zeroAmount = BigDecimal(0.00)

    accPeriodList.accountingPeriods.find(_.accountingPeriod == accPeriod) match {
      case Some(accPeriod: AccountingPeriodsRowResponse) =>
        if (
          !accPeriod.taxChargePresent && (accPeriod.interestTotal == zeroAmount) && (accPeriod.penaltyTotal == zeroAmount)
        ) {
          for {
            payRepayServiceResponse <- payRepayService.getTotalAmounts(taxRef, accPeriod.accountingPeriod.toLong)
          } yield {
            val calcPaySlipTotal   = payRepayServiceResponse.totalAmountPayments
            val calcRepRfrRtoTotal = payRepayServiceResponse.totalAmountRepRfrRto
            Right(accPeriod.copy(payslipTotal = calcPaySlipTotal, repayReallocTotal = calcRepRfrRtoTotal))
          }
        } else {
          Future.successful(Right(accPeriod))
        }
      case None                                          =>
        Future.successful(
          Left(MissingAccountingPeriodError(s"Cannot find AccountingPeriod information for the accPeriod::$accPeriod"))
        )
    }

  }
}
