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
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentSearchResultConnector
import uk.gov.hmrc.corporationtax.models.payments.{
  PaymentSearchResult, PaymentSearchResultResponse, PaymentSearchResultResponseRds
}
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.corporationtax.utils.EmptyAndZeroConstants.{emptyString, zeroValue}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentSearchResultService @Inject() (
  connector: PaymentSearchResultConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getPaymentSearchResult(taxRef: Long, fromDate: Option[LocalDate], toDate: Option[LocalDate])(implicit
    hc: HeaderCarrier
  ): Future[PaymentSearchResultResponse] = {
    logger.info(s"Calling Connector to getPaymentSearchResult: $taxRef :: $fromDate - $toDate")
    connector
      .getPaymentSearchResult(taxRef, fromDate, toDate)
      .map { paymentSearchResultResponseRds =>
        toPaymentSearchResult(paymentSearchResultResponseRds)
      }

  }

  private def toPaymentSearchResult(
    paymentSearchResultResponseRds: PaymentSearchResultResponseRds
  ): PaymentSearchResultResponse =
    PaymentSearchResultResponse(
      paymentSearchResult = paymentSearchResultResponseRds.paymentSearchResult.map { value =>
        PaymentSearchResult(
          accountingPeriod = value.accountingPeriod.getOrElse(BigDecimal(zeroValue)),
          payslipId = value.payslipId.getOrElse(BigDecimal(zeroValue)),
          amount = AmountTransformation(value.amount),
          effectiveDateOfPayment = value.effectiveDateOfPayment,
          paymentType = value.paymentType.getOrElse(emptyString),
          bankSortCode = value.bankSortCode.getOrElse(BigDecimal(zeroValue)),
          bankAccountNumber = value.bankAccountNumber.getOrElse(BigDecimal(zeroValue)),
          chequeNumber = value.chequeNumber.getOrElse(BigDecimal(zeroValue)),
          paymentAmount = AmountTransformation(value.paymentAmount),
          chequeId = value.chequeId.getOrElse(BigDecimal(zeroValue))
        )
      }
    )

}
