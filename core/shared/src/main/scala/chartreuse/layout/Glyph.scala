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
import chartreuse.theme.LayoutTheme
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.algebra.Shape
import doodle.algebra.Style

/** Glyph describes how to turn a data point into a graphical mark (a "glyph")
  * in the plot.
  *
  * The data point has type `A` and the glyph requires algebra's `Alg` to draw.
  *
  * As this is a trait, it is open for extension. However, see the `Glyph`
  * companion object for commonly used cases. Note that `Glyph` instances should
  * not position the glyph. That is, they should not call `at` or other methods
  * that change the position of the `Picture` origin or bounding box unless that
  * is essential to correctly producing the glyph. However, they should style
  * the glyph using the information contained in the the `theme`.
  */
trait Glyph[-A, Alg <: Algebra] {

  /** Given a data point, turn it into a glyph */
  def draw(data: A, theme: LayoutTheme[Id]): Picture[Alg, Unit]
}
object Glyph {

  /** Create a `Glyph` from a function that produces a `Picture` given some
    * input. The function does not need to do any themeing of its output.
    */
  def apply[A, Alg <: Algebra](f: A => Picture[Alg, Unit]) =
    new Glyph[A, Alg & Style] {
      def draw(data: A, theme: LayoutTheme[Id]): Picture[Alg & Style, Unit] =
        theme(f(data))
    }

  /** Create a `Glyph` that draws a circle of the given size. */
  val circle: Glyph[Double, Shape & Style] =
    apply((size: Double) => doodle.syntax.shape.circle(size))
}
