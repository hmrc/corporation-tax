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

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentPostingsConnector
import uk.gov.hmrc.corporationtax.models.payments.PaymentPostingsResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class PaymentPostingsService @Inject() (connector: PaymentPostingsConnector) {

  def getPaymentPostings(taxRef: Long)(implicit
    hc: HeaderCarrier
  ): Future[PaymentPostingsResponse] = {
    logger.info(s"taxRef: $taxRef")
    connector.getPaymentPostings(taxRef)
  }

}
