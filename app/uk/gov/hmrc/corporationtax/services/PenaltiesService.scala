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

import uk.gov.hmrc.corporationtax.models.*
import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.PenaltiesConnector
import uk.gov.hmrc.http.HeaderCarrier
import PenaltyTransaction.*
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class PenaltiesService @Inject() (
  connector: PenaltiesConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getPenaltyTransactionList(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[PenaltyItems] =
    for {
      penalties <- connector.getPenaltyTransactionList(taxRef, accPeriod)
      isCTPF    <- Future.successful(true) // TODO: implement logic to resolve Council Tax Penalty Framework (CTPF)
    } yield PenaltyItems(penalties.penaltyTransactions.map(p => convertToItems(p, isCTPF)))

}
