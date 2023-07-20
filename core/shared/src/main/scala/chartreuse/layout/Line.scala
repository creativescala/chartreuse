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

import chartreuse.*
import doodle.algebra.Picture
import doodle.core.Color
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
    strokeColor: Color,
    strokeWidth: Double
) extends Layout[A, Alg] {
  def withStrokeColor(strokeColor: Color): Line[A, Alg] =
    this.copy(strokeColor = strokeColor)

  def withStrokeWidth(strokeWidth: Double): Line[A, Alg] =
    this.copy(strokeWidth = strokeWidth)

  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point
  ): Picture[Alg, Unit] = {
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
        path.path.strokeColor(strokeColor).strokeWidth(strokeWidth)
    }
  }
}
object Line {
  def default[A]: Line[
    A,
    doodle.algebra.Shape & doodle.algebra.Style & doodle.algebra.Path
  ] =
    Line(Color.black, 1.0)
}
