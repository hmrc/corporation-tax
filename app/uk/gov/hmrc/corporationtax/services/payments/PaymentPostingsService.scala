package uk.gov.hmrc.corporationtax.services.payments

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentPostingsConnector
import uk.gov.hmrc.corporationtax.models.payments.PaymentPostingResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.Future

class PaymentPostingsService @Inject() (connector: PaymentPostingsConnector) {

  def getPaymentPostings(taxRef: Long)(implicit
    hc: HeaderCarrier
  ): Future[PaymentPostingResponse] = {
    logger.info(s"taxRef: $taxRef")
    connector.getPaymentPostings(taxRef)
  }

}
