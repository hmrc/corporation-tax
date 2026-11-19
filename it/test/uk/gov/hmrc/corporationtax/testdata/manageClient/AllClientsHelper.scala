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

package uk.gov.hmrc.corporationtax.testdata.manageClient

import uk.gov.hmrc.corporationtax.models.manageClient.{AllClients, ClientList}

trait AllClientsHelper {


  val allClients1: AllClients = AllClients(
    clientCount = 3,
    clientList = List(
      ClientList(
        utr = "1234567890",
        companyRegNo = Some("01234567"),
        companyName = "ABC Limited",
        agentOwnRef = Some("AGENT001")
      ),
      ClientList(
        utr = "9876543210",
        companyRegNo = None,
        companyName = "XYZ Solutions Ltd",
        agentOwnRef = Some("AGENT002")
      ),
      ClientList(
        utr = "5678901234",
        companyRegNo = Some("09876543"),
        companyName = "Global Technologies Ltd",
        agentOwnRef = None
      )
    ),
    clientNameStartChars = List("A", "G", "X")
  )

  val emptyAllClients: AllClients = AllClients(
    clientCount = 0,
    clientList = List.empty[ClientList],
    clientNameStartChars = List.empty[String]
  )

  val allClients: AllClients = AllClients(
    clientCount = 100,
    clientList = List(
      ClientList(
        utr          = "12345",
        companyRegNo = Some("01234A"),
        companyName  = "ABC Limited",
        agentOwnRef  = Some("ABC10")
      )
    ),
    clientNameStartChars = List(
      "A"
    )
  )

  val multipleClientListAllClients: AllClients = AllClients(
    clientCount = 100,
    clientList = List(
      ClientList(
        utr          = "12345",
        companyRegNo = Some("01234A"),
        companyName  = "ABC Limited",
        agentOwnRef  = Some("ABC10")
      ),
      ClientList(
        utr          = "67890",
        companyRegNo = Some("6789B"),
        companyName  = "DEF Limited",
        agentOwnRef  = Some("DEF10")
      )
    ),
    clientNameStartChars = List(
      "D"
    )
  )

  val multipleItemsClientNameAllClients: AllClients = AllClients(
    clientCount = 100,
    clientList = List(
      ClientList(
        utr          = "111213",
        companyRegNo = Some("111213C"),
        companyName  = "GHI Limited",
        agentOwnRef  = Some("GHI10")
      )
    ),
    clientNameStartChars = List(
      "G",
      "H",
      "I"
    )
  )

  def getParamCombo(irAgentId: String, credentialId: String, start: Int, count: Int, sort: Int, order: String): AllClients = {
    (start, count, sort, order) match {
      case (0, 30, 1, "DESC") => allClients
      case (0, 30, 2, "ASC") => multipleItemsClientNameAllClients
      case (0, 30, 3, "DESC") => multipleClientListAllClients
      case _ => throw new RuntimeException("Downstream error")
    }
  }
}
