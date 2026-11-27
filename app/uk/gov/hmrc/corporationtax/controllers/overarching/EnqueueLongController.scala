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

package uk.gov.hmrc.corporationtax.controllers.overarching

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.corporationtax.services.overarching.EnqueueLongService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class EnqueueLongController @Inject() (
  enqueueLongService: EnqueueLongService,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getEnqueueLong(
    messageIdIn: Long,
    sender: String,
    queue: String,
    replyQueue: String,
    correlationId: String,
    filter: String,
    key: String,
    dataType: Int,
    numberValue: Int
  ): Action[AnyContent] = Action.async { implicit request =>
    enqueueLongService
      .getEnqueueLong(messageIdIn, sender, queue, replyQueue, correlationId, filter, key, dataType, numberValue)
      .map { enqueue =>
        Ok(Json.toJson(enqueue))
      }
      .recover { case ex: Exception =>
        logger.error("Error while retrieving enqueue long", ex)
        InternalServerError(Json.obj("error" -> "Failed to retrieve enqueue long"))
      }

  }
}
