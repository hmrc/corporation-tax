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

package uk.gov.hmrc.corporationtax.services

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.CompanyDetailsRdsProxyConnector
import uk.gov.hmrc.corporationtax.models.CompanyDetailsResponse
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.models.CompanyDetails
import uk.gov.hmrc.corporationtax.utils.EmptyAndZeroConstants.emptyString

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CompanyDetailsService @Inject() (connector: CompanyDetailsRdsProxyConnector)(implicit ec: ExecutionContext)
    extends Logging {

  def getCompanyDetails(taxRef: Long)(implicit hc: HeaderCarrier): Future[CompanyDetailsResponse] =
    connector.getCompanyDetails(taxRef).map { rdsCompanyDetailsResponse =>
      CompanyDetailsResponse(
        taxpayerDetails = rdsCompanyDetailsResponse.taxpayerDetails.map { value =>
          CompanyDetails(
            orgUnitId = value.orgUnitId,
            companyName = value.companyName,
            companyRegNo = value.companyRegNo.getOrElse(emptyString),
            addressLine1 = value.addressLine1.getOrElse(emptyString),
            addressLine2 = value.addressLine2.getOrElse(emptyString),
            addressLine3 = value.addressLine3.getOrElse(emptyString),
            addressLine4 = value.addressLine4.getOrElse(emptyString),
            postCode = value.postCode.getOrElse(emptyString)
          )
        }
      )

    }
}
