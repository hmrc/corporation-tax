package uk.gov.hmrc.corporationtax.services

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.DisplayNeededConnector
import uk.gov.hmrc.corporationtax.models.{DisplayNeeded, DisplayNeededResponse}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DisplayNeededService @Inject() (
  connector: DisplayNeededConnector
)(implicit ec: ExecutionContext) {

  private def transform(e: DisplayNeededResponse): DisplayNeeded =
    DisplayNeeded(
      taxIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.taxIsDisplayNeededFlag),
      interestIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.interestIsDisplayNeededFlag),
      paymentIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.paymentIsDisplayNeededFlag),
      repayReallocIsDisplayNeededFlag = CommonBooleanTransformation.toBool(e.repayReallocIsDisplayNeededFlag)
    )

  def getDisplayNeeded(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[DisplayNeeded] = {
    logger.info(s"Calling repository with taxRef: $taxRef and accPeriod: $accPeriod")
    connector
      .getDisplayNeeded(taxRef, accPeriod)
      .map(transform)
  }

}
