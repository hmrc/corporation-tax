/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.corporationtax.services.clientExchangeProxy

import play.api.Logging
import org.apache.pekko.actor.ActorSystem
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.connectors.clientExchangeProxy.ClientExchangeProxyConnector
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientListDownloadStatusTransformed
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.ClientListDownloadStatusTransformed.*
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.AsynchronousProcessWaitTime
import uk.gov.hmrc.corporationtax.services.ClientListDownloadStatusService
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration.*
import scala.collection.concurrent.TrieMap

final case class ClientListDownloadFailedException(msg: String) extends RuntimeException(msg)
final case class ClientListDownloadInProgressException(msg: String) extends RuntimeException(msg)
final case class SystemException(msg: String) extends RuntimeException(msg)
final case class NoBusinessIntervalsException(msg: String) extends RuntimeException(msg)

class ClientExchangeProxyService @Inject (
  connector: ClientExchangeProxyConnector,
  clientListDownloadStatusService: ClientListDownloadStatusService,
  appConfig: AppConfig,
  actorSystem: ActorSystem
)(implicit ec: ExecutionContext)
    extends Logging {

  private val serviceName = appConfig.ctServiceName

  // ------------------------------------------------------------
  // Cache: wait plans per credentialId
  // ------------------------------------------------------------

  private val waitTimeCache: TrieMap[String, AsynchronousProcessWaitTime] = TrieMap.empty

  private def cacheWaitTime(credentialId: String, waitTime: AsynchronousProcessWaitTime): Unit =
    waitTimeCache.update(credentialId, waitTime)

  private def getCachedWaitTime(credentialId: String): Option[AsynchronousProcessWaitTime] =
    waitTimeCache.get(credentialId)

  private def clearWaitTime(credentialId: String): Unit =
    waitTimeCache.remove(credentialId)

  private def defaultWaitPlan: AsynchronousProcessWaitTime =
    AsynchronousProcessWaitTime(
      browserIntervalMs = appConfig.ctDefaultBrowserIntervalMs,
      businessIntervalsMs = appConfig.ctDefaultBusinessIntervalsMs
    )

  // ------------------------------------------------------------
  // Helpers
  // ------------------------------------------------------------

  protected def sleep(ms: Long): Future[Unit] =
    val p = Promise[Unit]()
    actorSystem.scheduler.scheduleOnce(ms.millis)(p.success(()))
    p.future

  def getStatus(credId: String)(implicit hc: HeaderCarrier): Future[ClientListDownloadStatusTransformed] =
    clientListDownloadStatusService.getClientListDownloadStatus(credId).map { response =>
      response.status match {
        case -1    => InitiateDownload
        case 0     => InProgress
        case 1     => Succeeded
        case 2     => Failed
        case other =>
          logger.error(s"[DatacacheProxyConnector] unknown status '$other' from rds-datacache-proxy")
          throw new RuntimeException(s"Unknown status '$other' from rds-datacache-proxy")
      }
    }

  protected def logWaitPlan(business: Seq[Long], browserMs: Long): Unit =
    logger.info(
      s"""event="wait_plan" service="$serviceName" business_ms="${business.mkString(",")}" browser_ms=$browserMs"""
    )

  private def logStatus(context: String, index: Option[Int], status: ClientListDownloadStatusTransformed): Unit =
    val indexString = index.map(i => s" index=$i").getOrElse("")
    logger.info(s"""event="status" service="$serviceName" context="$context"$indexString value="${status.toString}"""")

  // ------------------------------------------------------------
  // Shared polling logic (business + browser phases)
  // ------------------------------------------------------------

  // CALLED IF STATUS IS INITIATE DOWNLOAD OR IN PROGRESS
  private def processWithWaitPlan(
    credentialId: String,
    waitPlan: AsynchronousProcessWaitTime
  )(implicit hc: HeaderCarrier): Future[Unit] =
    val business = waitPlan.businessIntervalsMs
    logWaitPlan(business, waitPlan.browserIntervalMs)

    // BUSINESS PHASE
    if business.isEmpty then
      clearWaitTime(credentialId)
      Future.failed(NoBusinessIntervalsException("No business intervals"))
    else
      def loop(index: Int): Future[Boolean] =
        if index >= business.length then
          Future.successful(
            false
          )
        else
          for
            _      <- sleep(business(index))
            status <- getStatus(credentialId)
            _       = logStatus("business", Some(index), status)
            result <- status match
                        case Succeeded =>
                          clearWaitTime(credentialId)
                          Future.successful(true)

                        case Failed =>
                          clearWaitTime(credentialId)
                          Future.failed(ClientListDownloadFailedException("Failed"))

                        case InProgress =>
                          loop(index + 1)

                        case InitiateDownload =>
                          if index < business.length - 1 then
                            loop(
                              index + 1
                            )
                          else
                            clearWaitTime(credentialId)
                            Future.failed(SystemException("Initiate on final business interval"))
          yield result

      for
        businessSucceeded <- loop(0)
        outcome           <-
          if businessSucceeded then Future.unit
          else
            for
              _           <- sleep(waitPlan.browserIntervalMs)
              status      <- getStatus(credentialId)
              _            = logStatus("browser", None, status)
              finalResult <- status match
                               case Succeeded =>
                                 clearWaitTime(credentialId)
                                 Future.unit

                               case Failed =>
                                 clearWaitTime(credentialId)
                                 Future.failed(ClientListDownloadFailedException("Failed after browser interval"))

                               case InitiateDownload =>
                                 clearWaitTime(credentialId)
                                 Future.failed(SystemException("Initiate after browser interval"))

                               case InProgress =>
                                 clearWaitTime(credentialId)
                                 Future.failed(ClientListDownloadInProgressException("Still in progress"))
            yield finalResult
      yield outcome

  // ------------------------------------------------------------
  // Entry point
  // ------------------------------------------------------------

  def process(credentialId: String)(implicit hc: HeaderCarrier): Future[ClientListDownloadStatusTransformed] =
    val underlying: Future[Unit] =
      getStatus(credentialId)
        .flatMap {
          case Succeeded =>
            logStatus("initial", None, Succeeded)
            clearWaitTime(credentialId)
            Future.unit

          case Failed =>
            logStatus("initial", None, Failed)
            clearWaitTime(credentialId)
            Future.failed(
              ClientListDownloadFailedException("Failed")
            )

          case InProgress =>
            logStatus("initial", None, InProgress)
            val waitPlan = getCachedWaitTime(credentialId).getOrElse(
              defaultWaitPlan
            )
            processWithWaitPlan(
              credentialId,
              waitPlan
            )

          case InitiateDownload =>
            logStatus("initial", None, InitiateDownload)

            for
              waitPlan <- connector.initiateClientListDownload(
                            credentialId
                          )
              _         = cacheWaitTime(credentialId, waitPlan)
              outcome  <- processWithWaitPlan(
                            credentialId,
                            waitPlan
                          )
            yield outcome
        }

    underlying
      .map(_ => Succeeded)
      .recover {
        case _: ClientListDownloadInProgressException => InProgress
        case _: ClientListDownloadFailedException     => Failed
        case e: SystemException                       =>
          logger.error("[ClientListService][process] - client list download failed with system exception", e)
          InitiateDownload
      }

}
