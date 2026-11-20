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

package uk.gov.hmrc.corporationtax.controllers.manageClient

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.controllers.actions.IdentifierAction
import uk.gov.hmrc.corporationtax.services.manageClient.ClientsByAgentOwnRefService
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ClientsByAgentOwnRefController @Inject() (
  cc: ControllerComponents,
  auth: IdentifierAction,
  service: ClientsByAgentOwnRefService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getClientsByAgentOwnRef(clientId: String, start: Int, count: Int, sort: Int, order: String): Action[AnyContent] =
    auth.async { implicit request =>
      (request.irAgentReference, request.credentialId) match {
        case (Some(irAgentReference), credentialId) =>
          logger.info("Successfully retrieved agentReference and credentialId")
          service
            .getClientsByAgentOwnRef(irAgentReference, credentialId, clientId, start, count, sort, order)
            .map { response =>
              Ok(Json.toJson(response))
            }
            .recover {
              case u: UpstreamErrorResponse =>
                logger.error("Error response from Upstream", u)
                Status(u.statusCode)(Json.obj("message" -> u.message))
              case t: Throwable             =>
                logger.error("Error while retrieving AllClients", t)
                InternalServerError(Json.obj("error" -> "Failed to retrieve AllClients"))
            }
        case _                                      =>
          logger.info("Failed to retrieve agentReference or credentialId")
          Future.successful(
            BadRequest(
              Json.obj(
                "message" -> "Request could not be processed: the IR agent reference and/or credential ID is missing from the authenticated session"
              )
            )
          )
      }
    }
}
