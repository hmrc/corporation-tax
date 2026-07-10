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

package uk.gov.hmrc.corporationtax.connectors

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import com.github.tomakehurst.wiremock.client.WireMock.*
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.models.{Penalties, PenaltyTransaction}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class PenaltiesConnectorISpec extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with ApplicationWithWiremock
  with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val connector: PenaltiesConnector = app.injector.instanceOf[PenaltiesConnector]

  "getPenaltyTransactionList" should {

    def url(taxRef: Long, accPeriod: Long) = s"/corporation-tax/penalty-transactions/$taxRef/$accPeriod"

    "return Penalties list from BE with status code OK" in {

      stubFor(
        get(urlPathEqualTo(url(1L, 5L)))
          //.withRequestBody(equalToJson(Json.stringify(payloadJson), true, true))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |"penaltyTransactions":[
                   |  {"penalty_date":"2005-03-30","type":"F","posting_amount":-10},
                   |  {"penalty_date":"2005-03-30","type":"G","posting_amount":-100}
                   |]}""".stripMargin
              )
          )
      )

      val result = connector.getPenaltyTransactionList(1L, 5L).futureValue

      result mustBe Right(
        Penalties(
          List(
            PenaltyTransaction(
              penaltyDate = LocalDate.of(2025, 5, 1),
              `type` = "F",
              postingAmount = BigDecimal(100.13)
            ),
            PenaltyTransaction(penaltyDate = LocalDate.of(2021, 3, 7), `type` = "G", postingAmount = BigDecimal(27.19))
          )
        )
      )
    }

  }

}