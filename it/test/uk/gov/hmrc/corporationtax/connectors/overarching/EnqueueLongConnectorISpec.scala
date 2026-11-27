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

package uk.gov.hmrc.corporationtax.connectors.overarching

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.*
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata.overarching.EnqueueHelper
import uk.gov.hmrc.http.HeaderCarrier

class EnqueueLongConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with EnqueueHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  private val connector: EnqueueLongConnector = app.injector.instanceOf[EnqueueLongConnector]

  "getEnqueueLong" should {

    def url(messageIdIn: Long, sender: String, queue: String, replyQueue: String, correlationId: String, filter: String, key: String, dataType: Int, numberValue: Int) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/enqueue-long/$messageIdIn/$sender/$queue/$replyQueue/$correlationId/$filter/$key/$dataType/$numberValue"

    "return successful response of all enqueue long" in {
      stubFor(
        get(urlEqualTo(url(12345, "sender1", "queue1", "replyQueue1", "correlationId1", "filter1", "key1", 1, 1010)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""{
                   |    "messageID": 12345
                   |}""".stripMargin

              )
          )
      )

      val result = connector.getEnqueueLong(12345, "sender1", "queue1", "replyQueue1", "correlationId1", "filter1", "key1", 1, 1010).futureValue
      result mustBe enqueue
    }

    "return INTERNAL_ERROR when service failed" in {
      stubFor(
        get(urlEqualTo(url(65789, "sender2", "queue2", "replyQueue2", "correlationId2", "filter2", "key2", 2, 2020)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody(
                s"""{
                   |error" :"Failed to retrieve enqueue long"
                   |}""".stripMargin
              )
          )
      )

      val ex = intercept[Exception] {
        connector.getEnqueueLong(65789, "sender2", "queue2", "replyQueue2", "correlationId2", "filter2", "key2", 2, 2020).futureValue
      }
      ex.getMessage.toLowerCase must include("error")
    }
  }
}
