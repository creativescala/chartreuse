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
import doodle.core.BoundingBox
import doodle.core.Point

/** A `Layer` combines data with layout and other properties required to produce
  * a plot.
  */
final case class Layer[A, -Alg <: Algebra](
    data: Data[A],
    toPoint: A => Point,
    scale: Scale,
    layout: Layout[A, Alg],
    label: String
) {
  def draw(
      width: Int,
      height: Int,
      theme: LayoutTheme[Id]
  ): Picture[Alg, Unit] = {
    val bb = data.boundingBox(toPoint)
    val s = scale.build(bb, width, height)

    layout.draw(data, toPoint, s, theme)
  }

  def boundingBox: BoundingBox =
    data.boundingBox(toPoint)

  /** Convenience to convert to a Plot with a single `Layer`.
    */
  def toPlot: Plot[Alg] =
    Plot(this)

  // Builder methods -----------------------------------------------------------

  def withLayout[AAlg <: Algebra](layout: Layout[A, AAlg]): Layer[A, AAlg] =
    this.copy(layout = layout)

  def withScale(scale: Scale): Layer[A, Alg] =
    this.copy(scale = scale)

  def withToPoint(toPoint: A => Point): Layer[A, Alg] =
    this.copy(toPoint = toPoint)

  def withLabel(label: String): Layer[A, Alg] =
    this.copy(label = label)
}
object Layer {
  def apply[A](
      data: Data[A],
      label: String = "Layer Label"
  )(toPoint: A => Point): Layer[A, Shape] =
    Layer(data, toPoint, Scale.linear, Layout.empty, label)

  def apply[A, Alg <: Algebra](data: Data[A], layout: Layout[A, Alg])(
      toPoint: A => Point
  ): Layer[A, Alg] =
    Layer(data, toPoint, Scale.linear, layout, "Layer Label")
}
