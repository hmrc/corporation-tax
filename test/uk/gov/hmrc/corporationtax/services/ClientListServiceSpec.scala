package uk.gov.hmrc.corporationtax.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.corporationtax.services.clientExchangeProxy.ClientExchangeProxyService
import uk.gov.hmrc.corporationtax.services.manageClient.AllClientsService
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.corporationtax.models.ClientDownloadStatus
import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}
import uk.gov.hmrc.corporationtax.models.manageClient.{ClientsError, FailedRetrieveAllClients}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.helpers.manageClient.ClientListHelper

import scala.concurrent.{ExecutionContext, Future}

class ClientListServiceSpec extends AnyWordSpec with ScalaFutures with MockitoSugar with ClientListHelper {
  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  private trait BaseSetup {
    val irAfgentID: String                                         = "1000000"
    val credentialId: String                                       = "1000000"
    val mockClientExchangeProxyService: ClientExchangeProxyService = mock[ClientExchangeProxyService]
    val mockClientService: AllClientsService                       = mock[AllClientsService]
    val successfulDownload: ClientDownloadStatus                   = ClientDownloadStatus(1)

    val service = new ClientListService(mockClientExchangeProxyService, mockClientService)
  }

  "ClientListService.getClientClist" should {

    "should retrieve the first 10 clients, in ASC ascending order" in new BaseSetup {
      val pStart: Int   = 0
      val pCount: Int   = 10
      val sort: Int     = 0
      val order: String = "ASC"
      when(mockClientExchangeProxyService.getStatus(any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(successfulDownload))
      when(
        mockClientService.getAllClients(any[String], any[String], any[Int], any[Int], any[Int], any[String])(
          any[HeaderCarrier]
        )
      ).thenReturn(Future.successful(paginatedClients))

      val result: Either[ClientsError, AllClients] =
        service.getClientList(irAfgentID, credentialId, pStart, pCount, sort, order).futureValue

      result.isRight shouldBe true

      result shouldBe Right(
        AllClients(
          clientCount = 10,
          clientList = List(
            ClientList(
              utr = "10000",
              companyRegNo = Some("1234567"),
              companyName = "Alpha Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "20000",
              companyRegNo = Some("2345678"),
              companyName = "Bravo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "30000",
              companyRegNo = Some("2345678"),
              companyName = "Charlie Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "290000",
              companyRegNo = Some("2345678"),
              companyName = "Daisy Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "40000",
              companyRegNo = Some("2345678"),
              companyName = "Delta Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "50000",
              companyRegNo = Some("2345678"),
              companyName = "Echo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "60000",
              companyRegNo = Some("2345678"),
              companyName = "Foxtrot Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "70000",
              companyRegNo = Some("2345678"),
              companyName = "Golf Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "80000",
              companyRegNo = Some("2345678"),
              companyName = "Hotel Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "90000",
              companyRegNo = Some("2345678"),
              companyName = "India Ltd",
              agentOwnRef = Some("agentOwnRef")
            )
          ),
          clientNameStartChars = List("alpha", "bravo", "charlie")
        )
      )

    }

    "should retrieve the first 10 clients, in DESC ascending order" in new BaseSetup {
      val pStart: Int                              = 0
      val pCount: Int                              = 10
      val sort: Int                                = 0
      val order: String                            = "DESC"
      when(mockClientExchangeProxyService.getStatus(any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(successfulDownload))
      when(
        mockClientService.getAllClients(any[String], any[String], any[Int], any[Int], any[Int], any[String])(
          any[HeaderCarrier]
        )
      ).thenReturn(Future.successful(paginatedClients))

      val result: Either[ClientsError, AllClients] =
        service.getClientList(irAfgentID, credentialId, pStart, pCount, sort, order).futureValue

      result.isRight shouldBe true

      result shouldBe Right(
        AllClients(
          clientCount = 10,
          clientList = List(
            ClientList(
              utr = "260000",
              companyRegNo = Some("2345678"),
              companyName = "Zebra Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "250000",
              companyRegNo = Some("2345678"),
              companyName = "Yankee Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "240000",
              companyRegNo = Some("2345678"),
              companyName = "X-Ray Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "300000",
              companyRegNo = Some("2345678"),
              companyName = "Willow Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "230000",
              companyRegNo = Some("2345678"),
              companyName = "Whiskey Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "220000",
              companyRegNo = Some("2345678"),
              companyName = "Victor Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "210000",
              companyRegNo = Some("2345678"),
              companyName = "Uniform Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "200000",
              companyRegNo = Some("2345678"),
              companyName = "Tango Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "190000",
              companyRegNo = Some("2345678"),
              companyName = "Sierra Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "270000",
              companyRegNo = Some("2345678"),
              companyName = "Rusty Ltd",
              agentOwnRef = Some("agentOwnRef")
            )
          ),
          clientNameStartChars = List("alpha", "bravo", "charlie")
        )
      )
    }

    "should retrieve the next 10 clients, in ASC ascending order" in new BaseSetup {
      val pStart: Int                              = 10
      val pCount: Int                              = 10
      val sort: Int                                = 0
      val order: String                            = "ASC"
      when(mockClientExchangeProxyService.getStatus(any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(successfulDownload))
      when(
        mockClientService.getAllClients(any[String], any[String], any[Int], any[Int], any[Int], any[String])(
          any[HeaderCarrier]
        )
      ).thenReturn(Future.successful(paginatedClients))

      val result: Either[ClientsError, AllClients] =
        service.getClientList(irAfgentID, credentialId, pStart, pCount, sort, order).futureValue

      result.isRight shouldBe true

      result shouldBe Right(
        AllClients(
          clientCount = 10,
          clientList = List(
            ClientList(
              utr = "100000",
              companyRegNo = Some("2345678"),
              companyName = "Juliett Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "110000",
              companyRegNo = Some("2345678"),
              companyName = "Kilo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "120000",
              companyRegNo = Some("2345678"),
              companyName = "Lima Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "130000",
              companyRegNo = Some("2345678"),
              companyName = "Mike Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "280000",
              companyRegNo = Some("2345678"),
              companyName = "Milo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "140000",
              companyRegNo = Some("2345678"),
              companyName = "November Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "150000",
              companyRegNo = Some("2345678"),
              companyName = "Oscar Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "160000",
              companyRegNo = Some("2345678"),
              companyName = "Papa Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "170000",
              companyRegNo = Some("2345678"),
              companyName = "Quebec Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "180000",
              companyRegNo = Some("2345678"),
              companyName = "Romeo Ltd",
              agentOwnRef = Some("agentOwnRef")
            )
          ),
          clientNameStartChars = List("alpha", "bravo", "charlie")
        )
      )
    }

    "should retrieve the next 10 clients, in DESC ascending order" in new BaseSetup {
      val pStart: Int                              = 10
      val pCount: Int                              = 10
      val sort: Int                                = 0
      val order: String                            = "DESC"
      when(mockClientExchangeProxyService.getStatus(any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(successfulDownload))
      when(
        mockClientService.getAllClients(any[String], any[String], any[Int], any[Int], any[Int], any[String])(
          any[HeaderCarrier]
        )
      ).thenReturn(Future.successful(paginatedClients))

      val result: Either[ClientsError, AllClients] =
        service.getClientList(irAfgentID, credentialId, pStart, pCount, sort, order).futureValue

      val clientListResult: AllClients = result match {
        case Right(allClients)  => allClients
        case Left(clientsError) => fail("Did not retrieve All Clients")
      }

      result.isRight shouldBe true

      result shouldBe Right(
        AllClients(
          clientCount = 10,
          clientList = List(
            ClientList(
              utr = "180000",
              companyRegNo = Some("2345678"),
              companyName = "Romeo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "170000",
              companyRegNo = Some("2345678"),
              companyName = "Quebec Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "160000",
              companyRegNo = Some("2345678"),
              companyName = "Papa Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "150000",
              companyRegNo = Some("2345678"),
              companyName = "Oscar Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "140000",
              companyRegNo = Some("2345678"),
              companyName = "November Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "280000",
              companyRegNo = Some("2345678"),
              companyName = "Milo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "130000",
              companyRegNo = Some("2345678"),
              companyName = "Mike Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "120000",
              companyRegNo = Some("2345678"),
              companyName = "Lima Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "110000",
              companyRegNo = Some("2345678"),
              companyName = "Kilo Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "100000",
              companyRegNo = Some("2345678"),
              companyName = "Juliett Ltd",
              agentOwnRef = Some("agentOwnRef")
            )
          ),
          clientNameStartChars = List("alpha", "bravo", "charlie")
        )
      )
    }

    "should retrieve the first 10 clients, in no particular order" in new BaseSetup {
      val pStart: Int                              = 0
      val pCount: Int                              = 10
      val sort: Int                                = 0
      val order: String                            = ""
      when(mockClientExchangeProxyService.getStatus(any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(successfulDownload))
      when(
        mockClientService.getAllClients(any[String], any[String], any[Int], any[Int], any[Int], any[String])(
          any[HeaderCarrier]
        )
      ).thenReturn(Future.successful(paginatedClients))

      val result: Either[ClientsError, AllClients] =
        service.getClientList(irAfgentID, credentialId, pStart, pCount, sort, order).futureValue

      result.isRight shouldBe true

      result shouldBe Right(
        AllClients(
          clientCount = 10,
          clientList = List(
            ClientList(
              utr = "210000",
              companyRegNo = Some("2345678"),
              companyName = "Uniform Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "220000",
              companyRegNo = Some("2345678"),
              companyName = "Victor Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "230000",
              companyRegNo = Some("2345678"),
              companyName = "Whiskey Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "60000",
              companyRegNo = Some("2345678"),
              companyName = "Foxtrot Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "70000",
              companyRegNo = Some("2345678"),
              companyName = "Golf Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "130000",
              companyRegNo = Some("2345678"),
              companyName = "Mike Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "140000",
              companyRegNo = Some("2345678"),
              companyName = "November Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "150000",
              companyRegNo = Some("2345678"),
              companyName = "Oscar Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "40000",
              companyRegNo = Some("2345678"),
              companyName = "Delta Ltd",
              agentOwnRef = Some("agentOwnRef")
            ),
            ClientList(
              utr = "50000",
              companyRegNo = Some("2345678"),
              companyName = "Echo Ltd",
              agentOwnRef = Some("agentOwnRef")
            )
          ),
          clientNameStartChars = List("alpha", "bravo", "charlie")
        )
      )
    }

    "should retrieve an FailedRetrieveAllClients error" in new BaseSetup {
      val pStart: Int                              = -1
      val pCount: Int                              = -10
      val sort: Int                                = 0
      val order: String                            = "ASC"
      when(mockClientExchangeProxyService.getStatus(any[String])(any[HeaderCarrier]))
        .thenReturn(Future.successful(successfulDownload))
      when(
        mockClientService.getAllClients(any[String], any[String], any[Int], any[Int], any[Int], any[String])(
          any[HeaderCarrier]
        )
      ).thenReturn(Future.successful(paginatedClients))

      val result: Either[ClientsError, AllClients] =
        service.getClientList(irAfgentID, credentialId, pStart, pCount, sort, order).futureValue

      result.isLeft shouldBe true

      result shouldBe Left(FailedRetrieveAllClients)

    }

  }

}
