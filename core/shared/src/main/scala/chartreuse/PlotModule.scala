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

trait PlotModule(numberFormat: NumberFormat) {

  /** A `Plot` is a collection of layers along with a title, legend, axes, and
    * grid.
    */
  final case class Plot[Alg <: Algebra](
      layers: List[Layer[?, Alg]],
      plotTitle: String = "Plot Title",
      xTitle: String = "X data",
      yTitle: String = "Y data",
      grid: Boolean = false,
      minorTicks: Boolean = false,
      tickSize: Int = 7
  ) {
    type TicksSequence = Seq[(ScreenCoordinate, DataCoordinate)]
    type PlotPicture = Picture[
      Alg & Layout & Text & Path & Style & Shape & doodle.algebra.Transform,
      Unit
    ]

    private val margin = 10

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

    def withMinorTicks(newMinorTicks: Boolean): Plot[Alg] = {
      copy(minorTicks = newMinorTicks)
    }

    def draw(width: Int, height: Int): PlotPicture = {
      val dataBoundingBox = layers.foldLeft(BoundingBox.empty) { (bb, layer) =>
        bb.on(layer.boundingBox)
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
        scale(Point(xTicks.size, 0)).x
      )
      val yTicksMapped = Ticks(
        scale(Point(0, yTicks.min)).y,
        scale(Point(0, yTicks.max)).y,
        scale(Point(0, yTicks.size)).y
      )

      // Convert the Ticks to a sequence of points
      val asX: Double => Point = x => Point(x, 0)
      val asY: Double => Point = y => Point(0, y)
      val xTicksSequence = ticksToSequence(xTicks, scale, asX)
      val yTicksSequence = ticksToSequence(yTicks, scale, asY)

      val xMinorTickInterval =
        (xTicksSequence.tail.head._1.x - xTicksSequence.head._1.x) / 4
      val yMinorTickInterval =
        (yTicksSequence.tail.head._1.y - yTicksSequence.head._1.y) / 4

      val xMinorTicksSequence = xTicksSequence.tail.flatMap {
        (screenCoordinate, _) =>
          val minorTicks = for (i <- 1 to 3) yield {
            val x = screenCoordinate.x - xMinorTickInterval * i
            (
              ScreenCoordinate(x, 0),
              DataCoordinate(x, 0)
            )
          }

          minorTicks
      }

      val yMinorTicksSequence = yTicksSequence.tail.flatMap {
        (screenCoordinate, _) =>
          val minorTicks = for (i <- 1 to 3) yield {
            val y = screenCoordinate.y - yMinorTickInterval * i
            (
              ScreenCoordinate(0, y),
              DataCoordinate(0, y)
            )
          }

          minorTicks
      }

      val allLayers =
        layers
          .map(_.draw(width, height))
          .foldLeft(empty[Alg & Layout & Shape])(_ on _)

      val createXTick: (ScreenCoordinate, Int) => OpenPath =
        (screenCoordinate, tickSize) =>
          OpenPath.empty
            .moveTo(screenCoordinate.x, yTicksMapped.min - margin)
            .lineTo(screenCoordinate.x, yTicksMapped.min - margin - tickSize)

      val createXTickLabel: (ScreenCoordinate, DataCoordinate) => PlotPicture =
        (screenCoordinate, dataCoordinate) =>
          text(numberFormat.format(dataCoordinate.x))
            .at(screenCoordinate.x, yTicksMapped.min - 30)

      val createYTick: (ScreenCoordinate, Int) => OpenPath =
        (screenCoordinate, tickSize) =>
          OpenPath.empty
            .moveTo(xTicksMapped.min - margin, screenCoordinate.y)
            .lineTo(xTicksMapped.min - margin - tickSize, screenCoordinate.y)

      val createYTickLabel: (ScreenCoordinate, DataCoordinate) => PlotPicture =
        (screenCoordinate, dataCoordinate) =>
          text(numberFormat.format(dataCoordinate.y))
            .at(xTicksMapped.min - 45, screenCoordinate.y)

      val plotTitle = text(this.plotTitle)
        .scale(2, 2)
      val xTitle = text(this.xTitle)
      val yTitle = text(this.yTitle)
        .rotate(Angle(1.5708))

      yTitle
        .beside(
          allLayers
            .on(
              withTicks(xTicksSequence, createXTick, createXTickLabel, tickSize)
            )
            .on(
              withTicks(yTicksSequence, createYTick, createYTickLabel, tickSize)
            )
            .on(withAxes(xTicksMapped, yTicksMapped))
            .on(
              if minorTicks then
                withTicks(
                  xMinorTicksSequence,
                  createXTick,
                  (_, _) => empty[Shape],
                  tickSize / 2
                )
                  .on(
                    withTicks(
                      yMinorTicksSequence,
                      createYTick,
                      (_, _) => empty[Shape],
                      tickSize / 2
                    )
                  )
              else empty[Shape]
            )
            .on(
              if grid then
                withGrid(
                  xTicksMapped,
                  yTicksMapped,
                  xTicksSequence,
                  yTicksSequence
                )
              else empty[Shape]
            )
            .margin(5)
            .below(plotTitle)
            .above(xTitle)
        )

    }

    /** Converts `Ticks` to a list of tuples. The first element is the mapped
      * coordinate of a tick, i.e. a screen coordinate - to place the tick on a
      * graph. The second one is the original coordinate, i.e. a data coordinate
      * \- to give the tick a label with its coordinate. Screen coordinates are
      * the coordinates of the graph rendered on the screen. Data coordinates
      * are the values in the data.
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

    private def withTicks(
        ticksSequence: TicksSequence,
        createTick: (ScreenCoordinate, Int) => OpenPath,
        createTickLabel: (ScreenCoordinate, DataCoordinate) => PlotPicture,
        tickSize: Int
    ): PlotPicture = {
      ticksSequence
        .foldLeft(
          empty[
            Alg & Layout & Text & Path & Style & Shape &
              doodle.algebra.Transform
          ]
        )((plot, tick) =>
          val (screenCoordinate, dataCoordinate) = tick

          plot
            .on(createTick(screenCoordinate, tickSize).path)
            .on(createTickLabel(screenCoordinate, dataCoordinate))
        )
    }

    private def withAxes(
        xTicksMapped: Ticks,
        yTicksMapped: Ticks
    ): PlotPicture = {
      ClosedPath.empty
        .moveTo(xTicksMapped.min - margin, yTicksMapped.min - margin)
        .lineTo(xTicksMapped.max + margin, yTicksMapped.min - margin)
        .lineTo(xTicksMapped.max + margin, yTicksMapped.max + margin)
        .lineTo(xTicksMapped.min - margin, yTicksMapped.max + margin)
        .path
    }

    private def withGrid(
        xTicksMapped: Ticks,
        yTicksMapped: Ticks,
        xTicksSequence: TicksSequence,
        yTicksSequence: TicksSequence
    ): PlotPicture = {
      xTicksSequence
        .foldLeft(empty[Layout & Path & Style & Shape])((plot, tick) =>
          val (screenCoordinate, _) = tick

          plot
            .on(
              OpenPath.empty
                .moveTo(screenCoordinate.x, yTicksMapped.min - margin)
                .lineTo(screenCoordinate.x, yTicksMapped.max + margin)
                .path
                .strokeColor(Color.gray)
                .strokeWidth(0.5)
            )
        )
        .on(
          yTicksSequence
            .foldLeft(empty[Layout & Path & Style & Shape])((plot, tick) =>
              val (screenCoordinate, _) = tick

              plot
                .on(
                  OpenPath.empty
                    .moveTo(xTicksMapped.min - margin, screenCoordinate.y)
                    .lineTo(xTicksMapped.max + margin, screenCoordinate.y)
                    .path
                    .strokeColor(Color.gray)
                    .strokeWidth(0.5)
                )
            )
        )
    }
  }
  object Plot {

    /** Utility constructor to create a `Plot` from a single layer. */
    def apply[Alg <: Algebra](layer: Layer[?, Alg]): Plot[Alg] =
      Plot(layers = List(layer))
  }
}
