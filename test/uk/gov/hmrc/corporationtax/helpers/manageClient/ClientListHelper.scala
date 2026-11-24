package uk.gov.hmrc.corporationtax.helpers.manageClient

import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}

trait ClientListHelper {

  val paginatedClients: AllClients = AllClients(
    clientCount = 30,
    clientList = List(
      ClientList(utr = "210000", companyRegNo = Some("2345678"), companyName = "Uniform Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "220000", companyRegNo = Some("2345678"), companyName = "Victor Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "230000", companyRegNo = Some("2345678"), companyName = "Whiskey Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "60000", companyRegNo = Some("2345678"), companyName = "Foxtrot Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "70000", companyRegNo = Some("2345678"), companyName = "Golf Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "130000", companyRegNo = Some("2345678"), companyName = "Mike Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "140000", companyRegNo = Some("2345678"), companyName = "November Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "150000", companyRegNo = Some("2345678"), companyName = "Oscar Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "40000", companyRegNo = Some("2345678"), companyName = "Delta Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "50000", companyRegNo = Some("2345678"), companyName = "Echo Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "160000", companyRegNo = Some("2345678"), companyName = "Papa Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "10000", companyRegNo = Some("1234567"), companyName = "Alpha Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "20000", companyRegNo = Some("2345678"), companyName = "Bravo Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "30000", companyRegNo = Some("2345678"), companyName = "Charlie Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "170000", companyRegNo = Some("2345678"), companyName = "Quebec Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "180000", companyRegNo = Some("2345678"), companyName = "Romeo Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "190000", companyRegNo = Some("2345678"), companyName = "Sierra Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "200000", companyRegNo = Some("2345678"), companyName = "Tango Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "260000", companyRegNo = Some("2345678"), companyName = "Zebra Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "270000", companyRegNo = Some("2345678"), companyName = "Rusty Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "80000", companyRegNo = Some("2345678"), companyName = "Hotel Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "90000", companyRegNo = Some("2345678"), companyName = "India Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "100000", companyRegNo = Some("2345678"), companyName = "Juliett Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "110000", companyRegNo = Some("2345678"), companyName = "Kilo Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "120000", companyRegNo = Some("2345678"), companyName = "Lima Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "280000", companyRegNo = Some("2345678"), companyName = "Milo Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "240000", companyRegNo = Some("2345678"), companyName = "X-Ray Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "250000", companyRegNo = Some("2345678"), companyName = "Yankee Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "290000", companyRegNo = Some("2345678"), companyName = "Daisy Ltd", agentOwnRef = Some("agentOwnRef")),
      ClientList(utr = "300000", companyRegNo = Some("2345678"), companyName = "Willow Ltd", agentOwnRef = Some("agentOwnRef")),
    ),
    clientNameStartChars = List("alpha", "bravo", "charlie")
  )

  def filterPaginatedList(start: Int,
                          count: Int,
                          order: String): AllClients = {
    val lastIndex: Int = start + count

    val clientList = order.toLowerCase() match {
      case "asc"  => this
        .paginatedClients
        .clientList
        .sortBy(item => item.companyName)
        .slice(start, lastIndex)
      case "desc" => this
        .paginatedClients
        .clientList
        .sortBy(item => item.companyName).reverse
        .slice(start, lastIndex)
      case _ => this
        .paginatedClients
        .clientList
        .slice(start, lastIndex)
    }

    AllClients(clientCount = clientList.size, clientList = clientList, clientNameStartChars = this.paginatedClients.clientNameStartChars)
  }
  
}
