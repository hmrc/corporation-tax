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

package uk.gov.hmrc.corporationtax.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.AccountPositionConnector
import uk.gov.hmrc.corporationtax.helpers.{AccountPositionHelper, AccountingPeriodDetailsHelper}
import uk.gov.hmrc.corporationtax.models.AccountPositionResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountPositionServiceSpec
    extends AnyWordSpec
    with Matchers
    with AccountingPeriodDetailsHelper
    with ScalaFutures
    with AccountPositionHelper {

  private trait Fixture {
    val mockAccountPositionConnector: AccountPositionConnector = mock[AccountPositionConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service =
      new AccountPositionService(mockAccountPositionConnector)

  }

  "getAccountPosition returns transformed default record" in new Fixture {
    when(mockAccountPositionConnector.getAccountPosition(any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(defaultRecord))

    val result: AccountPositionResponse = service.getAccountPosition(1L).futureValue

    result shouldBe defaultResponse

    verify(mockAccountPositionConnector).getAccountPosition(1L)(hc)
  }

  "getAccountPosition returns transformed empty record" in new Fixture {
    when(mockAccountPositionConnector.getAccountPosition(any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(emptyRecord))

    val result: AccountPositionResponse = service.getAccountPosition(1L).futureValue

    result shouldBe emptyResponse

    verify(mockAccountPositionConnector).getAccountPosition(1L)(hc)
  }

}
