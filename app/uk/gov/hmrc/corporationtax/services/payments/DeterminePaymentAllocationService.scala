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

package uk.gov.hmrc.corporationtax.services.payments

import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeterminePaymentAllocationService @Inject()(paymentSearchResultService: PaymentSearchResultService,
                                                  ePaymentAllocationDetailsService: ElectronicPaymentAllocationDetailsService,
                                                  chequePaymentAllocationDetailsService: PaymentAllocationChequeDetailsService)(
  implicit ec: ExecutionContext
) extends Logging {

  def getPaymentType(taxRef: Long, fromDate: Option[LocalDate], toDate: Option[LocalDate])(implicit
    hc: HeaderCarrier
  ): Future[PaymentAllocationDetails] = {
    logger.info(s"Calling services for taxRef: $taxRef, fromDate: $fromDate, toDate: $toDate")

    paymentSearchResultService.getPaymentSearchResult(taxRef, fromDate, toDate).map { paymentSearchResultResponse =>
      paymentSearchResultResponse.paymentSearchResult.map { rdsAccountingPeriodList =>

    }
  }

    for {
      reallocationTo <- chequePaymentAllocationDetailsService.getPaymentAllocationChequeDetails(taxRef, accPeriod)
    } yield {
      val repaymentsSummary = repayments.repayments.map { repaymentsDetails =>
        RepayReallocationSummaryDetails(
          transactionDate = Some(repaymentsDetails.repaymentDate),
          `type` = Some(repaymentsDetails.repaymentType),
          amount = repaymentsDetails.amount,
          accountingPeriodEndDate = None,
          taxpayerReference = None
        )
      }
  }
}
