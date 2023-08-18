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

package chartreuse.component

import chartreuse.Plot.PlotAlg
import chartreuse.*
import chartreuse.component.Axis.TicksSequence
import chartreuse.component.Axis.tickSize
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

/** An `Axis` is used to build the plot tick marks. */
final case class Axis[-Alg <: Algebra](
    majorTickLayout: MajorTickLayout,
    minorTickLayout: MinorTickLayout,
    scale: Bijection[Point, Point],
    majorTickToMinorTick: (ScreenCoordinate, Double, Int) => (
        ScreenCoordinate,
        DataCoordinate
    ),
    createTick: (ScreenCoordinate, Int, Double) => OpenPath,
    createTickLabel: (
        ScreenCoordinate,
        DataCoordinate,
        Double
    ) => Picture[Alg & PlotAlg, Unit],
    toDouble: Point => Double,
    toPoint: Double => Point,
    dataMin: Double,
    dataMax: Double
) {

  /** Draw the ticks, using major and minor tick sequences and bounds of the
    * opposite `Axis`. That is, before using this method, it's necessary to
    * create an `Axis` object for the opposite axis and get its tick bounds
    * (e.g. to build the X-axis, you first need to create an `Axis` object for
    * the Y-axis).
    */
  def build(
      majorTicksSequence: TicksSequence,
      minorTicksSequence: TicksSequence,
      oppositeTicksBounds: TicksBounds
  ): Picture[Alg & PlotAlg, Unit] = {
    withTicks(
      majorTicksSequence,
      createTick,
      createTickLabel,
      tickSize,
      oppositeTicksBounds.min
    )
      .on(
        withTicks(
          minorTicksSequence,
          createTick,
          (_, _, _) => empty,
          tickSize / 2,
          oppositeTicksBounds.min
        )
      )
  }

  /** A convenience to convert [[MajorTickLayout]] to [[TicksSequence]]. */
  def majorTickLayoutToSequence: TicksSequence = {
    val filter: Double => Boolean = tick => tick >= dataMin && tick <= dataMax

    majorTickLayout match {
      case MajorTickLayout.Manual(ticks) =>
        manualTicksToSequence(ticks, scale, toPoint, filter)
      case MajorTickLayout.Algorithmic(tickCount) =>
        algorithmicTicksToSequence(
          TickMarkCalculator.calculateTickScale(dataMin, dataMax, tickCount),
          scale,
          toPoint
        )
      case MajorTickLayout.NoTicks =>
        List.empty
    }
  }

  /** A convenience to convert [[MinorTickLayout]] to [[TicksSequence]]. */
  def minorTickLayoutToSequence(
      majorTicksSequence: TicksSequence
  ): TicksSequence = {
    minorTickLayout match {
      case MinorTickLayout.Algorithmic(tickCount) =>
        convertToMinorTicks(
          majorTicksSequence,
          majorTickToMinorTick,
          tickCount,
          toDouble
        )
      case MinorTickLayout.NoTicks =>
        List.empty
    }
  }

  /** Returns [[TicksBounds]] of the current [[Axis]]. This is needed to build
    * such plot attributes, as axes, legends, grids, etc.
    */
  def getTicksBounds(majorTicksSequence: TicksSequence): TicksBounds = {
    val ticksMin = Math.min(
      majorTickLayout match {
        case MajorTickLayout.NoTicks => Double.MaxValue
        case _ =>
          toDouble(ScreenCoordinate.unapply(majorTicksSequence.head(0)).get)
      },
      toDouble(scale(toPoint(dataMin)))
    )
    val ticksMax = Math.max(
      majorTickLayout match {
        case MajorTickLayout.NoTicks => Double.MinValue
        case _ =>
          toDouble(ScreenCoordinate.unapply(majorTicksSequence.last(0)).get)
      },
      toDouble(scale(toPoint(dataMax)))
    )

    TicksBounds(ticksMin, ticksMax)
  }

  private def algorithmicTicksToSequence(
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
      coordinateToDouble: Point => Double
  ): TicksSequence = {
    var prev = ticksSequence.head
    ticksSequence.tail.flatMap { tick =>
      val (screenCoordinate, _) = tick
      val (prevScreenCoordinate, _) = prev
      val interval = (coordinateToDouble(
        Point(screenCoordinate.x, screenCoordinate.y)
      ) - coordinateToDouble(
        Point(prevScreenCoordinate.x, prevScreenCoordinate.y)
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
      createTick: (ScreenCoordinate, Int, Double) => OpenPath,
      createTickLabel: (
          ScreenCoordinate,
          DataCoordinate,
          Double
      ) => Picture[Alg & PlotAlg, Unit],
      tickSize: Int,
      anchorPoint: Double
  ): Picture[Alg & PlotAlg, Unit] = {
    ticksSequence
      .foldLeft(empty[Alg & PlotAlg])((plot, tick) =>
        val (screenCoordinate, dataCoordinate) = tick

        plot
          .on(createTick(screenCoordinate, tickSize, anchorPoint).path)
          .on(createTickLabel(screenCoordinate, dataCoordinate, anchorPoint))
      )
  }
}

object Axis {

  /** Utility type to build tick marks. [[ScreenCoordinate]] is used to place a
    * tick mark, and [[DataCoordinate]] is used to give the tick a label.
    */
  type TicksSequence = Seq[(ScreenCoordinate, DataCoordinate)]
  val axisMargin = 10
  val tickSize = 7
  val textMargin = axisMargin + tickSize + 5

  // Functions for building tick marks ----------------------------------------

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

  val createXTick: (ScreenCoordinate, Int, Double) => OpenPath =
    (screenCoordinate, tickSize, anchorPoint) =>
      OpenPath.empty
        .moveTo(screenCoordinate.x, anchorPoint - axisMargin)
        .lineTo(
          screenCoordinate.x,
          anchorPoint - axisMargin - tickSize
        )

  val createYTick: (ScreenCoordinate, Int, Double) => OpenPath =
    (screenCoordinate, tickSize, anchorPoint) =>
      OpenPath.empty
        .moveTo(anchorPoint - axisMargin, screenCoordinate.y)
        .lineTo(
          anchorPoint - axisMargin - tickSize,
          screenCoordinate.y
        )

  def createXTickLabel[Alg <: Algebra](
      rotatedLabels: Boolean
  )(using numberFormat: NumberFormat): (
      ScreenCoordinate,
      DataCoordinate,
      Double
  ) => Picture[Alg & PlotAlg, Unit] =
    (screenCoordinate, dataCoordinate, anchorPoint) =>
      text(numberFormat.format(dataCoordinate.x))
        .rotate(Angle(if rotatedLabels then 0.523599 else 0))
        .originAt(
          if rotatedLabels then Landmark.topRight
          else Landmark.percent(0, 100)
        )
        .at(screenCoordinate.x, anchorPoint - textMargin)

  def createYTickLabel[Alg <: Algebra](using numberFormat: NumberFormat): (
      ScreenCoordinate,
      DataCoordinate,
      Double
  ) => Picture[Alg & PlotAlg, Unit] =
    (screenCoordinate, dataCoordinate, anchorPoint) =>
      text(numberFormat.format(dataCoordinate.y))
        .originAt(Landmark.percent(100, 0))
        .at(anchorPoint - textMargin, screenCoordinate.y)
}
