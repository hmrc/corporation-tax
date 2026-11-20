/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.corporationtax.services.clientExchangeProxy

import uk.gov.hmrc.corporationtax.models.clientExchangeProxy.AsynchronousProcessWaitTime

import scala.util.Try
import scala.xml.XML

object WaitTimeXmlMapper {
  def parse(xmlBody: String): Either[String, AsynchronousProcessWaitTime] =
    for {
      xml         <- Try(XML.loadString(xmlBody)).toEither.left.map(_ => "invalid XML document")
      root        <- (xml \\ "AsynchronousProcessWaitTime").headOption.toRight("missing AsynchronousProcessWaitTime element")
      browser     <- root.attribute("browserInterval").map(_.text.trim).toRight("missing browserInterval attribute")
      browserMs   <- Try(browser.toLong).toEither.left.map(_ => s"invalid browserInterval '$browser'")
      businessVals = (xml \\ "BusinessServiceInterval").map(_.text.trim).toList
      businessMs  <- sequence(businessVals.map { s =>
                       Try(s.toLong).toEither.left.map(_ => s"invalid BusinessServiceInterval '$s'")
                     })
    } yield AsynchronousProcessWaitTime(browserMs, businessMs)

  private def sequence[A](values: List[Either[String, A]]): Either[String, List[A]] =
    values.foldRight(Right(Nil): Either[String, List[A]]) { (currentEither, accEither) =>
      (currentEither, accEither) match {
        case (Right(v), Right(acc)) => Right(v :: acc)
        case (Left(err), _)         => Left(err)
        case (_, Left(err))         => Left(err)
      }
    }
}
