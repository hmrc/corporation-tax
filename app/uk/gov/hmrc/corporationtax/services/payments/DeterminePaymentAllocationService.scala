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
import uk.gov.hmrc.corporationtax.models.payments.{PaymentAllocation, PaymentAllocationInfo}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeterminePaymentAllocationService @Inject() (
  paymentSearchResultService: PaymentSearchResultService,
  electronicPaymentAllocationDetailsService: ElectronicPaymentAllocationDetailsService,
  chequePaymentAllocationDetailsService: PaymentAllocationChequeDetailsService
)(implicit
  ec: ExecutionContext
) extends Logging {

  def getPaymentType(
    taxRef: Long,
    fromDate: Option[LocalDate],
    toDate: Option[LocalDate],
    userEnteredAmount: BigDecimal
  )(implicit
    hc: HeaderCarrier
  ): Future[List[PaymentAllocation]] = {
    logger.info(
      s"Calling services for taxRef: $taxRef, fromDate: $fromDate, toDate: $toDate, userEnteredAmount: $userEnteredAmount"
    )

    paymentSearchResultService.getPaymentSearchResult(taxRef, fromDate, toDate, userEnteredAmount).flatMap {
      paymentSearchResultResponse =>
        Future
          .sequence(paymentSearchResultResponse.paymentSearchResult.map { paymentSearchResult =>
            if (paymentSearchResult.chequeId == -1 || paymentSearchResult.chequeId == 0) {
              electronicPaymentAllocationDetailsService
                .getPayAllocationElectronicDtls(
                  taxRef,
                  paymentSearchResult.accountingPeriod.toLong,
                  paymentSearchResult.payslipId.toLong
                )
                .map(electronic =>
                  electronic.electronicPaymentAllocationDetails.map { e =>
                    PaymentAllocation(
                      List(
                        PaymentAllocationInfo(
                          accountingPeriod = e.accountingPeriod,
                          apEndDate = e.apEndDate,
                          amount = e.amount,
                          effectiveDateOfPayment = e.effectiveDateOfPayment
                        )
                      )
                    )
                  }
                )
            } else if (paymentSearchResult.payslipId == -1 && paymentSearchResult.accountingPeriod == -1) {
              chequePaymentAllocationDetailsService
                .getPaymentAllocationChequeDetails(taxRef, paymentSearchResult.chequeId.toLong)
                .map(cheque =>
                  cheque.paymentAllocationDetails.map { c =>
                    PaymentAllocation(
                      List(
                        PaymentAllocationInfo(
                          accountingPeriod = c.accountingPeriod,
                          apEndDate = c.apEndDate,
                          amount = c.amount,
                          effectiveDateOfPayment = c.effectiveDateOfPayment
                        )
                      )
                    )
                  }
                )
            } else {
              Future.successful(None)
            }
          })
          .map(_.flatten)
    }
  }
}
