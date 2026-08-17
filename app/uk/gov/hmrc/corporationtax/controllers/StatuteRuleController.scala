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

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class StatuteRuleController @Inject()(
                                       cc: ControllerComponents,
                                       service: StatuteRuleService
                                     )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getStatueRule(ruleRateKey: String,
                    startDateStr: String, endDateStr: String): Action[AnyContent] = Action.async { implicit request =>
    (Try {
      LocalDate.parse(startDateStr)
    }.toEither,
      Try {
        LocalDate.parse(endDateStr)
      }.toEither
    ) match {
      case (Right(_), Right(_)) =>
        service
          .getStatueRule(ruleRateKey, startDateStr, endDateStr)
          .map { responseRecord =>
            Ok(
              Json.toJson(responseRecord)
            )
          }
          .recover { case ex: Exception =>
            logger.error("Error while retrieving tax transactions", ex)
            InternalServerError(Json.obj("error" -> "Failed to retrieve accounting period details"))
          }
      case (_, _) =>
        logger.error("Error while retrieving tax transactions")
        Future.successful(
          InternalServerError(Json.obj("error" -> "Failed to retrieve accounting period details"))
        )

    }
    
  }

}
