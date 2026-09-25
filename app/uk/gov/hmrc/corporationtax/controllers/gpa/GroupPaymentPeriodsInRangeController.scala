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

  def getGroupPaymentPeriodsInRange(gpaUTR: Long, nominatedCompanyUTR: Long, pPeriod: Long, pMonthRestriction: Long): Action[AnyContent] = Action.async { implicit request =>
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