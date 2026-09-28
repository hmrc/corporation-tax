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

package uk.gov.hmrc.corporationtax.services.gpa

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.gpa.CompanyNominatorConnector
import uk.gov.hmrc.corporationtax.models.gpa.CompanyNominator
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation.toBool
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CompanyNominatorService @Inject() (companyNominatorConnector: CompanyNominatorConnector)(implicit
  ec: ExecutionContext
) extends Logging {

  def getIsCompanyNominatorOfGPA(gpaUtr: Long, nominatedCompanyUtr: Long)(implicit
    hc: HeaderCarrier
  ): Future[CompanyNominator] = {
    logger.info(s"Calling connector for gpaUtr: $gpaUtr and nominatedCompanyUtr: $nominatedCompanyUtr")

    companyNominatorConnector.getIsCompanyNominatorOfGPA(gpaUtr, nominatedCompanyUtr).map { rdsCompanyNominator =>
      CompanyNominator(
        isParticipator = toBool(rdsCompanyNominator.isParticipator)
      )
    }
  }
}
