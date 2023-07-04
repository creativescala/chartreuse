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
    Alg <: Layout & Shape & Style
](
    layers: List[Layer[A, Alg]],
    plotTitle: String = "Plot Title",
    xTitle: String = "X data",
    yTitle: String = "Y data",
    grid: Boolean = false
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

  def withGrid(newGrid: Boolean): Plot[A, Alg] = {
    copy(grid = newGrid)
  }

  def draw(width: Int, height: Int): Picture[
    Alg & Text & doodle.algebra.Transform & Path & Debug,
    Unit
  ] = {
    val allData = layers.flatMap(_.data.foldLeft(List.empty[A])(_ :+ _))
    val dataBoundingBox = Data(allData).boundingBox(layers.head.toPoint)

    val minX = dataBoundingBox.left
    val maxX = dataBoundingBox.right
    val minY = dataBoundingBox.bottom
    val maxY = dataBoundingBox.top

    val scale = Scale.linear.build(dataBoundingBox, width, height)
    val minYPoint = scale(Point(0, minY)).y

    val xTicks = TickMarkCalculator.calculateTickScale(minX, maxX, 12)
    var yTicks = TickMarkCalculator.calculateTickScale(minY, maxY, 12)

    // This is needed to prevent the y-axis lowest tick to be under the x-axis
    if (scale(Point(0, yTicks.min)).y < minYPoint - 20) {
      yTicks = yTicks.copy(min = yTicks.min + yTicks.size)
    }

    val xTicksMapped = (0 to ((xTicks.max - xTicks.min) / xTicks.size).toInt)
      .map(i =>
        (
          scale(Point(xTicks.min + i * xTicks.size, 0)),
          Point(xTicks.min + i * xTicks.size, 0)
        )
      )
      .toList

    val yTicksMapped = (0 to ((yTicks.max - yTicks.min) / yTicks.size).toInt)
      .map(i =>
        (
          scale(Point(0, yTicks.min + i * yTicks.size)),
          Point(0, yTicks.min + i * yTicks.size)
        )
      )
      .toList

    val allLayers =
      layers
        .map(_.draw(width, height))
        .foldLeft(empty[Alg & Path & Text])(_ on _)

    val plotWithXTicks = xTicksMapped
      .foldLeft(allLayers)((plot, tick) =>
        plot
          .on(
            interpolatingSpline(
              List(
                Point(tick._1.x, minYPoint - 20),
                Point(tick._1.x, minYPoint - 27),
              )
            )
          )
          .on(
            text(((tick._2.x * 1000).round / 1000.0).toString)
              .at(tick._1.x, minYPoint - 40)
          )
      )

    val plotWithXAndYTicks = yTicksMapped
      .foldLeft(plotWithXTicks)((plot, tick) =>
        plot
          .on(
            interpolatingSpline(
              List(
                Point(xTicksMapped.head._1.x - 10, tick._1.y),
                Point(xTicksMapped.head._1.x - 17, tick._1.y)
              )
            )
          )
          .on(
            text(((tick._2.y * 1000).round / 1000.0).toString)
              .at(xTicksMapped.head._1.x - 45, tick._1.y)
          )
      )

    val plotWithAxes = plotWithXAndYTicks
      .on(
        interpolatingSpline(
          List(
            Point(xTicksMapped.head._1.x - 10, minYPoint - 20),
            Point(scale(Point(xTicks.max, 0)).x + 10, minYPoint - 20)
          )
        )
      )
      .on(
        interpolatingSpline(
          List(
            Point(xTicksMapped.head._1.x - 10, minYPoint - 20),
            Point(
              xTicksMapped.head._1.x - 10,
              scale(Point(0, yTicks.max)).y + 10
            )
          )
        )
      )
      .on(
        interpolatingSpline(
          List(
            Point(xTicksMapped.head._1.x - 10, scale(Point(0, yTicks.max)).y + 10),
            Point(scale(Point(xTicks.max, 0)).x + 10, scale(Point(0, yTicks.max)).y + 10)
          )
        )
      )
      .on(
        interpolatingSpline(
          List(
            Point(scale(Point(xTicks.max, 0)).x + 10, minYPoint - 20),
            Point(
              scale(Point(xTicks.max, 0)).x + 10,
              scale(Point(0, yTicks.max)).y + 10
            )
          )
        )
      )

    val plotTitle = text(this.plotTitle)
      .scale(2, 2)
    val xTitle = text(this.xTitle)
    val yTitle = text(this.yTitle)
      .rotate(Angle(1.5708))

    if (grid) {
      val gridLayout = xTicksMapped
        .foldLeft(empty[Alg & Path & Style])((plot, tick) =>
          plot
            .on(
              interpolatingSpline(
                List(
                  Point(tick._1.x, minYPoint - 20),
                  Point(tick._1.x, scale(Point(0, yTicks.max)).y + 10)
                )
              )
                .strokeColor(Color.gray)
                .strokeWidth(0.5)
            )
        )
        .on(
          yTicksMapped
            .foldLeft(empty[Alg & Path & Style])((plot, tick) =>
              plot
                .on(
                  interpolatingSpline(
                    List(
                      Point(xTicksMapped.head._1.x - 10, tick._1.y),
                      Point(
                        scale(Point(xTicks.max, 0)).x + 10,
                        tick._1.y
                      )
                    )
                  )
                    .strokeColor(Color.gray)
                    .strokeWidth(0.5)
                )
            )
        )

      return yTitle
        .beside(
          plotWithAxes
            .on(gridLayout)
            .margin(5)
            .below(plotTitle)
            .above(xTitle)
        )
    }

    yTitle
      .beside(
        plotWithAxes
          .margin(5)
          .below(plotTitle)
          .above(xTitle)
      )
  }
}
