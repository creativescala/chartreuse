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
    Alg <: Layout & Shape & Style & Text & doodle.algebra.Transform & Path
](
    layers: List[Layer[A, Alg]],
    plotTitle: String = "Plot Title",
    xTitle: String = "X data",
    yTitle: String = "Y data",
    grid: Boolean = false
) {
  type TicksSequence = Seq[(Point, Point)]

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
    Alg,
    Unit
  ] = {
    val allData = layers.flatMap(_.data.foldLeft(List.empty[A])(_ :+ _))
    val dataBoundingBox = Data(allData).boundingBox(layers.head.toPoint)

    val minX = dataBoundingBox.left
    val maxX = dataBoundingBox.right
    val minY = dataBoundingBox.bottom
    val maxY = dataBoundingBox.top

    val scale = Scale.linear.build(dataBoundingBox, width, height)

    val xTicks = TickMarkCalculator.calculateTickScale(minX, maxX, 12)
    val yTicks = TickMarkCalculator.calculateTickScale(minY, maxY, 12)

    // Map the Ticks to the screen coordinates
    val xTicksMapped = Ticks(
      scale(Point(xTicks.min, 0)).x,
      scale(Point(xTicks.max, 0)).x,
      xTicks.size
    )
    val yTicksMapped = Ticks(
      scale(Point(0, yTicks.min)).y,
      scale(Point(0, yTicks.max)).y,
      yTicks.size
    )

    // Convert the Ticks to a sequence of points
    val xTicksSequence = xTicksToSequence(xTicks, scale)
    val yTicksSequence = yTicksToSequence(yTicks, scale)

    val allLayers =
      layers
        .map(_.draw(width, height))
        .foldLeft(empty[Alg])(_ on _)

    val plotWithXTicks = withXTicks(xTicksSequence, allLayers, yTicksMapped)
    val plotWithXAndYTicks =
      withYTicks(yTicksSequence, plotWithXTicks, xTicksMapped)
    val plotWithTicksAndAxes =
      withAxes(plotWithXAndYTicks, xTicksMapped, yTicksMapped)

    if (grid) {
      val plotWithTicksAndAxesAndGrid = withGrid(
        plotWithTicksAndAxes,
        xTicksMapped,
        yTicksMapped,
        xTicksSequence,
        yTicksSequence
      )
      return withTitles(plotWithTicksAndAxesAndGrid)
    }

    withTitles(plotWithTicksAndAxes)
  }

  /** Converts `Ticks` to a list of tuples. The first element is the mapped
    * coordinate of a tick - to place the tick on a graph. The second one is the
    * original coordinate - to give the tick a label with its coordinate.
    */
  private def xTicksToSequence(
      xTicks: Ticks,
      scale: Bijection[Point, Point]
  ): TicksSequence = {
    (0 to ((xTicks.max - xTicks.min) / xTicks.size).toInt)
      .map(i =>
        (
          scale(Point(xTicks.min + i * xTicks.size, 0)),
          Point(xTicks.min + i * xTicks.size, 0)
        )
      )
      .toList
  }

  /** Converts `Ticks` to a list of tuples. The first element is the mapped
    * coordinate of a tick - to place the tick on a graph. The second one is the
    * original coordinate - to give the tick a label with its coordinate.
    */
  private def yTicksToSequence(
      yTicks: Ticks,
      scale: Bijection[Point, Point]
  ): TicksSequence = {
    (0 to ((yTicks.max - yTicks.min) / yTicks.size).toInt)
      .map(i =>
        (
          scale(Point(0, yTicks.min + i * yTicks.size)),
          Point(0, yTicks.min + i * yTicks.size)
        )
      )
      .toList
  }

  private def withXTicks(
      xTicksSequence: TicksSequence,
      plot: Picture[Alg, Unit],
      yTicksMapped: Ticks
  ) = {
    xTicksSequence
      .foldLeft(plot)((plot, tick) =>
        plot
          .on(
            OpenPath.empty
              .moveTo(tick._1.x, yTicksMapped.min - 10)
              .lineTo(tick._1.x, yTicksMapped.min - 17)
              .path
          )
          .on(
            text(((tick._2.x * 1000).round / 1000.0).toString)
              .at(tick._1.x, yTicksMapped.min - 30)
          )
      )
  }

  private def withYTicks(
      yTicksSequence: TicksSequence,
      plot: Picture[Alg, Unit],
      xTicksMapped: Ticks
  ) = {
    yTicksSequence
      .foldLeft(plot)((plot, tick) =>
        plot
          .on(
            OpenPath.empty
              .moveTo(xTicksMapped.min - 10, tick._1.y)
              .lineTo(xTicksMapped.min - 17, tick._1.y)
              .path
          )
          .on(
            text(((tick._2.y * 1000).round / 1000.0).toString)
              .at(xTicksMapped.min - 45, tick._1.y)
          )
      )
  }

  private def withAxes(
      plot: Picture[Alg, Unit],
      xTicksMapped: Ticks,
      yTicksMapped: Ticks
  ) = {
    plot
      .on(
        ClosedPath.empty
          .moveTo(xTicksMapped.min - 10, yTicksMapped.min - 10)
          .lineTo(xTicksMapped.max + 10, yTicksMapped.min - 10)
          .lineTo(xTicksMapped.max + 10, yTicksMapped.max + 10)
          .lineTo(xTicksMapped.min - 10, yTicksMapped.max + 10)
          .path
      )
  }

  private def withGrid(
      plot: Picture[Alg, Unit],
      xTicksMapped: Ticks,
      yTicksMapped: Ticks,
      xTicksSequence: TicksSequence,
      yTicksSequence: TicksSequence
  ) = {
    plot.on(
      xTicksSequence
        .foldLeft(empty[Alg])((plot, tick) =>
          plot
            .on(
              OpenPath.empty
                .moveTo(tick._1.x, yTicksMapped.min - 20)
                .lineTo(tick._1.x, yTicksMapped.max + 10)
                .path
                .strokeColor(Color.gray)
                .strokeWidth(0.5)
            )
        )
        .on(
          yTicksSequence
            .foldLeft(empty[Alg])((plot, tick) =>
              plot
                .on(
                  OpenPath.empty
                    .moveTo(xTicksMapped.min - 10, tick._1.y)
                    .lineTo(xTicksMapped.max + 10, tick._1.y)
                    .path
                    .strokeColor(Color.gray)
                    .strokeWidth(0.5)
                )
            )
        )
    )
  }

  private def withTitles(plot: Picture[Alg, Unit]) = {
    val plotTitle = text(this.plotTitle)
      .scale(2, 2)
    val xTitle = text(this.xTitle)
    val yTitle = text(this.yTitle)
      .rotate(Angle(1.5708))

    yTitle
      .beside(
        plot
          .margin(5)
          .below(plotTitle)
          .above(xTitle)
      )
  }
}
