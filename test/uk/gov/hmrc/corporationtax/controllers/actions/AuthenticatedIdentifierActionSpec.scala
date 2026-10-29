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

package uk.gov.hmrc.corporationtax.controllers.actions

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.http.Status.{FORBIDDEN, OK}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.*
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, running, status, stubControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, ~}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.corporationtax.controllers.actions.TestAuthRetrievals.Ops

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierActionSpec extends AnyWordSpec with Matchers {

  type RetrievalsType = Option[String] ~ Enrolments ~ Option[AffinityGroup] ~ Option[CredentialRole] ~
    Option[Credentials]

  trait Fixture {
    val cc: ControllerComponents      = stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext

    val id: String = UUID.randomUUID().toString

    val testUtr: String = "testUTR"

    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    val application: Application = new GuiceApplicationBuilder()
      .overrides(
        bind[AuthConnector].toInstance(mockAuthConnector)
      )
      .build()

    val bodyParsers: BodyParsers.Default = application.injector.instanceOf[BodyParsers.Default]

    val testCredentials: Credentials = Credentials(id, "GovernmentGateway")

    val emptyEnrolments: Enrolments = Enrolments(Set.empty)

    val orgActiveEnrollment: Enrolment = Enrolment(
      "IR-CT",
      Seq(
        EnrolmentIdentifier("UTR", testUtr)
      ),
      "activated",
      None
    )

    val agentActiveEnrollment: Enrolment = Enrolment(
      "IR-CT-AGENT",
      Seq(
        EnrolmentIdentifier("UTR", testUtr)
      ),
      "activated",
      None
    )

    val agentNotActiveEnrollment: Enrolment = Enrolment(
      "IR-CT-AGENT",
      Seq(
        EnrolmentIdentifier("UTR", testUtr)
      ),
      "inactivated",
      None
    )

    val agentNotYetActiveEnrollment: Enrolment = Enrolment(
      "IR-CT-AGENT",
      Seq(
        EnrolmentIdentifier("UTR", testUtr)
      ),
      "notyetactivated",
      None
    )

    val orgActiveNotActiveEnrollment: Enrolment = Enrolment(
      "IR-CT",
      Seq(
        EnrolmentIdentifier("utr", testUtr)
      ),
      "inactivated",
      None
    )

    val orgActiveNotYetActiveEnrollment: Enrolment = Enrolment(
      "IR-CT",
      Seq(
        EnrolmentIdentifier("UTR", testUtr)
      ),
      "notyetactivated",
      None
    )

    val orgEnrollments: Enrolments             = Enrolments(Set(orgActiveEnrollment))
    val orgEnrollmentsNotActive: Enrolments    = Enrolments(Set(orgActiveNotActiveEnrollment))
    val orgEnrollmentsNotYetActive: Enrolments = Enrolments(Set(orgActiveNotYetActiveEnrollment))

    val agentEnrollments: Enrolments             = Enrolments(Set(agentActiveEnrollment))
    val agentEnrollmentsNotActive: Enrolments    = Enrolments(Set(agentNotActiveEnrollment))
    val agentEnrollmentsNotYetActive: Enrolments = Enrolments(Set(agentNotYetActiveEnrollment))
  }

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction(_ => Results.Ok)
  }

  "Request contains expected:: enrollments | affinityGroup | credentialRole " should {
    "must process request with OrgEnrollments :: return 200:OK " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(
          Future.successful(Some(id) ~ orgEnrollments ~ Some(Organisation) ~ Some(User) ~ Some(testCredentials))
        )

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe OK
      }
    }
    "must process request with AgentEnrollments :: return 200:OK " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(Future.successful(Some(id) ~ agentEnrollments ~ Some(Agent) ~ Some(User) ~ Some(testCredentials)))

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe OK
      }
    }

    "must NOT process request with inactive AgentEnrollments :: return 403:Forbidden " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(
          Future.successful(Some(id) ~ agentEnrollmentsNotActive ~ Some(Agent) ~ Some(User) ~ Some(testCredentials))
        )

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe FORBIDDEN
      }
    }

    "must process request with NOT YET ACTIVE AgentEnrollments :: return 200:OK " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(
          Future.successful(
            Some(id) ~ agentEnrollmentsNotYetActive ~ Some(Agent) ~ Some(User) ~ Some(testCredentials)
          )
        )

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe OK
      }
    }

    "must NOT process request with inactive OrgEnrollments :: return 403:Forbidden " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(
          Future.successful(
            Some(id) ~ orgEnrollmentsNotActive ~ Some(Organisation) ~ Some(User) ~ Some(testCredentials)
          )
        )

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe FORBIDDEN
      }
    }

    "must NOT process request with OrgEnrollments and role Agent:: return 403:Forbidden " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(Future.successful(Some(id) ~ orgEnrollments ~ Some(Agent) ~ Some(User) ~ Some(testCredentials)))

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe FORBIDDEN
      }
    }

    "must process request with NOT YET ACTIVE OrgEnrollments:: return 200:OK " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(
          Future.successful(
            Some(id) ~ orgEnrollmentsNotYetActive ~ Some(Organisation) ~ Some(User) ~ Some(testCredentials)
          )
        )

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe OK
      }
    }

    "must NOT process request with AgentEnrollments and role Agent:: return 403:Forbidden " in new Fixture {
      when(mockAuthConnector.authorise[RetrievalsType](any(), any())(any(), any()))
        .thenReturn(
          Future.successful(Some(id) ~ agentEnrollments ~ Some(Organisation) ~ Some(User) ~ Some(testCredentials))
        )

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe FORBIDDEN
      }
    }

  }

}
