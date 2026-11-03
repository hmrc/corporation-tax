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

package uk.gov.hmrc.corporationtax.controllers.payments

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.models.MissingDataError
import uk.gov.hmrc.corporationtax.models.payments.PaymentsOnAccount
import uk.gov.hmrc.corporationtax.queryParams.PaymentSearchResultQueryParams
import uk.gov.hmrc.corporationtax.services.payments.{PaymentSearchResultService, PaymentsOnAccountService}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class PaymentOnAccountController @Inject()(
  cc: ControllerComponents,
  service: PaymentsOnAccountService
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getPaymentsOnAccount(taxRef: Long): Action[AnyContent] =
    Action.async { implicit request =>
      service
        .getPaymentsOnAccount(taxRef)
        .map {
          case Right(response: PaymentsOnAccount) => ???
          case Left(error: MissingDataError) => ???
          case _ => ???
        }
        .recover {
          case u: UpstreamErrorResponse =>
            Status(u.statusCode)(Json.obj("message" -> u.message))
          case t: Throwable             =>
            logger.error("Error while retrieving paymentsOnAccount", t)
            InternalServerError(Json.obj("error" -> "Failed to retrieve paymentsOnAccount"))
        }
    }

}
