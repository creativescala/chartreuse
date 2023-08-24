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

/** Draws a straight line connecting each data point to its neighbour. Data is
  * rendered in the order in which it is found.
  */
final case class Line[
    A,
    Alg <: doodle.algebra.Shape & doodle.algebra.Style & doodle.algebra.Path
](
    themeable: LayoutTheme[Themeable]
) extends Layout[A, Alg] {
  type Self = Line[A, Alg]

  def forThemeable(
      f: LayoutTheme[Themeable] => LayoutTheme[Themeable]
  ): Line[A, Alg] =
    this.copy(themeable = f(themeable))

  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point,
      theme: LayoutTheme[Id]
  ): Picture[Alg, Unit] = {
    val line =
      data
        .foldLeft(None: Option[OpenPath]) { (path, a) =>
          path match {
            case None =>
              Some(OpenPath.empty.moveTo(scale(toPoint(a))))
            case Some(p) =>
              Some(p.lineTo(scale(toPoint(a))))
          }
        } match {
        case None => empty
        case Some(path) =>
          path.path
      }

    theme.theme(themeable)(line)
  }
}
object Line {
  def default[A]: Line[
    A,
    doodle.algebra.Shape & doodle.algebra.Style & doodle.algebra.Path
  ] =
    Line(
      // Disable fill to avoid filling the open path
      LayoutTheme.default[Themeable].withFillColor(Themeable.Override(None))
    )
}
