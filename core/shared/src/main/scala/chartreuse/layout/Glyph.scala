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

import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.Color
import doodle.language.Basic
import doodle.syntax.all.*

/** Glyph describes how to turn a data point into a graphical mark (a "glyph")
  * in the plot.
  *
  * The data point has type `A` and the glyph requires algebra's `Alg` to draw.
  *
  * As this is a trait, it is open for extension. However, see the `Glyph`
  * companion object for commonly used cases. Note that `Glyph` instances should
  * not position the glyph. That is, they should not call `at` or other methods
  * that change the position of the `Picture` origin or bounding box unless that
  * is essential to correctly producing the glyph.
  */
trait Glyph[-A, Alg <: Algebra] {

  /** Given a data point, turn it into a glyph */
  def draw(data: A): Picture[Alg, Unit]
}
object Glyph {

  /** Little algebra for constructing Glyph instances that supports the common
    * operations.
    */
  enum Simple[A] extends Glyph[A, Basic] {
    case Contramap[A, B](source: Simple[A], f: B => A) extends Simple[B]
    case Shape(glyph: A => Picture[Basic, Unit])
    case Style(
        source: Simple[A],
        style: Picture[Basic, Unit] => Picture[Basic, Unit]
    )

    def contramap[B](f: B => A): Simple[B] =
      Contramap(this, f)

    def draw(data: A): Picture[Basic, Unit] =
      this match {
        case Contramap(source, f) => source.draw(f(data))
        case Shape(glyph)         => glyph(data)
        case Style(source, style) => style(source.draw(data))
      }

    def fillColor(color: Color): Simple[A] =
      Style(this, picture => picture.fillColor(color))

    def noFill: Simple[A] =
      Style(this, picture => picture.noFill)

    def strokeColor(color: Color): Simple[A] =
      Style(this, picture => picture.strokeColor(color))

    def noStroke: Simple[A] =
      Style(this, picture => picture.noStroke)

    def strokeWidth(width: Double): Simple[A] =
      Style(this, picture => picture.strokeWidth(width))
  }

  val circle: Simple[Double] =
    Simple.Shape(diameter => doodle.syntax.shape.circle[Basic](diameter))
}
