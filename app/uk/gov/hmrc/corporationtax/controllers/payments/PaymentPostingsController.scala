package uk.gov.hmrc.corporationtax.controllers.payments


import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.services.payments.PaymentPostingsService
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class PaymentPostingsController @Inject() (
                                            cc: ControllerComponents,
                                            service: PaymentPostingsService
                                          )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getPaymentPostings(taxRef: Long): Action[AnyContent] = Action.async { implicit request =>
    service
      .getPaymentPostings(taxRef)
      .map { paymentPostingsResponse =>
        Ok(Json.toJson(paymentPostingsResponse))
      }
      .recover {
        case u: UpstreamErrorResponse =>
          Status(u.statusCode)(Json.obj("message" -> u.message))
        case t: Throwable             =>
          logger.error("Error while retrieving payment postings", t)
          InternalServerError(Json.obj("error" -> "Failed to retrieve payment postings"))
      }
  }

}
