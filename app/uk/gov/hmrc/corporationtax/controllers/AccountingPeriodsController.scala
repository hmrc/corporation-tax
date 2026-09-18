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
import uk.gov.hmrc.corporationtax.models.MissingAccountingPeriodError
import uk.gov.hmrc.corporationtax.services.{AccountingPeriodService, AccountingPeriodsService}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AccountingPeriodsController @Inject() (
  cc: ControllerComponents,
  service: AccountingPeriodsService,
  accountingPeriodService: AccountingPeriodService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getAccountingPeriods(taxRef: Long): Action[AnyContent]                 = Action.async { implicit request =>
    service
      .getAccountingPeriod(taxRef)
      .map { response =>
        Ok(Json.toJson(response))
      }
      .recover {
        case u: UpstreamErrorResponse =>
          logger.error("Error response from Upstream", u)
          Status(u.statusCode)(Json.obj("message" -> u.message))
        case t: Throwable             =>
          logger.error("Error while retrieving AccountingPeriods", t)
          InternalServerError(Json.obj("error" -> "Failed to retrieve AccountingPeriods"))
      }
  }
  def getAccountingPeriod(taxRef: Long, accPeriod: Long): Action[AnyContent] = Action.async { implicit request =>
    accountingPeriodService
      .getAccountingPeriod(taxRef, accPeriod)
      .map {
        case Right(value)                              => Ok(Json.toJson(value))
        case Left(error: MissingAccountingPeriodError) =>
          logger.error(s"Error while retrieving accountingPeriod information : ${error.message}")
          NotFound(Json.obj("error" -> error.message))
      }
      .recover {
        case u: UpstreamErrorResponse =>
          logger.error("Error response from Upstream while retrieving AccountingPeriods", u)
          Status(u.statusCode)(Json.obj("message" -> u.message))
        case t: Throwable             =>
          logger.error("Error while retrieving AccountingPeriods", t)
          InternalServerError(Json.obj("error" -> "Failed to retrieve accountingPeriod information"))
      }
  }

}
