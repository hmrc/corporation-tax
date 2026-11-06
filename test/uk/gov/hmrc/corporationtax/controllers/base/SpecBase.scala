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

package uk.gov.hmrc.corporationtax.controllers.base

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, OptionValues, TestSuite, TryValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.{BaseOneAppPerSuite, FakeApplicationFactory}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents, PlayBodyParsers}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.controllers.actions.FakeIdentifierAction

import scala.concurrent.ExecutionContext

trait SpecBase
    extends AnyWordSpec
    with Matchers
    with TryValues
    with DefaultAwaitTimeout
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with BeforeAndAfterEach
    with TestSuite
    with FakeApplicationFactory
    with BaseOneAppPerSuite {

  def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .build()

  val cc: ControllerComponents                         = stubControllerComponents()
  implicit val ec: ExecutionContext                    = cc.executionContext
  val bodyParsers: PlayBodyParsers                     = app.injector.instanceOf[PlayBodyParsers]
  val fakeIdentifierAction                             = new FakeIdentifierAction(bodyParsers)
  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

}
