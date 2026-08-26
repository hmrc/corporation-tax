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
  AccountingPeriods, AccountingPeriodsRowResponse, MissingAccountingPeriodError, RdsAccountingPeriod,
  RdsAccountingPeriodsRowResponse, TransformToDomainModelError
}
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.toBool
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.corporationtax.utils.EmptyString.emptyString
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodsService @Inject (connector: AccountingPeriodsConnector)(implicit
  ec: ExecutionContext
) extends Logging {

  def getAccountingPeriod(
    taxRef: Long
  )(implicit hc: HeaderCarrier): Future[Either[TransformToDomainModelError, AccountingPeriods]] =
    connector
      .getAccountingPeriods(taxRef)
      .map { rdsAccountingPeriod =>
        transformToAccountingPeriod(rdsAccountingPeriod, taxRef) match {
          case Right(value) =>
            Future.successful(Right(value))
          case Left(error)  =>
            logger.error(s"Couldn't retrieve accountingPeriod for taxRef: $taxRef")
            Future.successful(Left(error))
        }
      }
      .flatten

  private def transformToAccountingPeriod(
    rdsAccountingPeriod: RdsAccountingPeriod,
    taxRef: Long
  ): Either[TransformToDomainModelError, AccountingPeriods] = {
    val rowsEither: Either[TransformToDomainModelError, List[AccountingPeriodsRowResponse]] =
      rdsAccountingPeriod.accountingPeriods.foldRight(
        Right(Nil): Either[TransformToDomainModelError, List[AccountingPeriodsRowResponse]]
      ) { (value, accEither) =>
        for {
          row <- toAccountingPeriodsRowResponse(value, taxRef)
          acc <- accEither
        } yield row :: acc
      }
    rowsEither.map(AccountingPeriods.apply)
  }

  private def toAccountingPeriodsRowResponse(
    row: RdsAccountingPeriodsRowResponse,
    taxRef: Long
  ): Either[TransformToDomainModelError, AccountingPeriodsRowResponse] =
    for {
      accountingPeriod <-
        row.accountingPeriod.toRight(MissingAccountingPeriodError(s"Cannot find accountingPeriod for taxRef : $taxRef"))
    } yield AccountingPeriodsRowResponse(
      accountingPeriod = accountingPeriod,
      apStartDate = row.apStartDate,
      apEndDate = row.apEndDate,
      apStatus = row.apStatus.getOrElse(emptyString),
      taxChargePresent = row.taxChargePresent.exists(toBool),
      clericalIntSig = row.clericalIntSig.exists(toBool),
      creditDebitInterestInd = row.creditDebitInterestInd.exists(toBool),
      taxTotal = AmountTransformation(row.taxTotal),
      interestTotal = AmountTransformation(row.interestTotal),
      penaltyTotal = AmountTransformation(row.penaltyTotal),
      payslipTotal = AmountTransformation(row.payslipTotal),
      repayReallocTotal = AmountTransformation(row.repayReallocTotal),
      adjustmentTotal = AmountTransformation(row.adjustmentTotal)
    )

}
