/*
 * Copyright 2015 Creative Scala
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
import doodle.core.Point
import doodle.language.Basic
import doodle.syntax.all.*

final case class ScatterPlot[
    A,
    Alg <: doodle.algebra.Layout & doodle.algebra.Shape
](
    glyph: Glyph[Double, Alg],
    toSize: A => Double
) extends Layout[A, Alg] {
  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point
  ): Picture[Alg, Unit] = {
    data.foldLeft(empty[Alg]) { (plot, a) =>
      glyph.draw(toSize(a)).at(scale(toPoint(a))).on(plot)
    }
  }
}
object ScatterPlot {
  def default[A]: ScatterPlot[A, Basic] =
    ScatterPlot(Glyph.circle.fillColor(Color.cadetBlue), _ => 5.0)
}
