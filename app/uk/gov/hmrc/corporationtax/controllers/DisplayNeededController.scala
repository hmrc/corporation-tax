package uk.gov.hmrc.corporationtax.controllers

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.corporationtax.services.DisplayNeededService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DisplayNeededController @Inject() (
                                     cc: ControllerComponents,
                                     service: DisplayNeededService
                                   )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getDisplayNeeded(taxRef: Long, accPeriod: Long): Action[AnyContent] = Action.async { implicit request =>
    service
      .getDisplayNeeded(taxRef, accPeriod)
      .map { displayNeeded =>
        Ok(Json.toJson(displayNeeded))
      }
      .recover { case ex: Exception =>
        logger.error("Error while retrieving display needed", ex)
        InternalServerError(Json.obj("error" -> "Failed to retrieve display needed"))
      }
  }

}
