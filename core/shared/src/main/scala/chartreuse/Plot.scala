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

import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

/** A `Plot` is a collection of layers along with a title, legend, axes, and
  * grid.
  */
final case class Plot[-Alg <: Algebra](
    layers: List[Layer[?, Alg]],
    plotTitle: String = "Plot Title",
    xTitle: String = "X data",
    yTitle: String = "Y data",
    grid: Boolean = false,
    xTicks: MajorTickLayout = MajorTickLayout.Algorithmic(12),
    yTicks: MajorTickLayout = MajorTickLayout.Algorithmic(12),
    minorTicks: MinorTickLayout = MinorTickLayout.NoTicks
) {
  def addLayer[Alg2 <: Algebra](layer: Layer[?, Alg2]): Plot[Alg & Alg2] = {
    copy(layers = layer :: layers)
  }

  def withPlotTitle(newPlotTitle: String): Plot[Alg] = {
    copy(plotTitle = newPlotTitle)
  }

  def withXTitle(newXTitle: String): Plot[Alg] = {
    copy(xTitle = newXTitle)
  }

  def withYTitle(newYTitle: String): Plot[Alg] = {
    copy(yTitle = newYTitle)
  }

  def withGrid(newGrid: Boolean): Plot[Alg] = {
    copy(grid = newGrid)
  }

  def withMinorTicks(
      newMinorTicks: MinorTickLayout = MinorTickLayout.Algorithmic(3)
  ): Plot[Alg] = {
    copy(minorTicks = newMinorTicks)
  }

  def withXTicks(newXTicks: MajorTickLayout): Plot[Alg] = {
    copy(xTicks = newXTicks)
  }
  
  def withYTicks(newYTicks: MajorTickLayout): Plot[Alg] = {
    copy(yTicks = newYTicks)
  }

  def draw(width: Int, height: Int): Picture[Alg & PlotAlg, Unit] = {
    val axes = Axes(xTicks, yTicks, minorTicks, grid, layers, width, height)
    val plotAttributes = axes.build

    val allLayers: Picture[Alg & PlotAlg, Unit] =
      layers
        .map(_.draw(width, height))
        .foldLeft(empty)(_ on _)

    val plotTitle = text(this.plotTitle)
      .scale(2, 2)
    val xTitle = text(this.xTitle)
    val yTitle = text(this.yTitle)
      .rotate(Angle(1.5708))

    yTitle
      .beside(
        allLayers
          .on(
            plotAttributes
          )
          .margin(5)
          .below(plotTitle)
          .above(xTitle)
      )
  }
}

object Plot {

  /** Utility constructor to create a `Plot` from a single layer. */
  def apply[Alg <: Algebra](layer: Layer[?, Alg]): Plot[Alg] =
    Plot(layers = List(layer))
}
