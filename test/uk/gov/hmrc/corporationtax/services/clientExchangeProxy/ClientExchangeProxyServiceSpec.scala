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

import uk.gov.hmrc.corporationtax.controllers.base.SpecBase
import org.mockito.Mockito.*
import org.apache.pekko.actor.ActorSystem
import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.corporationtax.config.AppConfig
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus
import uk.gov.hmrc.corporationtax.connectors.clientExchangeProxy.ClientExchangeProxyConnector
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.{
  AsynchronousProcessWaitTime, ClientListDownloadStatusTransformed
}
import uk.gov.hmrc.corporationtax.services.ClientListDownloadStatusService
import uk.gov.hmrc.http.HeaderCarrier
import scala.collection.concurrent.TrieMap

import scala.concurrent.{ExecutionContext, Future}

class ClientExchangeProxyServiceSpec extends SpecBase {

  private val appConfig: AppConfig     = app.injector.instanceOf[AppConfig]
  private val actorSystem: ActorSystem = app.injector.instanceOf[ActorSystem]

  class TestClientListService(
    clientExchangeProxyConnector: ClientExchangeProxyConnector,
    clientListDownloadStatusService: ClientListDownloadStatusService
  )(implicit ec: ExecutionContext)
      extends ClientExchangeProxyService(
        clientExchangeProxyConnector,
        clientListDownloadStatusService,
        appConfig,
        actorSystem
      ) {

    // capture the wait plan actually used by processWithWaitPlan
    @volatile var lastWaitPlan: Option[AsynchronousProcessWaitTime] = None

    override protected def sleep(ms: Long): Future[Unit] =
      Future.unit

    override protected def logWaitPlan(business: Seq[Long], browserMs: Long): Unit =
      super.logWaitPlan(business, browserMs)
      lastWaitPlan = Some(
        AsynchronousProcessWaitTime(
          browserIntervalMs = browserMs,
          businessIntervalsMs = business.toList
        )
      )
  }

  private def setupService(
    clientExchangeProxyConnector: ClientExchangeProxyConnector = mock[ClientExchangeProxyConnector],
    clientListDownloadStatusService: ClientListDownloadStatusService = mock[ClientListDownloadStatusService]
  ): (TestClientListService, ClientExchangeProxyConnector, ClientListDownloadStatusService) = {

    val service = new TestClientListService(clientExchangeProxyConnector, clientListDownloadStatusService)
    (service, clientExchangeProxyConnector, clientListDownloadStatusService)
  }

  private def waitTimeCacheOf(service: ClientExchangeProxyService): TrieMap[String, AsynchronousProcessWaitTime] = {
    val field = classOf[ClientExchangeProxyService].getDeclaredField("waitTimeCache")
    field.setAccessible(true)
    field.get(service).asInstanceOf[TrieMap[String, AsynchronousProcessWaitTime]]
  }

  "ClientExchangeProxyService.process" should {

    "return Succeeded status when initial status equals 1" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = 1)))

      val status = service.process("cred-1").futureValue
      status shouldBe ClientListDownloadStatusTransformed.Succeeded

    }

    "return Failed status when initial status equals 2" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = 2)))

      val exception = service.process("cred-1").futureValue

      exception shouldBe ClientListDownloadStatusTransformed.Failed
    }

    "InitiateDownload status should call clientExchangeProxyConnector once to fetch wait plan" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = -1)))

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(100L)
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      whenReady(service.process("cred-1")) { _ =>
        verify(clientExchangeProxyConnector, times(1)).initiateClientListDownload(any)(any)
      }
    }

    "InitiateDownload status with no business intervals should throw NoBusinessIntervalsException" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = -1)))

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = Nil
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      val ex = service.process("cred-1").failed.futureValue

      ex shouldBe a[NoBusinessIntervalsException]
    }

    "should call clientListDownloadStatusService after each business interval" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val businessIntervals = List(10L, 20L, 30L)

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = -1)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 1))
        )
      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = businessIntervals
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      whenReady(service.process("cred-1")) { _ =>
        verify(clientListDownloadStatusService, times(4)).getClientListDownloadStatus(any, any, any)(any)
      }
    }

    "terminal status equals 1 during business phase should abort loop and return Succeeded status" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(10L, 20L)
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = -1)),
          Future.successful(ClientDownloadStatus(status = 1))
        )

      whenReady(service.process("cred-1")) { _ =>
        verify(clientListDownloadStatusService, times(2)).getClientListDownloadStatus(any, any, any)(any)
      }

      val status = service.process("cred-1").futureValue
      status shouldBe ClientListDownloadStatusTransformed.Succeeded
    }

    "terminal status equals 2 during business phase should abort loop and return Failed status" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(10L, 20L)
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = -1)),
          Future.successful(ClientDownloadStatus(status = 2))
        )

      whenReady(service.process("cred-1")) { _ =>
        verify(clientListDownloadStatusService, times(2)).getClientListDownloadStatus(any, any, any)(any)
      }

      val status = service.process("cred-1").futureValue
      status shouldBe ClientListDownloadStatusTransformed.Failed
    }

    "return InitiateDownload status when status equals -1 returned after final business interval" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(10L, 20L)
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = -1)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = -1))
        )

      whenReady(service.process("cred-1")) { _ =>
        verify(clientListDownloadStatusService, times(3)).getClientListDownloadStatus(any, any, any)(any)
      }

      val status = service.process("cred-1").futureValue

      status shouldBe ClientListDownloadStatusTransformed.InitiateDownload
    }

    "return InProgress status when status equals 0 returned after browser wait" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(10L, 20L)
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = -1)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0))
        )

      whenReady(service.process("cred-1")) { _ =>
        verify(clientListDownloadStatusService, times(4)).getClientListDownloadStatus(any, any, any)(any)
      }

      val status = service.process("cred-1").futureValue

      status shouldBe ClientListDownloadStatusTransformed.InProgress
    }

    "return InitiateDownload status when status of -1 returned after browser wait" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val waitPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(10L, 20L)
      )

      when(clientExchangeProxyConnector.initiateClientListDownload(any)(any))
        .thenReturn(Future.successful(waitPlan))

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = -1)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = -1))
        )

      whenReady(service.process("cred-1")) { _ =>
        verify(clientListDownloadStatusService, times(4)).getClientListDownloadStatus(any, any, any)(any)
      }

      val status = service.process("cred-1").futureValue

      status shouldBe ClientListDownloadStatusTransformed.InitiateDownload
    }

    "initial InProgress should use cached intervals when present" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val cachedPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = 9999L,
        businessIntervalsMs = List(111L, 222L)
      )

      val cache = waitTimeCacheOf(service)
      cache.update("cred-1", cachedPlan)

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 1))
        )

      whenReady(service.process("cred-1")) { _ =>
        verify(clientExchangeProxyConnector, never()).initiateClientListDownload(any[String])(any[HeaderCarrier])
        service.lastWaitPlan shouldBe Some(cachedPlan)
      }
    }

    "initial InProgress should use default intervals when no cached plan exists" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val cache = waitTimeCacheOf(service)
      cache.remove("cred-2")

      val defaultPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = appConfig.ctDefaultBrowserIntervalMs,
        businessIntervalsMs = appConfig.ctDefaultBusinessIntervalsMs
      )

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 1))
        )

      whenReady(service.process("cred-2")) { _ =>
        verify(clientExchangeProxyConnector, never()).initiateClientListDownload(any[String])(any[HeaderCarrier])
        service.lastWaitPlan shouldBe Some(defaultPlan)
      }
    }

    "initial InProgress should follow business and browser phases same as InitiateDownload" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      val cache = waitTimeCacheOf(service)
      cache.remove("cred-1")

      val defaultPlan = AsynchronousProcessWaitTime(
        browserIntervalMs = appConfig.ctDefaultBrowserIntervalMs,
        businessIntervalsMs = appConfig.ctDefaultBusinessIntervalsMs
      )

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0)),
          Future.successful(ClientDownloadStatus(status = 0))
        )

      val status = service.process("cred-1").futureValue

      status shouldBe ClientListDownloadStatusTransformed.InProgress
      verify(clientExchangeProxyConnector, never()).initiateClientListDownload(any)(any)

      service.lastWaitPlan shouldBe Some(defaultPlan)
      verify(clientListDownloadStatusService, times(4)).getClientListDownloadStatus(any, any, any)(any)
    }

  }

  "ClientExchangeProxyService.getStatus" should {

    "map a getClientListDownloadStatus of -1 to InitiateDownload" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = -1)))

      val result = service.getStatus("cred-1").futureValue

      result shouldBe ClientListDownloadStatusTransformed.InitiateDownload
    }

    "map a getClientListDownloadStatus of 0 to InProgress" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = 0)))

      val result = service.getStatus("cred-1").futureValue

      result shouldBe ClientListDownloadStatusTransformed.InProgress
    }

    "map a getClientListDownloadStatus of 1 to Succeeded" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = 1)))

      val result = service.getStatus("cred-1").futureValue

      result shouldBe ClientListDownloadStatusTransformed.Succeeded
    }

    "map a getClientListDownloadStatus of 2 to Failed" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = 2)))

      val result = service.getStatus("cred-1").futureValue

      result shouldBe ClientListDownloadStatusTransformed.Failed
    }

    "throw new runtime exception when an unknown getClientListDownloadStatus value is returned" in {
      implicit val hc: HeaderCarrier                                               = HeaderCarrier()
      val (service, clientExchangeProxyConnector, clientListDownloadStatusService) = setupService()

      when(clientListDownloadStatusService.getClientListDownloadStatus(any, any, any)(any))
        .thenReturn(Future.successful(ClientDownloadStatus(status = 50)))

      val exception = service.getStatus("cred-1").failed.futureValue

      exception            shouldBe a[RuntimeException]
      exception.getMessage shouldBe "Unknown status '50' from rds-datacache-proxy"
    }

  }

}
