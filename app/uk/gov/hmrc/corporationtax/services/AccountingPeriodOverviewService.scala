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
import uk.gov.hmrc.corporationtax.models.{
  AccountingPeriodOverview, AccountingPeriodsRowResponse, MissingAccountingPeriodError
}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodOverviewService @Inject() (
  accPeriodService: AccountingPeriodService,
  accPeriodDetailsService: AccountingPeriodDetailsService,
  displayNeededService: DisplayNeededService
)(implicit ec: ExecutionContext)
    extends Logging {

  def getAccountingPeriodOverview(taxRef: Long, accPeriod: Long)(implicit
    hc: HeaderCarrier
  ): Future[Either[MissingAccountingPeriodError, AccountingPeriodOverview]] =
    accPeriodService.getAccountingPeriod(taxRef, accPeriod).flatMap {
      case Right(accPeriodResponse: AccountingPeriodsRowResponse) =>
        logger.info(s"Building AccountingPeriodOverview Response for taxRef :: $taxRef, accountingPeriod:: $accPeriod")
        getAccountingPeriodOverviewResponse(taxRef, accPeriod, accPeriodResponse).map(Right(_))

      case Left(error: MissingAccountingPeriodError) =>
        logger.info(
          s"AccountingPeriod information cannot be retrieved for while building AccountingPeriodOverview:: $taxRef, accountingPeriod:: $accPeriod"
        )
        Future.successful(Left(error))
    }

  private def getAccountingPeriodOverviewResponse(
    taxRef: Long,
    accPeriod: Long,
    accPeriodRowResponse: AccountingPeriodsRowResponse
  )(implicit hc: HeaderCarrier): Future[AccountingPeriodOverview] =
    displayNeededService.getDisplayNeeded(taxRef, accPeriod).flatMap { displayNeededResponse =>
      val accPeriodOverview = AccountingPeriodOverview(
        accountingPeriod = accPeriodRowResponse.accountingPeriod,
        apStartDate = accPeriodRowResponse.apStartDate,
        apEndDate = accPeriodRowResponse.apEndDate,
        apStatus = accPeriodRowResponse.apStatus,
        taxChargePresent = accPeriodRowResponse.taxChargePresent,
        clericalIntSig = accPeriodRowResponse.clericalIntSig,
        creditDebitInterestInd = accPeriodRowResponse.creditDebitInterestInd,
        taxTotal = accPeriodRowResponse.taxTotal,
        interestTotal = accPeriodRowResponse.interestTotal,
        penaltyTotal = accPeriodRowResponse.penaltyTotal,
        payslipTotal = accPeriodRowResponse.payslipTotal,
        repayReallocTotal = accPeriodRowResponse.repayReallocTotal,
        adjustmentTotal = accPeriodRowResponse.adjustmentTotal,
        clericalCalculationFlag = findClericalFlag(accPeriodRowResponse),
        taxIsDisplayNeededFlag = displayNeededResponse.taxIsDisplayNeededFlag,
        interestIsDisplayNeededFlag = displayNeededResponse.interestIsDisplayNeededFlag,
        paymentIsDisplayNeededFlag = displayNeededResponse.paymentIsDisplayNeededFlag,
        repayReallocIsDisplayNeededFlag = displayNeededResponse.repayReallocIsDisplayNeededFlag
      )
      if (accPeriodRowResponse.taxChargePresent) {
        accPeriodDetailsService.getAccountingDetails(taxRef, accPeriod).map { value =>
          val newInterestTotal = value.accountingPeriodDetails.totalDerivedActualInterest
          accPeriodOverview.copy(interestTotal = newInterestTotal)
        }
      } else {
        Future.successful(accPeriodOverview)
      }
    }
  private def findClericalFlag(accPeriodRowResponse: AccountingPeriodsRowResponse): Boolean =
    accPeriodRowResponse.clericalIntSig || accPeriodRowResponse.creditDebitInterestInd

}
