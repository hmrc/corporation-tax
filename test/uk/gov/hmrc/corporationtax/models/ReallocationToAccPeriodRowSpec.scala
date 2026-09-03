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

package uk.gov.hmrc.corporationtax.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*

import java.time.LocalDate

class ReallocationToAccPeriodRowSpec extends AnyWordSpec with Matchers {

  "ReallocationToAccPeriod"     should {
    "serialize to JSON correctly" in {
      val details = ReallocationToAccPeriod(
        reallocation = List(
          ReallocationToAccPeriodRow(
            amount = BigDecimal(1234.56),
            reallocationDate = LocalDate.of(2024, 1, 15),
            sourceApEndDate = Some(LocalDate.of(2024, 12, 31)),
            sourceTaxpayerReference = "123",
            transactionType = ReallocationFrom
          )
        )
      )

      val json = Json.toJson(details)

      json shouldBe Json.parse(
        s"""
           |
           |{
           |"reallocation":
           |[
           |{
           |"amount" : 1234.56,
           |"reallocationDate" : "2024-01-15",
           |"sourceApEndDate" : "2024-12-31",
           |"sourceTaxpayerReference" : "123",
           |"transactionType" : "RFR"
           |}
           |]
           |}
           |""".stripMargin
      )
    }
    "serialize to JSON correctly when sourceApEndDate is empty " in {
      val details = ReallocationToAccPeriod(
        reallocation = List(
          ReallocationToAccPeriodRow(
            amount = BigDecimal(1234.56),
            reallocationDate = LocalDate.of(2024, 1, 15),
            sourceApEndDate = None,
            sourceTaxpayerReference = "123",
            transactionType = ReallocationFrom
          )
        )
      )

      val json = Json.toJson(details)

      json shouldBe Json.parse(
        s"""
           |
           |{
           |"reallocation":
           |[
           |{
           |"amount" : 1234.56,
           |"reallocationDate" : "2024-01-15",
           |"sourceTaxpayerReference" : "123",
           |"transactionType" : "RFR"
           |}
           |]
           |}
           |""".stripMargin
      )
    }
    "de-serialize from JSON correctly" in {
      val json         = Json.parse(
        s"""
           |
           |{
           |"reallocation":
           |[
           |{
           |"amount" : 1234.56,
           |"reallocationDate" : "2024-01-15",
           |"sourceApEndDate" : "2024-12-31",
           |"sourceTaxpayerReference" : "123",
           |"transactionType" : "RFR"
           |},
           |{
           |"amount" : 0.00,
           |"reallocationDate" : "2024-01-15",
           |"sourceApEndDate" : "2024-12-31",
           |"sourceTaxpayerReference" : "123",
           |"transactionType" : "MiscTFR"
           |}
           |]
           |}
           |""".stripMargin
      )
      val reallocToAcc = ReallocationToAccPeriod(
        reallocation = List(
          ReallocationToAccPeriodRow(
            amount = BigDecimal(1234.56),
            reallocationDate = LocalDate.of(2024, 1, 15),
            sourceApEndDate = Some(LocalDate.of(2024, 12, 31)),
            sourceTaxpayerReference = "123",
            transactionType = ReallocationFrom
          ),
          ReallocationToAccPeriodRow(
            amount = BigDecimal(0.00),
            reallocationDate = LocalDate.of(2024, 1, 15),
            sourceApEndDate = Some(LocalDate.of(2024, 12, 31)),
            sourceTaxpayerReference = "123",
            transactionType = MiscellaneousTransfer
          )
        )
      )
      Json.fromJson[ReallocationToAccPeriod](json) shouldBe JsSuccess(reallocToAcc)

    }
    "fail to deserialize when transactionType field is missing" in {
      val json = Json.parse(
        s"""
           |
           |{
           |"reallocation":
           |[
           |{
           |"amount" : 1234.56,
           |"reallocationDate" : "2024-01-15",
           |"sourceApEndDate" : "2024-12-31",
           |"sourceTaxpayerReference" : "123"
           |}
           |]
           |}
           |""".stripMargin
      )
      json.validate[ReallocationToAccPeriod] shouldBe a[JsError]
    }
    "fail to deserialize when transactionType is an unknown string" in {
      val json = Json.parse(
        s"""
           |
           |{
           |"reallocation":
           |[
           |{
           |"amount" : 1234.56,
           |"reallocationDate" : "2024-01-15",
           |"sourceApEndDate" : "2024-12-31",
           |"sourceTaxpayerReference" : "123",
           |"transactionType" : "unknown-value"
           |}
           |]
           |}
           |""".stripMargin
      )
      json.validate[ReallocationToAccPeriod] shouldBe a[JsError]
    }
    "fail to deserialize when transactionType is a number" in {
      val json = Json.parse(
        s"""
           |
           |{
           |"reallocation":
           |[
           |{
           |"amount" : 1234.56,
           |"reallocationDate" : "2024-01-15",
           |"sourceApEndDate" : "2024-12-31",
           |"sourceTaxpayerReference" : "123",
           |"transactionType" : 16
           |}
           |]
           |}
           |""".stripMargin
      )
      json.validate[ReallocationToAccPeriod] shouldBe a[JsError]
    }
  }
  "ReallocationTransactionType" should {

    "write MiscellaneousTransfer as MiscTFR" in {
      Json.toJson[ReallocationTransactionType](MiscellaneousTransfer) shouldBe JsString("MiscTFR")
    }

    "write ReallocationFrom as RFR " in {
      Json.toJson[ReallocationTransactionType](ReallocationFrom) shouldBe JsString("RFR")
    }

    "read MiscTFR as MiscellaneousTransfer" in {
      JsString("MiscTFR").validate[ReallocationTransactionType] shouldBe JsSuccess(MiscellaneousTransfer)
    }

    "read RTO as ReallocationTo" in {
      JsString("RTO").validate[ReallocationTransactionType] shouldBe JsSuccess(ReallocationTo)
    }

    "fail to read an unknown string value" in {
      val result = JsString("unknown").validate[ReallocationTransactionType]

      result                                                 shouldBe a[JsError]
      result.asInstanceOf[JsError].errors.head._2.head.message should include(
        "Unknown ReallocationTransactionType"
      )
    }

    "fail to read a non-string JSON value" in {
      val result = JsNumber(16).validate[ReallocationTransactionType]

      result                                                 shouldBe a[JsError]
      result.asInstanceOf[JsError].errors.head._2.head.message should include("Expected JsString")
    }
  }

}
