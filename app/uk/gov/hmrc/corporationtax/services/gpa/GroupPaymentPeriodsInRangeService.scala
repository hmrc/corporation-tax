package uk.gov.hmrc.corporationtax.services.gpa

import play.api.i18n.Lang.logger
import uk.gov.hmrc.corporationtax.connectors.gpa.GroupPaymentPeriodsInRangeConnector
import uk.gov.hmrc.corporationtax.models.gpa.{PeriodWithinRange, PeriodWithinRangeResponse}
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GroupPaymentPeriodsInRangeService @Inject() (
                                       connector: GroupPaymentPeriodsInRangeConnector
                                     )(implicit ec: ExecutionContext) {

  private def transform(e: PeriodWithinRangeResponse): PeriodWithinRange =
    PeriodWithinRange(
      isPeriodWithinRange = CommonBooleanTransformation.toBool(e.isPeriodWithinRange),
    )

  def getGroupPaymentPeriodsInRange(gpaUTR: Long, nominatedCompanyUTR: Long, pPeriod: Long, pMonthRestriction: Long)(implicit hc: HeaderCarrier): Future[PeriodWithinRange] = {
    logger.info("Calling repository for gpaUTR: $gpaUTR, nominatedCompanyUTR: $nominatedCompanyUTR, pPeriod: $pPeriod, pMonthRestriction: $pMonthRestriction")
    connector
      .getGroupPaymentPeriodsInRange(gpaUTR, nominatedCompanyUTR, pPeriod, pMonthRestriction)
      .map(transform)
      .map(record => record)
  }

}
