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

import play.api.Logging
import uk.gov.hmrc.corporationtax.connectors.HasClientConnector
import uk.gov.hmrc.corporationtax.models.HasClientResponse
import uk.gov.hmrc.corporationtax.utils.CommonBooleanTransformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HasClientService @Inject() (
  connector: HasClientConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def hasClient(
    utr: Long,
    irAgentId: String,
    credentialId: String
  )(implicit hc: HeaderCarrier): Future[HasClientResponse] = {
    logger.info(s"Calling Has Client for UTR: $utr")

    connector
      .hasClient(utr, irAgentId, credentialId)
      .map(response => HasClientResponse(status = CommonBooleanTransformation.toBool(response.status.toString)))
  }
}
