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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.services.StatuteRuleService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.libs.json.Json
import uk.gov.hmrc.corporationtax.models.StatuteRuleResponse
import uk.gov.hmrc.corporationtax.queryParams.StatuteQueryParams

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class StatuteRuleController @Inject()(
                                       cc: ControllerComponents,
                                       service: StatuteRuleService
                                     )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getStatueRule(queryParams: StatuteQueryParams): Action[AnyContent] = Action.async {
    implicit request =>
      service
        .getStatueRule(queryParams.ruleKey, queryParams.startDate, queryParams.endDate)
        .map {
          case Some(responseRecord) =>
            Ok(
              Json.toJson(responseRecord)
            )
          case None =>
            NotFound
        }
        .recover { case ex: Exception =>
          logger.error("Error while retrieving tax transactions", ex)
          InternalServerError(Json.obj("error" -> "Failed to retrieve StatueRule"))
        }
  }

}
