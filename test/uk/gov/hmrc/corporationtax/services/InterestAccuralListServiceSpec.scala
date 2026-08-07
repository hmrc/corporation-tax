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
import uk.gov.hmrc.corporationtax.connectors.InterestAccuralListConnector
import uk.gov.hmrc.corporationtax.helpers.InterestAccuralListHelper
import uk.gov.hmrc.corporationtax.models.InterestAccuralList
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class InterestAccuralListServiceSpec
    extends AnyWordSpec
    with Matchers
    with InterestAccuralListHelper
    with ScalaFutures {

  private trait Fixture {
    val mockAccuralInterestListConnector: InterestAccuralListConnector = mock[InterestAccuralListConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service = new InterestAccuralListService(mockAccuralInterestListConnector)
  }

  "getInterestAccuralList returns Interest Accural List from connector" in new Fixture {

    when(mockAccuralInterestListConnector.getInterestAccuralList(any[Long], any[Long], any[String])(any[HeaderCarrier]))
      .thenReturn(Future.successful(interestAccuralList))

    val result: InterestAccuralList = service.getInterestAccuralList(1L, 1L, "IDB").futureValue

    result shouldBe interestAccuralListTransformed

    verify(mockAccuralInterestListConnector).getInterestAccuralList(1L, 1L, "IDB")(hc)
  }

  // TODO: extend testing to cover CTPF scenarios

}
