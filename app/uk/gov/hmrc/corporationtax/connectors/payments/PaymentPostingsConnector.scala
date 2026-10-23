package uk.gov.hmrc.corporationtax.connectors.payments

import play.api.Logging
import uk.gov.hmrc.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.models.payments.PaymentPostingResponse
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentPostingsConnector @Inject() (http: HttpClientV2, appConfig: AppConfig)(implicit
                                                                                            ec: ExecutionContext
) extends Logging {

  def getPaymentPostings(taxRef: Long)(implicit
                                                                hc: HeaderCarrier
  ): Future[PaymentPostingResponse] = {
    val url: URL = url"${appConfig.rdsDatacacheProxyFullUrl}/payment-postings/$taxRef"
    http
      .get(url)
      .execute[PaymentPostingResponse]
      .recover { case e: Throwable =>
        logger.error(s"Error: taxRef: $taxRef - ${e.getMessage}")
        throw new RuntimeException(e.getMessage)
      }
  }

}
