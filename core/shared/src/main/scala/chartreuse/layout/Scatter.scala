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

/** A [[chartreuse.Layout]] that represents each data point as a small graphical
  * mark, known as a [[chartreuse.layout.Glyph]].
  *
  * It is typically used to create scatter plots, but because the `Glyph` is
  * parameterized by a `Double` value, which is interpreted as some measure of
  * size, it can also be used to create bubble plots.
  */
final case class Scatter[
    A,
    Alg <: doodle.algebra.Algebra
](
    themeable: LayoutTheme[Themeable],
    glyph: Glyph[Double, Alg],
    toSize: A => Double
) extends Layout[A, Alg & doodle.algebra.Layout & doodle.algebra.Shape] {
  type Self = Scatter[A, Alg]

  def forThemeable(
      f: LayoutTheme[Themeable] => LayoutTheme[Themeable]
  ): Scatter[A, Alg] =
    this.copy(themeable = f(themeable))

  /** Change the function that determines the size of a data point to the given
    * function.
    */
  def withToSize(toSize: A => Double): Scatter[A, Alg] =
    this.copy(toSize = toSize)

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
