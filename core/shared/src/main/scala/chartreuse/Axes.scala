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

final case class Axes[-Alg <: Algebra](
    xTickLayout: MajorTickLayout,
    yTickLayout: MajorTickLayout,
    minorTickLayout: MinorTickLayout,
    grid: Boolean,
    layers: Seq[Layer[?, Alg]],
    width: Int,
    height: Int
) {
  private val tickSize = 7
  private val axisMargin = 10
  private val textMargin = axisMargin + tickSize + 5

  def build(using
      numberFormat: NumberFormat
  ): Picture[Alg & PlotAlg, Unit] = {
    val dataBoundingBox = layers.foldLeft(BoundingBox.empty) { (bb, layer) =>
      bb.on(layer.boundingBox)
    }

    val dataMinX = dataBoundingBox.left
    val dataMaxX = dataBoundingBox.right
    val dataMinY = dataBoundingBox.bottom
    val dataMaxY = dataBoundingBox.top

    val scale = Scale.linear.build(dataBoundingBox, width, height)

    // Convert the Ticks to a sequence of points
    val asX: Double => Point = x => Point(x, 0)
    val asY: Double => Point = y => Point(0, y)
    val xFilter: Double => Boolean = tick =>
      tick >= dataMinX && tick <= dataMaxX
    val yFilter: Double => Boolean = tick =>
      tick >= dataMinY && tick <= dataMaxY

    val xTicksSequence: TicksSequence =
      majorTickLayoutToSequence(xTickLayout, scale, asX, xFilter, dataMinX, dataMaxX)
    val yTicksSequence: TicksSequence =
      majorTickLayoutToSequence(yTickLayout, scale, asY, yFilter, dataMinY, dataMaxY)

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

    val xMinorTicksSequence = minorTickLayoutToSequence(
      minorTickLayout,
      xTicksSequence,
      xMajorTickToMinorTick,
      coordinate => coordinate.x
    )
    val yMinorTicksSequence = minorTickLayoutToSequence(
      minorTickLayout,
      yTicksSequence,
      yMajorTickToMinorTick,
      coordinate => coordinate.y
    )

    val (xTicksMinCoordinate, _) = xTicksSequence.head
    val (xTicksMaxCoordinate, _) = xTicksSequence.last
    val (yTicksMinCoordinate, _) = yTicksSequence.head
    val (yTicksMaxCoordinate, _) = yTicksSequence.last

    val xTicksMin = Math.min(xTicksMinCoordinate.x, scale(Point(dataMinX, 0)).x)
    val xTicksMax = Math.max(xTicksMaxCoordinate.x, scale(Point(dataMaxX, 0)).x)
    val yTicksMin = Math.min(yTicksMinCoordinate.y, scale(Point(0, dataMinY)).y)
    val yTicksMax = Math.max(yTicksMaxCoordinate.y, scale(Point(0, dataMaxY)).y)

    val createXTick: (ScreenCoordinate, Int) => OpenPath =
      (screenCoordinate, tickSize) =>
        OpenPath.empty
          .moveTo(screenCoordinate.x, yTicksMin - axisMargin)
          .lineTo(
            screenCoordinate.x,
            yTicksMin - axisMargin - tickSize
          )

    val createXTickLabel
        : (ScreenCoordinate, DataCoordinate) => Picture[Alg & PlotAlg, Unit] =
      (screenCoordinate, dataCoordinate) =>
        text(numberFormat.format(dataCoordinate.x))
          .originAt(Landmark.percent(0, 100))
          .at(screenCoordinate.x, yTicksMin - textMargin)

    val createYTick: (ScreenCoordinate, Int) => OpenPath =
      (screenCoordinate, tickSize) =>
        OpenPath.empty
          .moveTo(xTicksMin - axisMargin, screenCoordinate.y)
          .lineTo(
            xTicksMin - axisMargin - tickSize,
            screenCoordinate.y
          )

    val createYTickLabel
        : (ScreenCoordinate, DataCoordinate) => Picture[Alg & PlotAlg, Unit] =
      (screenCoordinate, dataCoordinate) =>
        text(numberFormat.format(dataCoordinate.y))
          .originAt(Landmark.percent(100, 0))
          .at(xTicksMin - textMargin, screenCoordinate.y)

    withTicks(xTicksSequence, createXTick, createXTickLabel, tickSize)
      .on(
        withTicks(yTicksSequence, createYTick, createYTickLabel, tickSize)
      )
      .on(withAxes(xTicksMin, xTicksMax, yTicksMin, yTicksMax))
      .on(
        if grid then
          withGrid(
            xTicksMin,
            xTicksMax,
            yTicksMin,
            yTicksMax,
            xTicksSequence,
            yTicksSequence
          )
        else empty[Shape]
      )
      .on(
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
      )
  }

  private def majorTickLayoutToSequence(
      tickLayout: MajorTickLayout,
      scale: Bijection[Point, Point],
      toPoint: Double => Point,
      filter: Double => Boolean,
      min: Double,
      max: Double
  ): TicksSequence = {
    tickLayout match {
      case MajorTickLayout.Manual(ticks) =>
        manualTicksToSequence(ticks, scale, toPoint, filter)
      case MajorTickLayout.Algorithmic(tickCount) =>
        automaticTicksToSequence(
          TickMarkCalculator.calculateTickScale(min, max, tickCount),
          scale,
          toPoint
        )
      case MajorTickLayout.NoTicks =>
        List.empty[(ScreenCoordinate, DataCoordinate)]
    }
  }

  private def minorTickLayoutToSequence(
      minorTickLayout: MinorTickLayout,
      ticksSequence: TicksSequence,
      majorTickToMinorTick: (ScreenCoordinate, Double, Int) => (
          ScreenCoordinate,
          DataCoordinate
      ),
      coordinateToDouble: ScreenCoordinate => Double
  ): TicksSequence = {
    minorTickLayout match {
      case MinorTickLayout.Algorithmic(tickCount) =>
        convertToMinorTicks(
          ticksSequence,
          majorTickToMinorTick,
          tickCount,
          coordinateToDouble
        )
      case MinorTickLayout.NoTicks =>
        List.empty[(ScreenCoordinate, DataCoordinate)]
    }
  }

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

  private def manualTicksToSequence(
      ticks: Seq[Double],
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
      majorTickToMinor: (ScreenCoordinate, Double, Int) => (
          ScreenCoordinate,
          DataCoordinate
      ),
      tickCount: Int,
      coordinateToDouble: ScreenCoordinate => Double
  ): TicksSequence = {
    var prev = ticksSequence.head
    ticksSequence.tail.flatMap { tick =>
      val (screenCoordinate, _) = tick
      val (prevScreenCoordinate, _) = prev
      val interval = (coordinateToDouble(screenCoordinate) - coordinateToDouble(
        prevScreenCoordinate
      )) / (tickCount + 1)
      val minorTicks = for (i <- 1 to tickCount) yield {
        majorTickToMinor(screenCoordinate, interval, i)
      }
      prev = tick

      minorTicks
    }
  }

  private def withTicks(
      ticksSequence: TicksSequence,
      createTick: (ScreenCoordinate, Int) => OpenPath,
      createTickLabel: (
          ScreenCoordinate,
          DataCoordinate
      ) => Picture[Alg & PlotAlg, Unit],
      tickSize: Int
  ): Picture[Alg & PlotAlg, Unit] = {
    ticksSequence
      .foldLeft(empty[Alg & PlotAlg])((plot, tick) =>
        val (screenCoordinate, dataCoordinate) = tick

        plot
          .on(createTick(screenCoordinate, tickSize).path)
          .on(createTickLabel(screenCoordinate, dataCoordinate))
      )
  }

  private def withAxes(
      xTicksMin: Double,
      xTicksMax: Double,
      yTicksMin: Double,
      yTicksMax: Double
  ): Picture[Alg & PlotAlg, Unit] = {
    ClosedPath.empty
      .moveTo(xTicksMin - axisMargin, yTicksMin - axisMargin)
      .lineTo(xTicksMax + axisMargin, yTicksMin - axisMargin)
      .lineTo(xTicksMax + axisMargin, yTicksMax + axisMargin)
      .lineTo(xTicksMin - axisMargin, yTicksMax + axisMargin)
      .path
  }

  private def withGrid(
      xTicksMin: Double,
      xTicksMax: Double,
      yTicksMin: Double,
      yTicksMax: Double,
      xTicksSequence: TicksSequence,
      yTicksSequence: TicksSequence
  ): Picture[Alg & PlotAlg, Unit] = {
    xTicksSequence
      .foldLeft(empty[Layout & Path & Style & Shape])((plot, tick) =>
        val (screenCoordinate, _) = tick

        plot
          .on(
            OpenPath.empty
              .moveTo(screenCoordinate.x, yTicksMin - axisMargin)
              .lineTo(screenCoordinate.x, yTicksMax + axisMargin)
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
                  .moveTo(xTicksMin - axisMargin, screenCoordinate.y)
                  .lineTo(xTicksMax + axisMargin, screenCoordinate.y)
                  .path
                  .strokeColor(Color.gray)
                  .strokeWidth(0.5)
              )
          )
      )
  }
}
