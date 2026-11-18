package uk.gov.hmrc.corporationtax.helpers.manageClient

import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}

trait ClientListHelper {

  val paginatedClients: AllClients = AllClients(
    clientCount = 30,
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
      ClientList(utr = "50000", companyRegNo = "2345678", companyName = "Echo Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "160000", companyRegNo = "2345678", companyName = "Papa Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "10000", companyRegNo = "1234567", companyName = "Alpha Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "20000", companyRegNo = "2345678", companyName = "Bravo Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "30000", companyRegNo = "2345678", companyName = "Charlie Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "170000", companyRegNo = "2345678", companyName = "Quebec Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "180000", companyRegNo = "2345678", companyName = "Romeo Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "190000", companyRegNo = "2345678", companyName = "Sierra Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "200000", companyRegNo = "2345678", companyName = "Tango Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "260000", companyRegNo = "2345678", companyName = "Zebra Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "270000", companyRegNo = "2345678", companyName = "Rusty Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "80000", companyRegNo = "2345678", companyName = "Hotel Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "90000", companyRegNo = "2345678", companyName = "India Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "100000", companyRegNo = "2345678", companyName = "Juliett Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "110000", companyRegNo = "2345678", companyName = "Kilo Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "120000", companyRegNo = "2345678", companyName = "Lima Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "280000", companyRegNo = "2345678", companyName = "Milo Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "240000", companyRegNo = "2345678", companyName = "X-Ray Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "250000", companyRegNo = "2345678", companyName = "Yankee Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "290000", companyRegNo = "2345678", companyName = "Daisy Ltd", agentOwnRef = "agentOwnRef"),
      ClientList(utr = "300000", companyRegNo = "2345678", companyName = "Willow Ltd", agentOwnRef = "agentOwnRef"),
    ),
    clientNameStartChars = List("alpha", "bravo", "charlie")
  )
  
}
