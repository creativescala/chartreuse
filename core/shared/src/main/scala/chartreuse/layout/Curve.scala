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
import doodle.core.OpenPath
import doodle.core.Point
import doodle.syntax.all.*

/** Draws a smooth curve connecting each data point to its neighbour. Data is
  * rendered in the order in which it is found.
  */
final case class Curve[
    A,
    Alg <: doodle.algebra.Style & doodle.algebra.Path
](
    themeable: LayoutTheme[Themeable],
    tension: Double
) extends Layout[A, Alg] {
  type Self = Curve[A, Alg]

  def forThemeable(
      f: LayoutTheme[Themeable] => LayoutTheme[Themeable]
  ): Curve[A, Alg] =
    this.copy(themeable = f(themeable))

  def withTension(tension: Double): Curve[A, Alg] =
    this.copy(tension = tension)

  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point,
      theme: LayoutTheme[Id]
  ): Picture[Alg, Unit] = {
    val curve = OpenPath
      .catmulRom(
        data
          .foldLeft(List.empty[Point]) { (path, a) =>
            scale(toPoint(a)) :: path
          }
          .reverse
      )
      .path

    theme.theme(themeable)(curve)
  }
}
object Curve {
  def default[A]: Curve[A, doodle.algebra.Style & doodle.algebra.Path] =
    Curve(
      // Disable fill to avoid filling the open path
      LayoutTheme.default[Themeable].withFillColor(Themeable.Override(None)),
      0.5
    )
}
