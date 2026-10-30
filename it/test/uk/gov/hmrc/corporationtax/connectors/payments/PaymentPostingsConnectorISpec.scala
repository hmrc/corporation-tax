package uk.gov.hmrc.corporationtax.connectors.payments


import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.corporationtax.config.AppConfig
import uk.gov.hmrc.corporationtax.itutils.ApplicationWithWiremock
import uk.gov.hmrc.corporationtax.testdata
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.corporationtax.helpers.payments.PaymentPostingsHelper

class PaymentPostingsConnectorISpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ApplicationWithWiremock
    with BeforeAndAfterEach
    with PaymentPostingsHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit private val appConfig: AppConfig              = app.injector.instanceOf[AppConfig]
  private val connector: PaymentPostingsConnector = app.injector.instanceOf[PaymentPostingsConnector]

  "getPaymentPostings" should {

    def url(taxRef: Long) =
      s"${appConfig.rdsDatacacheProxyEndpoint}/payment-postings/$taxRef"

    "return an empty Payment Postings with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"taxpayerDetails":[]
                   |
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentPostings(12L).futureValue
      result shouldBe emptyPaymentPostings
    }

    "return single Payment Postings with status code OK" in {
      stubFor(
        get(urlPathEqualTo(url(16L)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(
                s"""
                   |{
                   |"taxpayerDetails":
                   |[
                   | {
                   | "orgUnitId":"OU-100234",
                   | "companyName":"Northbridge Logistics Ltd",
                   | "companyRegNo":"08123456",
                   | "addressLine1":"Unit 4 Riverside Park",
                   | "addressLine2": "Canal Street",
                   | "addressLine3": "Birmingham",
                   | "addressLine4": "West Midlands",
                   | "postCode": "B1 2JH"
                   |  },
                   |  {
                   | "orgUnitId":"OU-100587",
                   | "companyName":"Harlow & Finch Consulting Ltd",
                   | "companyRegNo": "11987654",
                   | "addressLine1": "22 Market Square",
                   | "addressLine2": "Leeds",
                   | "addressLine3": "address1",
                   | "addressLine4": "address2",
                   | "postCode": "LS1 6DT"
                   |  }
                   |]
                   |}
                   |""".stripMargin
              )
          )
      )

      val result = connector.getPaymentPostings(16L).futureValue
      result shouldBe singlePaymentPosting

    }

    "return INTERNAL_SERVER_ERROR when the service failed" in {
      stubFor(
        get(urlPathEqualTo(url(12L)))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
              .withBody("Boom")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentPostings(12).futureValue
      }

      ex.getMessage should include("Boom")
    }

    "return 404 when BE returns BAD_REQUEST" in {
      stubFor(
        get(urlPathEqualTo(url(13L)))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody("Invalid Request")
          )
      )

      val ex = intercept[Exception] {
        connector.getPaymentPostings(13).futureValue
      }

      ex.getMessage should include("Invalid Request")
    }
  }
}