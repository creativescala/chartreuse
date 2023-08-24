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

package chartreuse.layout

import cats.Id
import chartreuse.*
import chartreuse.theme.LayoutTheme
import doodle.algebra.Picture
import doodle.core.Point
import doodle.syntax.all.*

final case class Scatter[
    A,
    Alg <: doodle.algebra.Algebra
](
    themeable: LayoutTheme[Themeable],
    glyph: Glyph[Double, Alg],
    toSize: A => Double
) extends Layout[A, Alg & doodle.algebra.Layout & doodle.algebra.Shape] {
  def withThemeable(themeable: LayoutTheme[Themeable]): Scatter[A, Alg] =
    this.copy(themeable = themeable)

  def forThemeable(
      f: LayoutTheme[Themeable] => LayoutTheme[Themeable]
  ): Scatter[A, Alg] =
    this.copy(themeable = f(themeable))

  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point,
      theme: LayoutTheme[Id]
  ): Picture[Alg & doodle.algebra.Layout & doodle.algebra.Shape, Unit] = {
    val plot =
      data.foldLeft(empty[Alg & doodle.algebra.Layout & doodle.algebra.Shape]) {
        (plot, a) =>
          glyph
            .draw(toSize(a), theme.theme(themeable))
            .at(scale(toPoint(a)))
            .on(plot)
      }
    plot
  }
}
object Scatter {
  def default[A]: Scatter[
    A,
    doodle.algebra.Shape & doodle.algebra.Style
  ] =
    Scatter(
      LayoutTheme.default[Themeable],
      Glyph.circle,
      _ => 5.0
    )
}
