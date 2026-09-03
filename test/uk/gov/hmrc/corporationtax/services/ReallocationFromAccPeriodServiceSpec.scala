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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.corporationtax.connectors.ReallocationFromAccPeriodRdsProxyConnector
import uk.gov.hmrc.corporationtax.helpers.ReallocationFromAccPeriodHelper
import uk.gov.hmrc.corporationtax.models.{
  MiscellaneousTransfer, RdsReallocationFromAccPeriodResponse, ReallocationFromAccPeriod, ReallocationTo
}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class ReallocationFromAccPeriodServiceSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with MockitoSugar
    with ReallocationFromAccPeriodHelper {

  private trait BaseSetup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    private val cc: ControllerComponents = stubControllerComponents()
    implicit val ec: ExecutionContext    = cc.executionContext

    val mockRds: ReallocationFromAccPeriodRdsProxyConnector = mock[ReallocationFromAccPeriodRdsProxyConnector]
    val service                                             = new ReallocationFromAccPeriodService(mockRds)
    val taxReferenceNumber: Long                            = 1234567L
    val accPeriod: Long                                     = 3456L
  }

  "ReallocationFromAccPeriodService.getReallocationFromAccPeriod" should {

    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse with amount = None  and transform to ReallocationFromAccPeriod with amount = 0.00" in new BaseSetup {
      val destinationTaxPayerReference: String = "7865"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
        rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(0.00),
        None,
        destinationTaxPayerReference,
        MiscellaneousTransfer
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse and transform to ReallocationFromAccPeriod with non-zero amount field negated and rounded up to 2 decimal places" in new BaseSetup {
      val destinationTaxPayerReference: String = "7865"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse = rdsReallocationFromAccPeriodResponse(
        amount = Some(BigDecimal(57689.8765)),
        destinationTaxPayerReference = destinationTaxPayerReference
      )

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(-57689.88),
        None,
        destinationTaxPayerReference,
        MiscellaneousTransfer
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse and transform to ReallocationFromAccPeriod with zero amount unchanged" in new BaseSetup {

      val destinationTaxPayerReference: String = "7865"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse = rdsReallocationFromAccPeriodResponse(
        amount = Some(BigDecimal(0)),
        destinationApEndDate = Some(LocalDate.of(2022, 12, 6)),
        destinationTaxPayerReference = destinationTaxPayerReference
      )

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(0.0),
        Some(LocalDate.of(2022, 12, 6)),
        destinationTaxPayerReference,
        MiscellaneousTransfer
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse with destinationApEndDate = None and then transform to ReallocationFromAccPeriod with destinationApEndDate = None " in new BaseSetup {

      val destinationTaxPayerReference: String = "7865"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
        rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(0.0),
        None,
        destinationTaxPayerReference,
        MiscellaneousTransfer
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return empty ReallocationFromAccPeriod when ReallocationFromAccPeriod is empty" in new BaseSetup {
      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(emptyListReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe emptyTransformedListReallocationFromAccPeriod

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "propagate any errors or exceptions from connector" in new BaseSetup {

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.failed(new RuntimeException("boom")))

      val ex: RuntimeException = intercept[RuntimeException] {
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue
      }

      ex.getMessage should include("boom")

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }

    // Business Function : F31 - Get Reallocation From Accounting Period
    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse when destinationTaxPayerReference = OasTransfer(99) and transform to ReallocationFromAccPeriod with transactionType = MiscellaneousTransfer" in new BaseSetup {
      val destinationTaxPayerReference: String = "99"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
        rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(0.0),
        None,
        destinationTaxPayerReference,
        transactionType = MiscellaneousTransfer
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      result.reallocation.head.transactionType shouldBe MiscellaneousTransfer

      verify(mockRds).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse(destinationTaxPayerReference is different from requested taxPayerReference) and transform to ReallocationFromAccPeriod with transactionType = MiscellaneousTransfer" in new BaseSetup {
      val taxPayerReferenceNumber: Long = 1237L

      val destinationTaxPayerReference: String = "98765"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
        rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(0.0),
        None,
        destinationTaxPayerReference,
        transactionType = MiscellaneousTransfer
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxPayerReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      verify(mockRds).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

    }
    "delegate to connector and successfully return RdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = requested taxPayerReference) and transform to ReallocationFromAccPeriod with transactionType = ReallocationTo" in new BaseSetup {
      val taxPayerReferenceNumber: Long = 1237L

      val destinationTaxPayerReference: String = "1237"

      val rdsReallocationFromAccPeriod: RdsReallocationFromAccPeriodResponse =
        rdsReallocationFromAccPeriodResponse(destinationTaxPayerReference = destinationTaxPayerReference)

      val transformedReallocation: ReallocationFromAccPeriod = transformedReallocationFromAccPeriod(
        BigDecimal(0.0),
        None,
        destinationTaxPayerReference,
        transactionType = ReallocationTo
      )

      when(mockRds.getReallocationFromAccPeriod(eqTo(taxPayerReferenceNumber), eqTo(accPeriod))(any[HeaderCarrier]))
        .thenReturn(Future.successful(rdsReallocationFromAccPeriod))

      val result: ReallocationFromAccPeriod =
        service.getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod).futureValue

      result shouldBe transformedReallocation

      verify(mockRds).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

      verify(mockRds, times(1)).getReallocationFromAccPeriod(taxPayerReferenceNumber, accPeriod)

    }

  }

}
