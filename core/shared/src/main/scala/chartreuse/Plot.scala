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
import chartreuse.Coordinate.*

/** A `Plot` is a collection of layers along with a title, legend, axes, and
  * grid.
  */
final case class Plot[A, Alg <: Algebra](
    layers: List[Layer[A, Alg]],
    plotTitle: String = "Plot Title",
    xTitle: String = "X data",
    yTitle: String = "Y data",
    grid: Boolean = false
) {
  type TicksSequence = Seq[(ScreenCoordinate, DataCoordinate)]
  type PlotPicture = Picture[
    Alg & Layout & Text & Path & Style & Shape & doodle.algebra.Transform,
    Unit
  ]

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

  def draw(width: Int, height: Int): PlotPicture = {
    val dataBoundingBox = layers.foldLeft(BoundingBox.empty) { (bb, layer) =>
      bb.on(layer.data.boundingBox(layer.toPoint))
    }

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
    val asX: Double => Point = x => Point(x, 0)
    val asY: Double => Point = y => Point(0, y)
    val xTicksSequence = ticksToSequence(xTicks, scale, asX)
    val yTicksSequence = ticksToSequence(yTicks, scale, asY)

    val allLayers =
      layers
        .map(_.draw(width, height))
        .foldLeft(empty[Alg & Layout & Shape])(_ on _)

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
      withTitles(plotWithTicksAndAxesAndGrid)
    } else {
      withTitles(plotWithTicksAndAxes)
    }
  }

  /** Converts `Ticks` to a list of tuples. The first element is the mapped
    * coordinate of a tick, i.e. a screen coordinate - to place the tick on a
    * graph. The second one is the original coordinate, i.e. a data coordinate -
    * to give the tick a label with its coordinate. Screen coordinates are the
    * coordinates of the graph rendered on the screen. Data coordinates are the
    * values in the data.
    */
  private def ticksToSequence(
      ticks: Ticks,
      scale: Bijection[Point, Point],
      toPoint: Double => Point
  ): TicksSequence = {
    (0 to ((ticks.max - ticks.min) / ticks.size).toInt)
      .map(i =>
        (
          ScreenCoordinate(scale(toPoint(ticks.min + i * ticks.size))),
          DataCoordinate(toPoint(ticks.min + i * ticks.size))
        )
      )
      .toList
  }

  private def withXTicks(
      xTicksSequence: TicksSequence,
      plot: PlotPicture,
      yTicksMapped: Ticks
  ): PlotPicture = {
    xTicksSequence
      .foldLeft(plot)((plot, tick) =>
        val (screenCoordinate, dataCoordinate) = tick

        plot
          .on(
            OpenPath.empty
              .moveTo(screenCoordinate.x, yTicksMapped.min - 10)
              .lineTo(screenCoordinate.x, yTicksMapped.min - 17)
              .path
          )
          .on(
            text(((dataCoordinate.x * 1000).round / 1000.0).toString)
              .at(screenCoordinate.x, yTicksMapped.min - 30)
          )
      )
  }

  private def withYTicks(
      yTicksSequence: TicksSequence,
      plot: PlotPicture,
      xTicksMapped: Ticks
  ): PlotPicture = {
    yTicksSequence
      .foldLeft(plot)((plot, tick) =>
        val (screenCoordinate, dataCoordinate) = tick

        plot
          .on(
            OpenPath.empty
              .moveTo(xTicksMapped.min - 10, screenCoordinate.y)
              .lineTo(xTicksMapped.min - 17, screenCoordinate.y)
              .path
          )
          .on(
            text(((dataCoordinate.y * 1000).round / 1000.0).toString)
              .at(xTicksMapped.min - 45, screenCoordinate.y)
          )
      )
  }

  private def withAxes(
      plot: PlotPicture,
      xTicksMapped: Ticks,
      yTicksMapped: Ticks
  ): PlotPicture = {
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
      plot: PlotPicture,
      xTicksMapped: Ticks,
      yTicksMapped: Ticks,
      xTicksSequence: TicksSequence,
      yTicksSequence: TicksSequence
  ): PlotPicture = {
    plot.on(
      xTicksSequence
        .foldLeft(empty[Alg & Layout & Text & Path & Style & Shape])(
          (plot, tick) =>
            val (screenCoordinate, _) = tick

            plot
              .on(
                OpenPath.empty
                  .moveTo(screenCoordinate.x, yTicksMapped.min - 20)
                  .lineTo(screenCoordinate.x, yTicksMapped.max + 10)
                  .path
                  .strokeColor(Color.gray)
                  .strokeWidth(0.5)
              )
        )
        .on(
          yTicksSequence
            .foldLeft(empty[Alg & Layout & Text & Path & Style & Shape])(
              (plot, tick) =>
                val (screenCoordinate, _) = tick

                plot
                  .on(
                    OpenPath.empty
                      .moveTo(xTicksMapped.min - 10, screenCoordinate.y)
                      .lineTo(xTicksMapped.max + 10, screenCoordinate.y)
                      .path
                      .strokeColor(Color.gray)
                      .strokeWidth(0.5)
                  )
            )
        )
    )
  }

  private def withTitles(plot: PlotPicture): PlotPicture = {
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
