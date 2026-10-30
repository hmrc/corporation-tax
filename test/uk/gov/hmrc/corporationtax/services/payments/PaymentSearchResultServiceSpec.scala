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

package uk.gov.hmrc.corporationtax.services.payments

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.payments.PaymentSearchResultConnector
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentSearchResultHelper
import uk.gov.hmrc.corporationtax.models.payments.{PaymentSearchResult, PaymentSearchResultResponse}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class PaymentSearchResultServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with PaymentSearchResultHelper {

  private trait Setup {
    val mockConnector: PaymentSearchResultConnector = mock[PaymentSearchResultConnector]
    implicit val hc: HeaderCarrier                  = HeaderCarrier()
    private val cc: ControllerComponents            = stubControllerComponents()
    implicit val ec: ExecutionContext               = cc.executionContext

    val taxRef: Long = 1287892L
    val service      = new PaymentSearchResultService(mockConnector)

  }

  def createRow(
    accountingPeriod: BigDecimal = 1,
    payslipId: BigDecimal = 1,
    amount: BigDecimal,
    effectiveDateOfPayment: Option[LocalDate] = Some(LocalDate.of(2007, 7, 14)),
    paymentType: String,
    bankSortCode: BigDecimal = 123,
    bankAccountNumber: BigDecimal = 123,
    chequeNumber: BigDecimal = 123,
    paymentAmount: BigDecimal,
    chequeId: BigDecimal
  ): PaymentSearchResult =
    PaymentSearchResult(
      accountingPeriod = accountingPeriod,
      payslipId = payslipId,
      amount = amount,
      effectiveDateOfPayment = effectiveDateOfPayment,
      paymentType = paymentType,
      bankSortCode = bankSortCode,
      bankAccountNumber = bankAccountNumber,
      chequeNumber = chequeNumber,
      paymentAmount = paymentAmount,
      chequeId = chequeId
    )

  "getPaymentSearchResult" should {

    "return a transformed list of PaymentSearchResults" in new Setup {
      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsMultiplePaymentSearchResult))
      val result: PaymentSearchResultResponse = service.getPaymentSearchResult(taxRef, None, None, 37500).futureValue

      result shouldBe multiplePaymentSearchResult

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])

    }

    "return a transformed list of PaymentSearchResults when fields are missing" in new Setup {
      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsMultiplePaymentSearchResultMissingFields))
      val result: PaymentSearchResultResponse = service.getPaymentSearchResult(taxRef, None, None).futureValue

      result shouldBe multiplePaymentSearchResultMissingFields

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])

    }

    "return an emptyList if the connector returns an empty CompanyDetails" in new Setup {

      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.successful(rdsEmptyPaymentSearchResult))

      val result: PaymentSearchResultResponse = service.getPaymentSearchResult(taxRef, None, None, 37500).futureValue

      result shouldBe PaymentSearchResultResponse(paymentSearchResult = List.empty)

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])
    }

    "propagates exceptions from connector" in new Setup {
      val ex = new RuntimeException("Error from Downstream")

      when(
        mockConnector.getPaymentSearchResult(any[Long], any[Option[LocalDate]], any[Option[LocalDate]])(
          any[HeaderCarrier]
        )
      )
        .thenReturn(Future.failed(ex))

      val result: Exception = intercept[Exception] {
        service.getPaymentSearchResult(taxRef, None, None, 37500).futureValue
      }

      verify(mockConnector, times(1)).getPaymentSearchResult(any(), any(), any())(any[HeaderCarrier])
    }
  }

  "filterPaymentSearchResult" should {

    "include BAC, BGP or CHP payment types when amount matches" in new Setup {
      val userEnteredAmount = 100
      val response          = PaymentSearchResultResponse(
        List(
          createRow(paymentType = "BAC", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BGP", amount = -100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "CHP", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "XYZ", amount = 100, paymentAmount = 300, chequeId = 0)
        )
      )

      val result = service.filterPaymentSearchResult(response, userEnteredAmount)

      result.paymentSearchResult.size mustBe 3
      result.paymentSearchResult.map(_.paymentType) should contain theSameElementsAs
        Seq("BAC", "BGP", "CHP")

      result.paymentSearchResult.map(_.paymentType) should not contain "XYZ"

      result.paymentSearchResult.forall(row =>
        row.amount == BigDecimal(100) || row.amount == BigDecimal(-100)
      ) mustBe true
    }

    "include BLP, NGP, LOP or BGT payment types when amount matches && chequeId is zero" in new Setup {
      val userEnteredAmount = 100
      val response          = PaymentSearchResultResponse(
        List(
          createRow(paymentType = "BLP", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "NGP", amount = -100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BGT", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "LOP", amount = -100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "XYZ", amount = 100, paymentAmount = 300, chequeId = 0)
        )
      )

      val result = service.filterPaymentSearchResult(response, userEnteredAmount)

      result.paymentSearchResult.size mustBe 4
      result.paymentSearchResult.map(_.paymentType) should contain theSameElementsAs
        Seq("BLP", "NGP", "BGT", "LOP")

      result.paymentSearchResult.forall(row =>
        row.amount == BigDecimal(100) || row.amount == BigDecimal(-100)
      ) mustBe true

      result.paymentSearchResult.map(_.paymentType) should not contain "XYZ"
    }

    "include BLP, NGP, LOP or BGT payment types when paymentAmount matches && chequeId is not zero && distinct bank details" in new Setup {
      val userEnteredAmount = 300
      val response          = PaymentSearchResultResponse(
        List(
          createRow(paymentType = "BLP", amount = 100, paymentAmount = 300, chequeId = 1),
          createRow(paymentType = "NGP", amount = -100, paymentAmount = -300, chequeId = 2, bankSortCode = 234),
          createRow(paymentType = "BGT", amount = 100, paymentAmount = 300, chequeId = 5, bankSortCode = 345),
          createRow(paymentType = "LOP", amount = -100, paymentAmount = -300, chequeId = -6, bankSortCode = 456),
          createRow(paymentType = "XYZ", amount = 200, paymentAmount = 300, chequeId = 0, bankSortCode = 567)
        )
      )

      val result = service.filterPaymentSearchResult(response, userEnteredAmount)

      result.paymentSearchResult.size mustBe 4
      result.paymentSearchResult.map(_.paymentType) should contain theSameElementsAs
        Seq("BLP", "NGP", "BGT", "LOP")

      result.paymentSearchResult.forall(row =>
        row.paymentAmount == BigDecimal(300) || row.paymentAmount == BigDecimal(-300)
      ) mustBe true
    }

    "exclude rows if amount or payment amount does not match" in new Setup {
      val userEnteredAmount = 800
      val response          = PaymentSearchResultResponse(
        List(
          createRow(paymentType = "BAC", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BGP", amount = -100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "CHP", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BLP", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "NGP", amount = -100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BGT", amount = 100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "LOP", amount = -100, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BLP", amount = 300, paymentAmount = 100, chequeId = 1),
          createRow(paymentType = "NGP", amount = -300, paymentAmount = -100, chequeId = 2, bankSortCode = 234),
          createRow(paymentType = "BGT", amount = 300, paymentAmount = 100, chequeId = 5, bankSortCode = 345),
          createRow(paymentType = "LOP", amount = -300, paymentAmount = -100, chequeId = -6, bankSortCode = 456)
        )
      )

      val result = service.filterPaymentSearchResult(response, userEnteredAmount)

      result.paymentSearchResult shouldBe empty
    }

    "remove duplicate rows with the same bankSortCode, bankAccountNumber and chequeNumber when cheque ID is not zero" in new Setup {
      val userEnteredAmount = 300
      val response          = PaymentSearchResultResponse(
        List(
          createRow(paymentType = "BLP", amount = 300, paymentAmount = 300, chequeId = 1),
          createRow(paymentType = "LOP", amount = -300, paymentAmount = 300, chequeId = 1),
          createRow(paymentType = "NGP", amount = 300, paymentAmount = 300, chequeId = 1),
          createRow(paymentType = "BGT", amount = 300, paymentAmount = 300, chequeId = 1),
          createRow(paymentType = "BGT", amount = 300, paymentAmount = 300, chequeId = 1, bankSortCode = 234),
          createRow(paymentType = "LOP", amount = 300, paymentAmount = 300, chequeId = 1, bankAccountNumber = 234),
          createRow(paymentType = "NGP", amount = 300, paymentAmount = 300, chequeId = 1, chequeNumber = 234),
          createRow(paymentType = "BLP", amount = 300, paymentAmount = 300, chequeId = 1, chequeNumber = 456)
        )
      )

      val result = service.filterPaymentSearchResult(response, userEnteredAmount)

      result.paymentSearchResult.size mustBe 5

      result.paymentSearchResult
        .map(row => (row.bankSortCode, row.bankAccountNumber, row.chequeNumber))
        .distinct should have size 5
    }

    "not remove duplicate rows with the same bankSortCode, bankAccountNumber and chequeNumber when cheque ID is zero" in new Setup {
      val userEnteredAmount = 300
      val response          = PaymentSearchResultResponse(
        List(
          createRow(paymentType = "BAC", amount = 300, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BGP", amount = -300, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "CHP", amount = 300, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BGT", amount = 300, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "LOP", amount = 300, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "NGP", amount = 300, paymentAmount = 300, chequeId = 0),
          createRow(paymentType = "BLP", amount = 300, paymentAmount = 300, chequeId = 0)
        )
      )

      val result = service.filterPaymentSearchResult(response, userEnteredAmount)

      result.paymentSearchResult.size mustBe 7
    }
  }
}
