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

package uk.gov.hmrc.corporationtax.controllers

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.connectors.GroupPaymentsConnector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GroupPaymentsController @Inject()(
                                         cc: ControllerComponents,
                                         connector: GroupPaymentsConnector
                                       )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getGroupSummary(gpaUTR: Long, nomCompanyUTR: Long): Action[AnyContent] = Action.async { implicit request =>
    connector
      .getGroupSummary(gpaUTR, nomCompanyUTR)
      .map {
        case Some(gpPayment) =>
          Ok(
            Json.toJson(gpPayment)
          )
        case None =>
          NotFound
      }
      .recover { case ex: Exception =>
        logger.error("Error while retrieving groupPayments", ex)
        InternalServerError(Json.obj("error" -> "Failed to retrieve groupPayments"))
      }
  }

}
