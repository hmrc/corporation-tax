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

package uk.gov.hmrc.corporationtax.helpers

import uk.gov.hmrc.corporationtax.models.AdminRule

import java.time.LocalDate

trait AdminRuleHelper {
  val emptyAdminRule: AdminRule    = AdminRule(None, None)
  val example1adminRule: AdminRule = AdminRule(Some(BigDecimal(3)), Some(LocalDate.of(1997, 7, 1)))
  val example2adminRule: AdminRule = AdminRule(Some(BigDecimal(56)), Some(LocalDate.of(2001, 8, 12)))
  val example3adminRule: AdminRule = AdminRule(Some(BigDecimal(87)), None)
  val example4adminRule: AdminRule = AdminRule(None, Some(LocalDate.of(2012, 4, 8)))
}
