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

package chartreuse

import chartreuse.layout.ScatterPlot
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

/** A `Plot` is a collection of layers along with a title, legend, axes, and
  * grid.
  */
final case class Plot[
    A,
    Alg <: Layout & Shape
](
    layers: List[Layer[A, Alg]],
    plotTitle: String = "Plot Title",
    xTitle: String = "X data",
    yTitle: String = "Y data",
    grid: Boolean = true
) {
  def addLayer(layer: Layer[A, Alg]): Plot[A, Alg] = {
    copy(layers = layer :: layers)
  }

  def withPlotTitle(newPlotTitle: String): Plot[A, Alg] = {
    copy(plotTitle = newPlotTitle)
  }

  def withXTitle(newXTitle: String): Plot[A, Alg] = {
    copy(xTitle = newXTitle)
  }

  def withYTitle(newYTitle: String): Plot[A, Alg] = {
    copy(yTitle = newYTitle)
  }

  //  TODO: handle axes and grid layout
  def draw(width: Int, height: Int): Picture[
    Alg & Text & doodle.algebra.Transform & Debug,
    Unit
  ] = {
    val allData = layers.flatMap(_.data.foldLeft(List.empty[A])(_ :+ _))
    val dataBoundingBox = Data(allData).boundingBox(layers.head.toPoint)

    val minX = dataBoundingBox.left
    val maxX = dataBoundingBox.right
    val minY = dataBoundingBox.bottom
    val maxY = dataBoundingBox.top

    println(minX)
    println(maxX)
    println(minY)
    println(maxY)

    println(TickMarkCalculator.calculateTickScale(minX, maxX, 12))
    println(TickMarkCalculator.calculateTickScale(minY, maxY, 12))

    val plot =
      layers
        .map(_.draw(width, height))
        .foldLeft(empty[Alg])(_ on _)
        .margin(20)
        .debug(color = Color.black)
        .margin(5)

    val plotTitle = text(this.plotTitle)
      .scale(2, 2)
    val xTitle = text(this.xTitle)
    val yTitle = text(this.yTitle)
      .rotate(Angle(1.5708))

    yTitle
      .beside(
        plot
          .below(plotTitle)
          .above(xTitle)
      )
  }
}
