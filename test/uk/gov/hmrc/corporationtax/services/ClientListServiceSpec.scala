package uk.gov.hmrc.corporationtax.services

//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
//import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatestplus.mockito.MockitoSugar
//import play.api.mvc.ControllerComponents
//import play.api.test.Helpers.stubControllerComponents
//import uk.gov.hmrc.corporationtax.helpers.manageClient.ClientListHelper
import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}

class ClientListServiceSpec
  extends AnyWordSpec
    with ScalaFutures
    with MockitoSugar {

  private trait BaseSetup {
    val irAfgentID: String                  = "1000000"
    val credentialId: String                = "1000000"
    val service                             = new ClientListService()
  }

  "ClientListService.getClientClist" should {

    "should retrieve the first 10 clients, in ASC ascending order" in new BaseSetup {
      val pStart: Int = 0
      val pCount: Int = 10
      val sortOrder = "ASC"
      val result: AllClients = service.getClientList(irAfgentID, credentialId, pStart, pCount, sortOrder).get

      result.clientCount shouldBe 10
      result.clientList.size shouldBe 10

      result shouldBe AllClients(
        clientCount = 10,
        clientList = List(
          ClientList(utr = "10000", companyRegNo = "1234567", companyName = "Alpha Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "20000", companyRegNo = "2345678", companyName = "Bravo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "30000", companyRegNo = "2345678", companyName = "Charlie Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "290000", companyRegNo = "2345678", companyName = "Daisy Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "40000", companyRegNo = "2345678", companyName = "Delta Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "50000", companyRegNo = "2345678", companyName = "Echo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "60000", companyRegNo = "2345678", companyName = "Foxtrot Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "70000", companyRegNo = "2345678", companyName = "Golf Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "80000", companyRegNo = "2345678", companyName = "Hotel Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "90000", companyRegNo = "2345678", companyName = "India Ltd", agentOwnRef = "agentOwnRef")
        ),
        clientNameStartChars = List("alpha", "bravo", "charlie")
      )
    }

    "should retrieve the first 10 clients, in DESC ascending order" in new BaseSetup {
      val pStart: Int = 0
      val pCount: Int = 10
      val sortOrder = "DESC"
      val result: AllClients = service.getClientList((irAfgentID, credentialId, pStart, pCount, sortOrder).get

      result.clientCount shouldBe 10
      result.clientList.size shouldBe 10

      result shouldBe AllClients(
        clientCount = 10,
        clientList = List(
          ClientList(utr = "260000", companyRegNo = "2345678", companyName = "Zebra Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "250000", companyRegNo = "2345678", companyName = "Yankee Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "240000", companyRegNo = "2345678", companyName = "X-Ray Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "300000", companyRegNo = "2345678", companyName = "Willow Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "230000", companyRegNo = "2345678", companyName = "Whiskey Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "220000", companyRegNo = "2345678", companyName = "Victor Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "210000", companyRegNo = "2345678", companyName = "Uniform Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "200000", companyRegNo = "2345678", companyName = "Tango Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "190000", companyRegNo = "2345678", companyName = "Sierra Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "270000", companyRegNo = "2345678", companyName = "Rusty Ltd", agentOwnRef = "agentOwnRef")
        ),
        clientNameStartChars = List("alpha", "bravo", "charlie")
      )
    }

    "should retrieve the next 10 clients, in ASC ascending order" in new BaseSetup {
      val pStart: Int = 10
      val pCount: Int = 10
      val sortOrder = "ASC"
      val result: AllClients = service.getClientList((irAfgentID, credentialId, pStart, pCount, sortOrder).get

      result.clientCount shouldBe 10
      result.clientList.size shouldBe 10

      result shouldBe AllClients(
        clientCount = 10,
        clientList = List(
          ClientList(utr = "100000", companyRegNo = "2345678", companyName = "Juliett Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "110000", companyRegNo = "2345678", companyName = "Kilo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "120000", companyRegNo = "2345678", companyName = "Lima Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "130000", companyRegNo = "2345678", companyName = "Mike Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "280000", companyRegNo = "2345678", companyName = "Milo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "140000", companyRegNo = "2345678", companyName = "November Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "150000", companyRegNo = "2345678", companyName = "Oscar Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "160000", companyRegNo = "2345678", companyName = "Papa Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "170000", companyRegNo = "2345678", companyName = "Quebec Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "180000", companyRegNo = "2345678", companyName = "Romeo Ltd", agentOwnRef = "agentOwnRef")
        ),
        clientNameStartChars = List("alpha", "bravo", "charlie")
      )
    }

    "should retrieve the next 10 clients, in DESC ascending order" in new BaseSetup {
      val pStart: Int = 10
      val pCount: Int = 10
      val sortOrder = "DESC"
      val result: AllClients = service.getClientList((irAfgentID, credentialId, pStart, pCount, sortOrder).get

      result.clientCount shouldBe 10
      result.clientList.size shouldBe 10

      result shouldBe AllClients(
        clientCount = 10,
        clientList = List(
          ClientList(utr = "180000", companyRegNo = "2345678", companyName = "Romeo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "170000", companyRegNo = "2345678", companyName = "Quebec Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "160000", companyRegNo = "2345678", companyName = "Papa Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "150000", companyRegNo = "2345678", companyName = "Oscar Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "140000", companyRegNo = "2345678", companyName = "November Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "280000", companyRegNo = "2345678", companyName = "Milo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "130000", companyRegNo = "2345678", companyName = "Mike Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "120000", companyRegNo = "2345678", companyName = "Lima Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "110000", companyRegNo = "2345678", companyName = "Kilo Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "100000", companyRegNo = "2345678", companyName = "Juliett Ltd", agentOwnRef = "agentOwnRef")
        ),
        clientNameStartChars = List("alpha", "bravo", "charlie")
      )
    }

    "should retrieve the first 10 clients, in no particular order" in new BaseSetup {
      val pStart: Int = 0
      val pCount: Int = 10
      val sortOrder = ""
      val result: AllClients = service.getClientList((irAfgentID, credentialId, pStart, pCount, sortOrder).get

      result.clientCount shouldBe 10
      result.clientList.size shouldBe 10

      result shouldBe AllClients(
        clientCount = 10,
        clientList = List(
          ClientList(utr = "210000", companyRegNo = "2345678", companyName = "Uniform Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "220000", companyRegNo = "2345678", companyName = "Victor Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "230000", companyRegNo = "2345678", companyName = "Whiskey Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "60000", companyRegNo = "2345678", companyName = "Foxtrot Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "70000", companyRegNo = "2345678", companyName = "Golf Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "130000", companyRegNo = "2345678", companyName = "Mike Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "140000", companyRegNo = "2345678", companyName = "November Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "150000", companyRegNo = "2345678", companyName = "Oscar Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "40000", companyRegNo = "2345678", companyName = "Delta Ltd", agentOwnRef = "agentOwnRef"),
          ClientList(utr = "50000", companyRegNo = "2345678", companyName = "Echo Ltd", agentOwnRef = "agentOwnRef")
        ),
        clientNameStartChars = List("alpha", "bravo", "charlie")
      )
    }

  }

}
