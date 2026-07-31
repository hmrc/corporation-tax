package uk.gov.hmrc.corporationtax.connectors

import play.api.Logging
import uk.gov.hmrc.corporationtax.models.ReallocationFromAccPeriod
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReallocationFromAccPeriodRdsProxyConnector @Inject() (http: HttpClientV2, config: ServicesConfig)(implicit
  ec: ExecutionContext
) extends Logging {

  private val stubPath = config.baseUrl("corporation-tax-stub") + "/corporation-tax-stubs"
  private val rdsDataCachePath = config.baseUrl("rds-datacache-proxy") + "/rds-datacache-proxy"
  private val stubEnabled: Boolean = config.getBoolean("features.corporation-tax-stub-enabled")

  def getReallocationFromAccPeriod(taxPayerReference: Long, accPeriod:Long)(implicit hc: HeaderCarrier): Future[ReallocationFromAccPeriod] = {
    val url: URL =
      if (stubEnabled) url"$stubPath/corporation-tax/interest-charge-summary/$taxPayerReference"
      else url"$rdsDataCachePath/corporation-tax/interest-charge-summary/$taxPayerReference"
    http
      .get(url)
      .execute[ReallocationFromAccPeriod]
      .recover {
        case e: UpstreamErrorResponse =>
          logger.error(
            s"[ReallocationFromAccPeriodRdsProxyConnector][getReallocationFromAccPeriod]: Upstream error - ${e.getMessage}"
          )
          throw e
        case e: Throwable =>
          logger.error(
            s"[ReallocationFromAccPeriodRdsProxyConnector][getReallocationFromAccPeriod]: ${e.getMessage}"
          )
          throw new RuntimeException(e.getMessage)
      }
  }
  
}
