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
import uk.gov.hmrc.corporationtax.services.PenaltiesService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Try


class PenaltiesController @Inject()(
                                     cc: ControllerComponents,
                                     service: PenaltiesService
                                   )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getPenaltyTransactionList(
                                 taxRef: Long,
                                 accPeriod: Long,
                                 endDateMaybe: Option[String]
                               ): Action[AnyContent] = Action.async { implicit request =>
    endDateMaybe match {
      case Some(endDateStr) =>
        service
          .getPenaltyTransactionList(taxRef, accPeriod, Try{ LocalDate.parse(endDateStr) }.toOption )
          .map { penalties =>
            Ok(Json.toJson(penalties))
          }
          .recover { case ex: Exception =>
              logger.error("Error while retrieving penalties", ex)
              InternalServerError(Json.obj("error" -> "Failed to retrieve penalties: 11"))
          }
      case None =>
        service
          .getPenaltyTransactionList(taxRef, accPeriod, None )
          .map { penalties =>
            Ok(Json.toJson(penalties))
          }
          .recover { case ex: Exception =>
            logger.error("Error while retrieving penalties", ex)
            InternalServerError(Json.obj("error" -> "Failed to retrieve penalties: 11"))
          }
    }

  }

}
