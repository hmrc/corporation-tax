package uk.gov.hmrc.corporationtax.connectors

import play.api.Logging
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.models.RdsCompanyDetailsResponse
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CompanyDetailsRdsProxyConnector @Inject() (http: HttpClientV2, appConfig: AppConfig)(implicit
  ec: ExecutionContext
) extends Logging {

  def getCompanyDetails(taxRef: Long)(implicit hc: HeaderCarrier): Future[RdsCompanyDetailsResponse] = {
    val url: URL = url"${appConfig.rdsDatacacheProxyFullUrl}/company-details/$taxRef"

    http
      .get(url)
      .execute[RdsCompanyDetailsResponse]
      .recover {
        case e: UpstreamErrorResponse =>
          logger.error(
            s"Error from Upstream while retrieving companyDetails for taxRef : $taxRef - ${e.getMessage}"
          )
          throw e
        case e: Throwable             =>
          logger.error(
            s"Unexpected exception while retrieving companyDetails for taxRef: $taxRef: ${e.getMessage}"
          )
          throw new RuntimeException(e.getMessage)
      }
  }
}
