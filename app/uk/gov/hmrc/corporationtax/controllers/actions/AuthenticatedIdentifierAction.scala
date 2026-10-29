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

package uk.gov.hmrc.corporationtax.controllers.actions

import com.google.inject.Inject
import play.api.Logging
import play.api.mvc.*
import play.api.mvc.Results.Forbidden
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, ~}
import uk.gov.hmrc.corporationtax.models.auth.IdentifierRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with Logging {

  private val orgEnrollment: String                  = "IR-CT"
  private val agentEnrollment: String                = "IR-CT-AGENT"
  private val permittedEnrollmentStates: Set[String] = Set("activated", "notyetactivated")

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    given hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised()
      .retrieve(
        Retrievals.internalId and
          Retrievals.allEnrolments and
          Retrievals.affinityGroup and
          Retrievals.credentialRole and
          Retrievals.credentials
      ) {
        case Some(_) ~ Enrolments(enrolments) ~ Some(Organisation) ~ Some(User) ~ Some(Credentials(credId, _))
            if enrolments
              .find(_.key == orgEnrollment)
              .exists(s => permittedEnrollmentStates.contains(s.state.toLowerCase)) =>
          block(IdentifierRequest(request, credId, Organisation))

        case Some(_) ~ Enrolments(enrolments) ~ Some(Agent) ~ Some(User) ~ Some(Credentials(credId, _))
            if enrolments
              .find(_.key == agentEnrollment)
              .exists(e => permittedEnrollmentStates.contains(e.state.toLowerCase())) =>
          block(IdentifierRequest(request, credId, Agent))

        case _ ~ _ ~ _ ~ _ ~ _ =>
          throw InternalError("Authentication error: expected enrollments and/or affinity group not found")

      }
      .recoverWith { case ex =>
        logger.error(
          s"[AuthenticatedIdentifierAction][authorised] - Authentication failed: ${ex.getCause}-${ex.getMessage}"
        )
        Future.successful(Forbidden)
      }
  }

}
