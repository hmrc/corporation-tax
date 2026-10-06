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

package uk.gov.hmrc.corporationtax.controllers.gpa

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.services.gpa.GroupPaymentPeriodsInRangeService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GroupPaymentPeriodsInRangeController @Inject() (
  cc: ControllerComponents,
  service: GroupPaymentPeriodsInRangeService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getGroupPaymentPeriodsInRange(
    gpaUTR: Long,
    nominatedCompanyUTR: Long,
    pPeriod: Int,
    pMonthRestriction: Int
  ): Action[AnyContent] = Action.async { implicit request =>
    service
      .getGroupPaymentPeriodsInRange(gpaUTR, nominatedCompanyUTR, pPeriod, pMonthRestriction)
      .map { isPeriodWithinRange =>
        Ok(Json.toJson(isPeriodWithinRange))
      }
      .recover { case ex: Exception =>
        logger.error("Error while retrieving period within range", ex)
        InternalServerError(Json.obj("error" -> "Failed to retrieve period within range"))
      }
  }

}
