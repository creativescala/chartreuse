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

package chartreuse

import cats.Id
import chartreuse.theme.LayoutTheme
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.algebra.Shape
import doodle.core.Point

/** A `Layout` determines the visual appearance of data. */
trait Layout[A, -Alg <: Algebra] {

  /** This should be the type of the concrete subclass that extends `Layout`. We
    * use this instead of F-bound polymorphism to avoid putting a confusing type
    * in the type signature of `Layout`. This means that `Self` is not
    * constrained in the same way as it would be using F-bound polymorphism but
    * in practice an issue is not likely to arise.
    */
  type Self <: Layout[A, Alg]

  /** The themeable values associated with this `Layout`. */
  def themeable: LayoutTheme[Themeable]

  /** Builder method to change the themeable values of this `Layout`. */
  def forThemeable(
      f: LayoutTheme[Themeable] => LayoutTheme[Themeable]
  ): Self

  /** Convenience builder to change all themeable values in one go. */
  def withThemeable(themeable: LayoutTheme[Themeable]): Self =
    forThemeable(_ => themeable)

  /** Plot the given data, using the scale to convert from data coordinates to
    * screen coordinates.
    *
    * The `theme` has not been combined with this `Layouts` `themeable` value.
    * The `Layout` should do that itself.
    */
  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point,
      theme: LayoutTheme[Id]
  ): Picture[Alg, Unit]

  /** Convenience to convert to a `Layer`, by associating this `Layout` with
    * data and a function to convert data elements to `Point`.
    */
  def toLayer[F[_]](data: F[A])(toPoint: A => Point)(using
      toData: ToData[F]
  ): Layer[A, Alg] =
    Layer(toData.toData(data))(toPoint).withLayout(this)

  /** Convenience to convert directly to a `Layer`, by associating this `Layout`
    * with data. Use this variant when the data already consists of `Points`.
    */
  def toLayer[F[_]](data: F[Point])(using
      toData: ToData[F],
      ev: A =:= Point
  ): Layer[A, Alg] =
    this.toLayer[F](ev.liftContra.apply(data))(a => ev.apply(a))(using toData)

  /** Convenience to convert to a Plot, by associating this `Layout` with data
    * and a function to convert data elements to `Point` and creating a `Plot`
    * with a single `Layer`.
    */
  def toPlot[F[_]](data: F[A])(toPoint: A => Point)(using
      toData: ToData[F]
  ): Plot[Alg] =
    Plot(toLayer(data)(toPoint)(using toData))

  /** Convenience to convert to a Plot, by associating this `Layout` with data
    * and creating a `Plot` with a single `Layer`. Use this variant when the
    * data already consists of `Points`.
    */
  def toPlot[F[_]](data: F[Point])(using
      toData: ToData[F],
      ev: A =:= Point
  ): Plot[Alg] =
    Plot(toLayer(data)(using toData, ev))
}
object Layout {
  final case class Empty[A](
      themeable: LayoutTheme[Themeable] = LayoutTheme.default[Themeable]
  ) extends Layout[A, Shape] {
    type Self = Empty[A]
    def forThemeable(
        f: LayoutTheme[chartreuse.Themeable] => LayoutTheme[
          chartreuse.Themeable
        ]
    ): Empty[A] =
      this.copy(themeable = f(themeable))
    def draw(
        data: Data[A],
        toPoint: A => Point,
        scale: Point => Point,
        theme: LayoutTheme[Id]
    ): Picture[Shape, Unit] =
      doodle.syntax.shape.empty[Shape]
  }

  /** The `Layout` that draws nothing. */
  def empty[A]: Layout[A, Shape] = Empty()
}
