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
import uk.gov.hmrc.corporationtax.connectors.TaxTransactionsConnector
import uk.gov.hmrc.corporationtax.models.TaxTransactions
import uk.gov.hmrc.corporationtax.utils.applyAmountTransformToList
import uk.gov.hmrc.corporationtax.utils.AmountAdjustableInstances.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.models.TaxTransactionsItem

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TaxTransactionsService @Inject() (
  connector: TaxTransactionsConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def getTaxTransactions(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[TaxTransactions] = {
    logger.info(s"Calling repository with taxRef: $taxRef and accPeriod: $accPeriod")
    connector.getTaxTransactions(taxRef, accPeriod).map { taxTransactions =>
      taxTransactions
        .copy(taxTransactions = filterAndSortTranscations(applyAmountTransformToList(taxTransactions.taxTransactions)))
    }

  }
  private val filterableAssessmentTypes: Set[String] = Set("M", "A", "S", "E", "T", "R", "J", "Z")

  private def filterAndSortTranscations(transactions: List[TaxTransactionsItem]): List[TaxTransactionsItem] = {

    val assessmentTypesWithZeroAmountRetained = scala.collection.mutable.Set.empty[String]

    val filteredTransactions = transactions.filter { transaction =>
      val isDuplicateFilterableZero =
        filterableAssessmentTypes.contains(transaction.assessmentType) && transaction.currentAmount == BigDecimal(0.00)

      if (!isDuplicateFilterableZero) {
        true
      } else if (!assessmentTypesWithZeroAmountRetained.contains(transaction.assessmentType)) {
        assessmentTypesWithZeroAmountRetained.add(transaction.assessmentType)
        true
      } else { false }
    }

    filteredTransactions.sortBy(_.taxDate)(Ordering[LocalDate].reverse)
  }

}
