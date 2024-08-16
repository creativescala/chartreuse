/*
 * Copyright 2023 Creative Scala
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

package chartreuse.theme

import cats.Applicative
import doodle.core.Color
import doodle.core.font.*

final case class TitleTheme[F[_]: Applicative](
    family: F[FontFamily],
    style: F[FontStyle],
    weight: F[FontWeight],
    size: F[FontSize],
    strokeColor: F[Option[Color]],
    fillColor: F[Option[Color]]
) extends FontTheme[F, TitleTheme[F]] {
  def withFamily(family: F[FontFamily]): TitleTheme[F] =
    this.copy(family = family)

  def withStyle(style: F[FontStyle]): TitleTheme[F] =
    this.copy(style = style)

  def withWeight(weight: F[FontWeight]): TitleTheme[F] =
    this.copy(weight = weight)

  def withSize(size: F[FontSize]): TitleTheme[F] =
    this.copy(size = size)

  def withStrokeColor(strokeColor: F[Option[Color]]): TitleTheme[F] =
    this.copy(strokeColor = strokeColor)

  def withFillColor(fillColor: F[Option[Color]]): TitleTheme[F] =
    this.copy(fillColor = fillColor)
}
