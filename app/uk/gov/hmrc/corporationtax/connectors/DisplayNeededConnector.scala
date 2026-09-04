package uk.gov.hmrc.corporationtax.connectors

import play.api.Logging
import uk.gov.hmrc.*
import uk.gov.hmrc.corporationtax.models.DisplayNeededResponse
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DisplayNeededConnector @Inject() (http: HttpClientV2, config: ServicesConfig)(implicit ec: ExecutionContext)
  extends Logging {

  private val stubEnabled: Boolean = config.getBoolean("features.corporation-tax-stub-enabled")

  private val dataProxyPath =
    if (stubEnabled) {
      config.baseUrl("corporation-tax-stub") + "/corporation-tax-stubs"
    } else {
      config.baseUrl("rds-datacache-proxy") + "/rds-datacache-proxy"
    }

  def getDisplayNeeded(taxRef: Long, accPeriod: Long)(implicit hc: HeaderCarrier): Future[DisplayNeededResponse] = {
    val url: URL = url"$dataProxyPath/corporation-tax/display-needed/$taxRef/$accPeriod"

    http
      .get(url)
      .execute[DisplayNeededResponse]
      .recover { case e: Throwable =>
        logger.error(s"[DisplayNeededConnector][getDisplayNeeded]: $taxRef :: $accPeriod - ${e.getMessage}")
        throw new RuntimeException(e.getMessage)
      }
  }

}
