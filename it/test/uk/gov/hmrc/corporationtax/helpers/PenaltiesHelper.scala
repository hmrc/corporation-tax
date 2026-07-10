package uk.gov.hmrc.corporationtax.helpers

import uk.gov.hmrc.corporationtax.models.{Penalties, PenaltyTransaction}

import java.time.LocalDate

trait PenaltiesHelper {

  val penaltiesSingleItemList = Penalties(
    List(
      PenaltyTransaction(
        penaltyDate = LocalDate.of(2025, 5, 1),
        `type` = "F",
        postingAmount = BigDecimal(100.13)
      ),
      PenaltyTransaction(penaltyDate = LocalDate.of(2021, 3, 7), `type` = "G", postingAmount = BigDecimal(27.19))
    )
  )
}
