package uk.gov.hmrc.corporationtax.services.payments


import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentPostingsConnector
import uk.gov.hmrc.corporationtax.models.payments.PaymentPostingResponse
import uk.gov.hmrc.corporationtax.utils.AmountTransformation
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.toBool

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentPostingsService @Inject() (connector: PaymentPostingsConnector)(implicit  ec: ExecutionContext) {

  private def transform(e: PaymentPostingResponse): DisplayNeeded =
    DisplayNeeded(
      taxIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.taxIsDisplayNeededFlag),
      interestIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.interestIsDisplayNeededFlag),
      paymentIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.paymentIsDisplayNeededFlag),
      repayReallocIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.repayReallocIsDisplayNeededFlag)
    )
  
  def getPaymentPostings(taxRef: Long)(implicit
                                                          hc: HeaderCarrier
  ): Future[PaymentPostingResponse] = {
    logger.info(s"taxRef: $taxRef")
    connector
      .getPaymentPostings(taxRef)
      .map(record => record)
  }

}
