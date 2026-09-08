package uk.gov.hmrc.corporationtax.models

sealed trait MissingDataError {
  def message: String
}

sealed trait MissingFieldError extends MissingDataError

case class MissingStatueRule(value: String) extends MissingFieldError {
  val message = s"Cannot find Statue Rule: $value"
}
