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
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.corporationtax.controllers.actions.IdentifierAction
import uk.gov.hmrc.corporationtax.services.manageClient.AllClientsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AllClientsController @Inject() (
  allClientsService: AllClientsService,
  identify: IdentifierAction,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getAllClients(start: Int, count: Int, sort: Int, order: String): Action[AnyContent] = identify.async {
    implicit request =>
      (request.irAgentReference, request.credentialId) match {
        case (Some(agentId), credId) =>
          allClientsService
            .getAllClients(agentId, credId, start, count, sort, order)
            .map { allClients =>
              Ok(Json.toJson(allClients))
            }
            .recover { case ex: Exception =>
              logger.error("Error while retrieving all clients", ex)
              InternalServerError(Json.obj("error" -> "Failed to retrieve all clients"))
            }
        case _                       =>
          logger.error("Error while retrieving irAgentReference and credentialId")
          Future.successful(
            BadRequest(Json.obj("message" -> "irAgentReference and credentialId was not provided in the request"))
          )
      }
  }
}
