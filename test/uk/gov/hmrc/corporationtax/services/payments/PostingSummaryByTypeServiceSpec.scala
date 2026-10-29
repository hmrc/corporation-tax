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

package uk.gov.hmrc.corporationtax.services.payments

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.payments.PostingSummaryByTypeConnector
import uk.gov.hmrc.corporationtax.helpers.payments.PostingSummaryByTypeHelper
import uk.gov.hmrc.corporationtax.models.payments.PostingSummaryByTypeResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PostingSummaryByTypeServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with PostingSummaryByTypeHelper {

  private trait Setup {
    val mockConnector: PostingSummaryByTypeConnector = mock[PostingSummaryByTypeConnector]
    implicit val hc: HeaderCarrier                   = HeaderCarrier()
    private val cc: ControllerComponents             = stubControllerComponents()
    implicit val ec: ExecutionContext                = cc.executionContext

    val taxRef: Long = 1287892L
    val postingType  = "PYT"

    val service = new PostingSummaryByTypeService(mockConnector)

  }

  "getPostingSummaryByType" should {
    "return a transformed list of PostingSummaryByTypeResponse" in new Setup {
      when(
        mockConnector.getPostingSummaryByType(any[Long], any[String])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsPostingSummaryByTypeResponseMultiple))
      val result: PostingSummaryByTypeResponse = service.getPostingSummaryByType(taxRef, postingType).futureValue

      result shouldBe postingSummaryByTypeResponseMultiple

      verify(mockConnector, times(1)).getPostingSummaryByType(any(), any())(any[HeaderCarrier])

    }

    "return a transformed list of PostingSummaryByTypeResponse when some fields None" in new Setup {
      when(
        mockConnector.getPostingSummaryByType(any[Long], any[String])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsPostingSummaryByTypeResponseNone))
      val result: PostingSummaryByTypeResponse = service.getPostingSummaryByType(taxRef, postingType).futureValue

      result shouldBe postingSummaryByTypeResponseNone

      verify(mockConnector, times(1)).getPostingSummaryByType(any(), any())(any[HeaderCarrier])

    }

    "return an emptyList if the connector returns an empty PostingSummaryByTypeResponse" in new Setup {
      when(
        mockConnector.getPostingSummaryByType(any[Long], any[String])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsPostingSummaryByTypeResponseEmpty))
      val result: PostingSummaryByTypeResponse = service.getPostingSummaryByType(taxRef, postingType).futureValue

      result shouldBe postingSummaryByTypeResponseEmpty

      verify(mockConnector, times(1)).getPostingSummaryByType(any(), any())(any[HeaderCarrier])

    }

    "propagates exceptions from connector" in new Setup {
      val ex = new RuntimeException("Error from Downstream")

      when(
        mockConnector.getPostingSummaryByType(any[Long], any[String])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.failed(ex))

      val result: Exception = intercept[Exception] {
        service.getPostingSummaryByType(taxRef, postingType).futureValue
      }

      verify(mockConnector, times(1)).getPostingSummaryByType(any(), any())(any[HeaderCarrier])

    }
  }

}
