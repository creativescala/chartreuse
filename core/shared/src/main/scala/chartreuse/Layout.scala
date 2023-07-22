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

import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.algebra.Shape
import doodle.core.Point

trait Layout[A, -Alg <: Algebra] {

  /** Plot the given data, using the scale to convert from data coordinates to
    * screen coordinates.
    */
  def draw(
      data: Data[A],
      toPoint: A => Point,
      scale: Point => Point
  ): Picture[Alg, Unit]

  /** Convenience to convert to a Layer, by associating with data. */
  def toLayer[F[_]](data: F[A])(toPoint: A => Point)(using
      toData: ToData[F]
  ): Layer[A, Alg] =
    Layer(toData.toData(data))(toPoint).withLayout(this)

  /** Convenience to convert directly to a Plot, by associating with data and
    * creating a plot with a single layer.
    */
  def toLayer[F[_]](data: F[Point])(using
      toData: ToData[F],
      ev: A =:= Point
  ): Layer[A, Alg] =
    this.toLayer[F](ev.liftContra.apply(data))(a => ev.apply(a))(using toData)
}
object Layout {
  def empty[A]: Layout[A, Shape] =
    new Layout[A, Shape] {
      def draw(
          data: Data[A],
          toPoint: A => Point,
          scale: Point => Point
      ): Picture[Shape, Unit] =
        doodle.syntax.shape.empty[Shape]
    }
}
