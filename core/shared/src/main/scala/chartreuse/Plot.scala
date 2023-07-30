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
    minorTicks: Boolean = false,
    tickSize: Int = 7,
    xUserTicks: Option[List[Double]] = None,
    yUserTicks: Option[List[Double]] = None
) {
  type TicksSequence = Seq[(ScreenCoordinate, DataCoordinate)]
  type AAlg >: Alg
  type PlotAlg = AAlg & Layout & Text & Path & Style & Shape &
    doodle.algebra.Transform
  type PlotPicture = Picture[PlotAlg, Unit]

  private val axisMargin = 10
  private val textMargin = axisMargin + tickSize + 5
  private val majorTickCount = 12
  private val minorTickCount = 3

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

  def withXUserTicks(newXUserTicks: List[Double]): Plot[Alg] = {
    copy(xUserTicks = Some(newXUserTicks))
  }

  def withYUserTicks(newYUserTicks: List[Double]): Plot[Alg] = {
    copy(yUserTicks = Some(newYUserTicks))
  }

  def draw(width: Int, height: Int)(using
      numberFormat: NumberFormat
  ): PlotPicture = {
    val dataBoundingBox = layers.foldLeft(BoundingBox.empty) { (bb, layer) =>
      bb.on(layer.boundingBox)
    }

    val minX = dataBoundingBox.left
    val maxX = dataBoundingBox.right
    val minY = dataBoundingBox.bottom
    val maxY = dataBoundingBox.top

    val scale = Scale.linear.build(dataBoundingBox, width, height)

    val xTicks =
      TickMarkCalculator.calculateTickScale(minX, maxX, majorTickCount)
    val yTicks =
      TickMarkCalculator.calculateTickScale(minY, maxY, majorTickCount)

    // Map the Ticks to the screen coordinates
    val xTicksMapped = Ticks(
      scale(Point(xTicks.min, 0)).x,
      scale(Point(xTicks.max, 0)).x,
      scale(Point(xTicks.min + xTicks.size, 0)).x - scale(
        Point(xTicks.min, 0)
      ).x
    )
    val yTicksMapped = Ticks(
      scale(Point(0, yTicks.min)).y,
      scale(Point(0, yTicks.max)).y,
      scale(Point(0, yTicks.min + yTicks.size)).y - scale(
        Point(0, yTicks.min)
      ).y
    )

    // Convert the Ticks to a sequence of points
    val asX: Double => Point = x => Point(x, 0)
    val asY: Double => Point = y => Point(0, y)
    val xFilter: Double => Boolean = tick =>
      tick >= xTicks.min && tick <= xTicks.max
    val yFilter: Double => Boolean = tick =>
      tick >= xTicks.min && tick <= yTicks.max

    val xTicksSequence: TicksSequence =
      ticksToSequence(xUserTicks, scale, asX, xFilter, xTicks)
    val yTicksSequence: TicksSequence =
      ticksToSequence(yUserTicks, scale, asY, yFilter, yTicks)

    val xMajorTickToMinorTick: (ScreenCoordinate, Double, Int) => (
        ScreenCoordinate,
        DataCoordinate
    ) = (screenCoordinate, interval, i) => {
      val x = screenCoordinate.x - interval * i
      (
        ScreenCoordinate(x, 0),
        DataCoordinate(x, 0)
      )
    }

    val yMajorTickToMinorTick: (ScreenCoordinate, Double, Int) => (
        ScreenCoordinate,
        DataCoordinate
    ) = (screenCoordinate, interval, i) => {
      val y = screenCoordinate.y - interval * i
      (
        ScreenCoordinate(0, y),
        DataCoordinate(0, y)
      )
    }

    val xMinorTicksInterval = xTicksMapped.size / (minorTickCount + 1)
    val yMinorTicksInterval = yTicksMapped.size / (minorTickCount + 1)

    val xMinorTicksSequence = convertToMinorTicks(
      xTicksSequence,
      xMinorTicksInterval,
      xMajorTickToMinorTick
    )
    val yMinorTicksSequence = convertToMinorTicks(
      yTicksSequence,
      yMinorTicksInterval,
      yMajorTickToMinorTick
    )

    val allLayers: PlotPicture =
      layers
        .map(_.draw(width, height))
        .foldLeft(empty[PlotAlg])(
          _ on _.asInstanceOf[PlotPicture]
        )

    val createXTick: (ScreenCoordinate, Int) => OpenPath =
      (screenCoordinate, tickSize) =>
        OpenPath.empty
          .moveTo(screenCoordinate.x, yTicksMapped.min - axisMargin)
          .lineTo(
            screenCoordinate.x,
            yTicksMapped.min - axisMargin - tickSize
          )

    val createXTickLabel: (ScreenCoordinate, DataCoordinate) => PlotPicture =
      (screenCoordinate, dataCoordinate) =>
        text(numberFormat.format(dataCoordinate.x))
          .originAt(Landmark.percent(0, 100))
          .at(screenCoordinate.x, yTicksMapped.min - textMargin)

    val createYTick: (ScreenCoordinate, Int) => OpenPath =
      (screenCoordinate, tickSize) =>
        OpenPath.empty
          .moveTo(xTicksMapped.min - axisMargin, screenCoordinate.y)
          .lineTo(
            xTicksMapped.min - axisMargin - tickSize,
            screenCoordinate.y
          )

    val createYTickLabel: (ScreenCoordinate, DataCoordinate) => PlotPicture =
      (screenCoordinate, dataCoordinate) =>
        text(numberFormat.format(dataCoordinate.y))
          .originAt(Landmark.percent(100, 0))
          .at(xTicksMapped.min - textMargin, screenCoordinate.y)

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

  private def ticksToSequence(
      userTicks: Option[List[Double]],
      scale: Bijection[Point, Point],
      toPoint: Double => Point,
      filter: Double => Boolean,
      automaticTicks: Ticks
  ): TicksSequence = {
    userTicks match {
      case Some(ticks) =>
        userTicksToSequence(ticks, scale, toPoint, filter)
      case None =>
        automaticTicksToSequence(automaticTicks, scale, toPoint)
    }
  }

  /** Converts `Ticks` to a list of tuples. The first element is the mapped
    * coordinate of a tick, i.e. a screen coordinate - to place the tick on a
    * graph. The second one is the original coordinate, i.e. a data coordinate
    * \- to give the tick a label with its coordinate. Screen coordinates are
    * the coordinates of the graph rendered on the screen. Data coordinates are
    * the values in the data.
    */
  private def automaticTicksToSequence(
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

  private def userTicksToSequence(
      ticks: List[Double],
      scale: Bijection[Point, Point],
      toPoint: Double => Point,
      filter: Double => Boolean
  ): TicksSequence = {
    ticks
      .filter { tick =>
        filter(tick)
      }
      .map { tick =>
        (ScreenCoordinate(scale(toPoint(tick))), DataCoordinate(toPoint(tick)))
      }
  }

  private def convertToMinorTicks(
      ticksSequence: TicksSequence,
      interval: Double,
      majorTickToMinor: (ScreenCoordinate, Double, Int) => (
          ScreenCoordinate,
          DataCoordinate
      )
  ): TicksSequence = {
    ticksSequence.tail.flatMap { (screenCoordinate, _) =>
      val minorTicks = for (i <- 1 to minorTickCount) yield {
        majorTickToMinor(screenCoordinate, interval, i)
      }

      minorTicks
    }
  }

  private def withTicks(
      ticksSequence: TicksSequence,
      createTick: (ScreenCoordinate, Int) => OpenPath,
      createTickLabel: (
          ScreenCoordinate,
          DataCoordinate
      ) => PlotPicture,
      tickSize: Int
  ): PlotPicture = {
    ticksSequence
      .foldLeft(empty[PlotAlg])((plot, tick) =>
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
      .moveTo(xTicksMapped.min - axisMargin, yTicksMapped.min - axisMargin)
      .lineTo(xTicksMapped.max + axisMargin, yTicksMapped.min - axisMargin)
      .lineTo(xTicksMapped.max + axisMargin, yTicksMapped.max + axisMargin)
      .lineTo(xTicksMapped.min - axisMargin, yTicksMapped.max + axisMargin)
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
              .moveTo(screenCoordinate.x, yTicksMapped.min - axisMargin)
              .lineTo(screenCoordinate.x, yTicksMapped.max + axisMargin)
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
                  .moveTo(xTicksMapped.min - axisMargin, screenCoordinate.y)
                  .lineTo(xTicksMapped.max + axisMargin, screenCoordinate.y)
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
