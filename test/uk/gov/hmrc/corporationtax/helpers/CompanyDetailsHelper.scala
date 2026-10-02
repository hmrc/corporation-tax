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

package uk.gov.hmrc.corporationtax.helpers

import uk.gov.hmrc.corporationtax.models.{
  CompanyDetails, CompanyDetailsResponse, RdsCompanyDetails, RdsCompanyDetailsResponse
}
import uk.gov.hmrc.corporationtax.utils.EmptyAndZeroConstants.emptyString

trait CompanyDetailsHelper {

  val rdsEmptyCompany: RdsCompanyDetailsResponse = RdsCompanyDetailsResponse(taxpayerDetails = List.empty)

  val rdsTwoCompanies: RdsCompanyDetailsResponse = RdsCompanyDetailsResponse(taxpayerDetails =
    List(
      RdsCompanyDetails(
        "OU-100234",
        "Northbridge Logistics Ltd",
        None,
        Some("Unit 4 Riverside Park"),
        Some("Canal Street"),
        None,
        Some("West Midlands"),
        Some("B1 2JH")
      ),
      RdsCompanyDetails(
        "OU-100587",
        "Harlow & Finch Consulting Ltd",
        None,
        Some("22 Market Square"),
        None,
        Some("address1"),
        Some("address2"),
        Some("LS1 6DT")
      )
    )
  )

  val emptyCompanyDetails: CompanyDetailsResponse = CompanyDetailsResponse(taxpayerDetails = List.empty)
  val twoCompanies: CompanyDetailsResponse        = CompanyDetailsResponse(taxpayerDetails =
    List(
      CompanyDetails(
        "OU-100234",
        "Northbridge Logistics Ltd",
        emptyString,
        "Unit 4 Riverside Park",
        "Canal Street",
        emptyString,
        "West Midlands",
        "B1 2JH"
      ),
      CompanyDetails(
        "OU-100587",
        "Harlow & Finch Consulting Ltd",
        emptyString,
        "22 Market Square",
        emptyString,
        "address1",
        "address2",
        "LS1 6DT"
      )
    )
  )

}
