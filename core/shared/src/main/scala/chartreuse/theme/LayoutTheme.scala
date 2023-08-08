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
import cats.Comonad
import cats.Id
import cats.syntax.all.*
import chartreuse.Themeable
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.algebra.Style
import doodle.core.Color
import doodle.syntax.all.*

/** Generic container for the theme of a [[chartreuse.Layout]]. The type
  * [[this.F!]] determines the wrapper type for values in this theme. It should
  * be
  *
  *   - [[chartreuse.Themeable]] for values that can have a theme applied to
  *     them
  *   - [[cats.Id]] for values that have combined [[chartreuse.Themeable]] with
  *     a theme.
  */
// This doesn't cover all the possibilities for themeing layouts. For example,
// there is no stroke dash specified. This is a first pass. Extend it if needed.
final case class LayoutTheme[F[_]: Applicative](
    strokeColor: F[Option[Color]],
    strokeWidth: F[Double],
    fillColor: F[Option[Color]]
) {
  def withFillColor(fillColor: Color): LayoutTheme[F] =
    this.copy(fillColor = fillColor.some.pure[F])

  def withNoFill: LayoutTheme[F] =
    this.copy(fillColor = none.pure[F])

  def withStrokeColor(strokeColor: Color): LayoutTheme[F] =
    this.copy(strokeColor = strokeColor.some.pure[F])

  def withNoStroke: LayoutTheme[F] =
    this.copy(strokeColor = none.pure[F])

  def withStrokeWidth(strokeWidth: Double): LayoutTheme[F] =
    this.copy(strokeWidth = strokeWidth.pure[F])

  def theme(themeable: LayoutTheme[Themeable])(using
      Comonad[F]
  ): LayoutTheme[Id] =
    LayoutTheme(
      themeable.strokeColor.theme(strokeColor.extract).pure[Id],
      themeable.strokeWidth.theme(strokeWidth.extract).pure[Id],
      themeable.fillColor.theme(fillColor.extract).pure[Id]
    )

  /** Apply the style in this theme to a [[doodle.algebra.Picture]] */
  def apply[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  )(using Comonad[F]): Picture[Alg & Style, A] = {
    val p1 =
      strokeColor.extract.fold(picture.noStroke)(c => picture.strokeColor(c))
    val p2 =
      fillColor.extract.fold(p1.noFill)(c => p1.fillColor(c))

    p2.strokeWidth(strokeWidth.extract)
  }
}
object LayoutTheme {

  /** A default theme, with a black stroke and no fill. */
  def default[F[_]: Applicative]: LayoutTheme[F] =
    LayoutTheme(Color.black.some.pure[F], 1.0.pure[F], none.pure[F])
}
