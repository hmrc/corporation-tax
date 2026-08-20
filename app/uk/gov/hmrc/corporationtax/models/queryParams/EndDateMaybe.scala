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

package uk.gov.hmrc.corporationtax.models.queryParams

import play.api.mvc.PathBindable

import java.time.LocalDate
import scala.util.Try

final case class EndDateMaybe(endDate: Option[LocalDate])

object EndDateMaybe {
  implicit def pathBinder(implicit strBinder: PathBindable[String]): PathBindable[EndDateMaybe] = new PathBindable[EndDateMaybe] {
    override def bind(key: String, value: String): Either[String, EndDateMaybe] = {
      val res = {
        for {
          endDateStr <- strBinder.bind(key, value)
          endDate = Try {
            LocalDate.parse(endDateStr)
          }.toOption
          //user <- User.findById(id).toRight("User not found")
        } yield EndDateMaybe(endDate = endDate)
      }
      res match {
        case Right(x) => Right(x)
        case Left(_) => Right(EndDateMaybe(None))
      }
    }

    override def unbind(key: String, endDatePath: EndDateMaybe): String = {
      endDatePath.endDate match {
        case Some(endDate) =>
          endDate.toString
        case None =>
          ""
      }
    }
  }
}