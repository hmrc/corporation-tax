/*
 * Copyright 2025 HM Revenue & Customs
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
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers.mustBe
import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.AsynchronousProcessWaitTime


class WaitTimeXmlMapperSpec extends SpecBase with OptionValues {

  "WaitTimeXmlMapper.parse" should {

    "return Right with parsed AsynchronousProcessWaitTime for valid XML" in {
      val xml =
        """<Root>
          |  <AsynchronousProcessWaitTime browserInterval="1000">
          |    <BusinessServiceInterval>100</BusinessServiceInterval>
          |    <BusinessServiceInterval>200</BusinessServiceInterval>
          |    <BusinessServiceInterval>300</BusinessServiceInterval>
          |  </AsynchronousProcessWaitTime>
          |</Root>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result.isRight mustBe true

      val waitTime = result.toOption.value
      waitTime mustBe AsynchronousProcessWaitTime(
        browserIntervalMs = 1000L,
        businessIntervalsMs = List(100L, 200L, 300L)
      )
    }

    "handle whitespace and still parse correctly" in {
      val xml =
        """<AsynchronousProcessWaitTime browserInterval="  500  ">
          |  <BusinessServiceInterval> 10 </BusinessServiceInterval>
          |  <BusinessServiceInterval>  20</BusinessServiceInterval>
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result.isRight mustBe true

      val waitTime = result.toOption.value
      waitTime.browserIntervalMs mustBe 500L
      waitTime.businessIntervalsMs mustBe List(10L, 20L)
    }

    "return Left('invalid XML document') when XML is not well-formed" in {
      val xml = "<AsynchronousProcessWaitTime"

      val result = WaitTimeXmlMapper.parse(xml)

      result mustBe Left("invalid XML document")
    }

    "return Left('missing AsynchronousProcessWaitTime element') when element is absent" in {
      val xml =
        """<Root>
          |  <SomethingElse browserInterval="1000">
          |    <BusinessServiceInterval>100</BusinessServiceInterval>
          |  </SomethingElse>
          |</Root>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result mustBe Left("missing AsynchronousProcessWaitTime element")
    }

    "return Left('missing browserInterval attribute') when attribute is not present" in {
      val xml =
        """<AsynchronousProcessWaitTime>
          |  <BusinessServiceInterval>100</BusinessServiceInterval>
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result mustBe Left("missing browserInterval attribute")
    }

    "return Left with invalid browserInterval message when browserInterval is not a number" in {
      val xml =
        """<AsynchronousProcessWaitTime browserInterval="abc">
          |  <BusinessServiceInterval>100</BusinessServiceInterval>
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result mustBe Left("invalid browserInterval 'abc'")
    }

    "return Left with invalid BusinessServiceInterval message when any business interval is not a number" in {
      val xml =
        """<AsynchronousProcessWaitTime browserInterval="1000">
          |  <BusinessServiceInterval>100</BusinessServiceInterval>
          |  <BusinessServiceInterval>test</BusinessServiceInterval>
          |  <BusinessServiceInterval>300</BusinessServiceInterval>
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result mustBe Left("invalid BusinessServiceInterval 'test'")
    }

    "return Right with empty business list when there are no BusinessServiceInterval elements" in {
      val xml =
        """<AsynchronousProcessWaitTime browserInterval="1000">
          |</AsynchronousProcessWaitTime>
          |""".stripMargin

      val result = WaitTimeXmlMapper.parse(xml)

      result.isRight mustBe true

      val waitTime = result.toOption.value
      waitTime.browserIntervalMs mustBe 1000L
      waitTime.businessIntervalsMs mustBe Nil
    }
  }
}
