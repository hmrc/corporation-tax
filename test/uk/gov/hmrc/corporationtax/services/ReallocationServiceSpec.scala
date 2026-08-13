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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers
import uk.gov.hmrc.corporationtax.connectors.ReallocationsConnector
import uk.gov.hmrc.corporationtax.helpers.ReallocationDataHelper
import uk.gov.hmrc.corporationtax.models.Reallocations
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ReallocationServiceSpec extends AnyWordSpec with Matchers with ScalaFutures with ReallocationDataHelper {

  private trait Fixture {
    val mockReallocationsConnector: ReallocationsConnector = mock[ReallocationsConnector]

    val cc                            = Helpers.stubControllerComponents()
    implicit val ec: ExecutionContext = cc.executionContext
    implicit val hc: HeaderCarrier    = HeaderCarrier()

    val service = new ReallocationService(mockReallocationsConnector)

  }

  "getByAccountingPeriod returns list of Reallocations from connector" in new Fixture {

    when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.successful(reallocationsTwoItems))

    val result: Reallocations = service.getByAccountingPeriod(1L, 1L).futureValue

    result shouldBe reallocationsExpected

    verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
  }

  "getByAccountingPeriod returns failure from connector" in new Fixture {

    val error = new RuntimeException("Simulate error")
    when(mockReallocationsConnector.getByAccountingPeriod(any[Long], any[Long])(any[HeaderCarrier]))
      .thenReturn(Future.failed(error))

    val result: Throwable = service.getByAccountingPeriod(1L, 1L).failed.futureValue

    result shouldBe error

    verify(mockReallocationsConnector).getByAccountingPeriod(1L, 1L)(hc)
  }

  // Business Function : F31 - Get Reallocation From Accounting Period
  "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse when destinationTaxPayerReference = OasTransfer(99) and transform to TransformedReallocationFromAccPeriod with transactionType = MiscellaneousTransfer" in new BaseSetup {
    val destinationTaxPayerReference: String = "99"

    val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
      rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

    val transformedReallocation: TransformedReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
      BigDecimal(0.0),
      emptyString,
      destinationTaxPayerReference,
      transactionType = MiscellaneousTransfer
    )

    when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
      .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

    val result: TransformedReallocationFromAccPeriod =
      service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

    result shouldBe transformedReallocation

    result.reallocation.head.transactionType shouldBe MiscellaneousTransfer

    verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

  }
  "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse(destinationTaxPayerReference is different from requested taxPayerReference) and transform to TransformedReallocationFromAccPeriod with transactionType = MiscellaneousTransfer" in new BaseSetup {
    val taxPayerReferenceNumber: Long = 1237L

    val destinationTaxPayerReference: String = "98765"

    val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
      rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

    val transformedReallocation: TransformedReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
      BigDecimal(0.0),
      emptyString,
      destinationTaxPayerReference,
      transactionType = MiscellaneousTransfer
    )

    when(mockRds.getReallocationFromAccPeriod(eqTo(taxPayerReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
      .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

    val result: TransformedReallocationFromAccPeriod =
      service.getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod).futureValue

    result shouldBe transformedReallocation

    verify(mockRds).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

    verify(mockRds, times(1)).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

  }
  "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse and transform to TransformedReallocationFromAccPeriod with transactionType = ReallocationTo" in new BaseSetup {
    val taxPayerReferenceNumber: Long = 1237L

    val destinationTaxPayerReference: String = "1237"

    val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
      rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

    val transformedReallocation: TransformedReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
      BigDecimal(0.0),
      emptyString,
      destinationTaxPayerReference,
      transactionType = ReallocationTo
    )

    when(mockRds.getReallocationFromAccPeriod(eqTo(taxPayerReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
      .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

    val result: TransformedReallocationFromAccPeriod =
      service.getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod).futureValue

    result shouldBe transformedReallocation

    verify(mockRds).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

    verify(mockRds, times(1)).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

  }

}
