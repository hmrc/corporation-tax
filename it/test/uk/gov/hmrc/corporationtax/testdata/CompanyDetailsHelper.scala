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

package uk.gov.hmrc.corporationtax.testdata

import uk.gov.hmrc.corporationtax.models.{RdsCompanyDetails, RdsCompanyDetailsResponse}

trait CompanyDetailsHelper {

  val emptyCompany: RdsCompanyDetailsResponse = RdsCompanyDetailsResponse(taxpayerDetails = List.empty)

  val twoCompanies: RdsCompanyDetailsResponse = RdsCompanyDetailsResponse(taxpayerDetails =
    List(
      RdsCompanyDetails(
        "OU-100234",
        "Northbridge Logistics Ltd",
        Some("08123456"),
        Some("Unit 4 Riverside Park"),
        Some("Canal Street"),
        Some("Birmingham"),
        Some("West Midlands"),
        Some("B1 2JH")
      ),
      RdsCompanyDetails(
        "OU-100587",
        "Harlow & Finch Consulting Ltd",
        Some("11987654"),
        Some("22 Market Square"),
        Some("Leeds"),
        Some("address1"),
        Some("address2"),
        Some("LS1 6DT")
      )
    )
  )

  val oneCompany: RdsCompanyDetailsResponse = RdsCompanyDetailsResponse(taxpayerDetails =
    List(
      RdsCompanyDetails(
        "OU-200019",
        "Greenfield Bakeries Ltd",
        Some("09456123"),
        Some("5 Mill Lane"),
        Some("Shrewsbury"),
        Some("Shropshire"),
        None,
        Some("SY1 1AB")
      )
    )
  )

}
