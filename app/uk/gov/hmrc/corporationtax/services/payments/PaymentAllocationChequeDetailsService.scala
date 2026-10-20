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
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentAllocationChequeDetailsRdsProxyConnector
import uk.gov.hmrc.corporationtax.models.payments.{PaymentAllocationChequeDetails, PaymentAllocationChequeRow}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentAllocationChequeDetailsService @Inject (
  connector: PaymentAllocationChequeDetailsRdsProxyConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getPaymentAllocationChequeDetails(taxRef: Long, chequeId: Long)(implicit
    hc: HeaderCarrier
  ): Future[PaymentAllocationChequeDetails] = {
    logger.info(s"Retrieving PaymentAllocationChequeDetails for taxRef: $taxRef and chequeId:$chequeId")
    connector
      .getPaymentAllocationChequeDetails(taxRef, chequeId)
      .map { rdsPaymentAllocationChequeDetails =>
        PaymentAllocationChequeDetails(
          paymentAllocationDetails =
            rdsPaymentAllocationChequeDetails.paymentAllocationDetails.map { rdsPaymentAllocationChequeRow =>
              PaymentAllocationChequeRow(
                accountingPeriod = rdsPaymentAllocationChequeRow.accountingPeriod,
                apEndDate = rdsPaymentAllocationChequeRow.apEndDate,
                amount = AmountTransformation(rdsPaymentAllocationChequeRow.amount),
                effectiveDateOfPayment = rdsPaymentAllocationChequeRow.effectiveDateOfPayment
              )
            }
        )

      }
  }
}
