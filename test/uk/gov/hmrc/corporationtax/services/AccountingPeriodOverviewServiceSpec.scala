package uk.gov.hmrc.corporationtax.services

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers

class AccountingPeriodOverviewServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  private trait Fixture {
    val mockAccPeriodService: AccountingPeriodService        = mock[AccountingPeriodService]
    val mockAccPeriodDetailsService: AccPeriodDetailsService = mock[AccountingPeriodDetailsService]
    
    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val taxRef = 12L
    val accPeriod = 36L
    
    val service = new AccountingPeriodOverviewService(mockAccPeriodService, mockAccPeriodDetailsService)
    
    "AccountingPeriodOverviewService.getAccountingPeriodOverview " should {
      "return Right(AccountingPeriodOverview) when taxChargePresent = true" in {
        
      }
      "return Right(AccountingPeriodOverview) when taxChargePresent = false" in {
        
      }
      "return Right(AccountingPeriodOverview) after evaluating clericalCalculationFlag = false" in {
        
      }
      "return Right(AccountingPeriodOverview) after evaluating clericalCalculationFlag = true" in {
        
      }
      "return Left(MissingAccountingPeriodError) when AccountingPeriodServiceReturns Left(MissingAccountingPeriodError)" in {
        
      }
      "propagate errors and exceptions from AccountingPeriodService" in {
        
      }
      "propagate errors and exceptions from AccountingPeriodDetailsService" in {
      }
    }
  }
}
