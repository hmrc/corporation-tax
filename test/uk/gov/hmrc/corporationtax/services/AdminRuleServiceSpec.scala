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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.AdminRuleRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.AdminRuleHelper
import uk.gov.hmrc.corporationtax.models.AdminRule
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AdminRuleServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar with AdminRuleHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: AdminRuleRdsProxyConnector = mock[AdminRuleRdsProxyConnector]
    val service                             = new AdminRuleService(mockRds)
    val adminRuleKey: String                = "INST-PERIOD"
  }

  "AdminRuleService.getAdminRule" should {

    "delegate to connector and successfully return AdminRule" in new BaseSetup {
      when(mockRds.getAdminRule(eqTo(adminRuleKey))(any[HeaderCarrier]))
        .thenReturn(Future.successful(example2adminRule))

      val result: AdminRule = service.getAdminRule(adminRuleKey).futureValue

      result shouldBe example2adminRule

      verify(mockRds).getAdminRule(adminRuleKey)

      verify(mockRds, times(1)).getAdminRule(adminRuleKey)

    }

    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getAdminRule(eqTo(adminRuleKey))(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getAdminRule(adminRuleKey).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getAdminRule(adminRuleKey)

    }

  }

}
