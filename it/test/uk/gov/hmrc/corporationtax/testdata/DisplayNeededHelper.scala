package uk.gov.hmrc.corporationtax.testdata

import uk.gov.hmrc.corporationtax.models.DisplayNeededResponse

 trait DisplayNeededHelper {

   val displayNeededAllFalse: DisplayNeededResponse = DisplayNeededResponse(
     taxIsDisplayNeededFlag = "N",
     interestIsDisplayNeededFlag = "N",
     paymentIsDisplayNeededFlag = "N",
     repayReallocIsDisplayNeededFlag = "N"
   )

   val displayNeededAllTrue: DisplayNeededResponse = DisplayNeededResponse(
     taxIsDisplayNeededFlag = "Y",
     interestIsDisplayNeededFlag = "Y",
     paymentIsDisplayNeededFlag = "Y",
     repayReallocIsDisplayNeededFlag = "Y"
   )

   val displayNeededMixed: DisplayNeededResponse = DisplayNeededResponse(
     taxIsDisplayNeededFlag = "Y",
     interestIsDisplayNeededFlag = "N",
     paymentIsDisplayNeededFlag = "Y",
     repayReallocIsDisplayNeededFlag = "N"
   )

}
