package uk.gov.hmrc.corporationtax.helpers.gpa

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

}
