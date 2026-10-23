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

package uk.gov.hmrc.corporationtax.testdata.payments

import uk.gov.hmrc.corporationtax.models.payments.{PaymentSearchResult, PaymentSearchResultRds, PaymentSearchResultResponse, PaymentSearchResultResponseRds}

import java.time.LocalDate

trait PaymentSearchResultHelper {

  val rdsEmptyPaymentSearchResult: PaymentSearchResultResponseRds = PaymentSearchResultResponseRds(paymentSearchResult = List.empty)

  val rdsMultiplePaymentSearchResult: PaymentSearchResultResponseRds = PaymentSearchResultResponseRds(paymentSearchResult =
    List(
      PaymentSearchResultRds(
        accountingPeriod       = Some(1),
        payslipId              = Some(1),
        amount                 = Some(37500),
        effectiveDateOfPayment = Some(LocalDate.of(2007, 7, 14)),
        paymentType            = Some("BGP"),
        bankSortCode           = Some(114433),
        bankAccountNumber      = Some(1004005),
        chequeNumber           = Some(100301),
        paymentAmount          = Some(31250),
        chequeId               = Some(916)
      ),
      PaymentSearchResultRds(
        accountingPeriod       = Some(1),
        payslipId              = Some(2),
        amount                 = Some(31250),
        effectiveDateOfPayment = Some(LocalDate.of(2006, 10, 14)),
        paymentType            = Some("BGP"),
        bankSortCode           = Some(114433),
        bankAccountNumber      = Some(1004005),
        chequeNumber           = Some(100301),
        paymentAmount          = Some(31250),
        chequeId               = Some(917)
      ),
      PaymentSearchResultRds(
        accountingPeriod       = Some(1),
        payslipId              = Some(3),
        amount                 = Some(37500),
        effectiveDateOfPayment = Some(LocalDate.of(2005, 7, 14)),
        paymentType            = Some("BGP"),
        bankSortCode           = Some(114433),
        bankAccountNumber      = Some(1004005),
        chequeNumber           = Some(100301),
        paymentAmount          = Some(31250),
        chequeId               = Some(918)
      )
    )
  )

}
