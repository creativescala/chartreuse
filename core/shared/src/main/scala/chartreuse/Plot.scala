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

import chartreuse.component.Axis.axisMargin
import chartreuse.component.*
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

import Plot.PlotAlg

/** A `Plot` is a collection of layers along with a title, legend, axes, and
  * grid.
  */
final case class Plot[-Alg <: Algebra](
    layers: List[Layer[?, Alg]],
    plotTitle: String = "Plot Title",
    xTitle: String = "X data",
    yTitle: String = "Y data",
    grid: Boolean = false,
    legend: Boolean = false,
    rotatedLabels: Boolean = false,
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

  def withLegend(newLegend: Boolean): Plot[Alg] = {
    copy(legend = newLegend)
  }

  def withRotatedLabels(newRotatedLabels: Boolean): Plot[Alg] = {
    copy(rotatedLabels = newRotatedLabels)
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

  def draw(width: Int, height: Int)(using
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

    val createXTickLabel: (
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
          .at(screenCoordinate.x, anchorPoint - 22)

    val createYTick: (ScreenCoordinate, Int, Double) => OpenPath =
      (screenCoordinate, tickSize, anchorPoint) =>
        OpenPath.empty
          .moveTo(anchorPoint - axisMargin, screenCoordinate.y)
          .lineTo(
            anchorPoint - axisMargin - tickSize,
            screenCoordinate.y
          )

    val createYTickLabel: (
        ScreenCoordinate,
        DataCoordinate,
        Double
    ) => Picture[Alg & PlotAlg, Unit] =
      (screenCoordinate, dataCoordinate, anchorPoint) =>
        text(numberFormat.format(dataCoordinate.y))
          .originAt(Landmark.percent(100, 0))
          .at(anchorPoint - 22, screenCoordinate.y)

    val xAxis = Axis(
      xTicks,
      minorTicks,
      scale,
      xMajorTickToMinorTick,
      createXTick,
      createXTickLabel,
      point => point.x,
      d => Point(d, 0),
      dataMinX,
      dataMaxX
    )

    val yAxis = Axis(
      yTicks,
      minorTicks,
      scale,
      yMajorTickToMinorTick,
      createYTick,
      createYTickLabel,
      point => point.y,
      d => Point(0, d),
      dataMinY,
      dataMaxY
    )

    val xMajorTicksSequence = xAxis.majorTickLayoutToSequence
    val xMinorTicksSequence =
      xAxis.minorTickLayoutToSequence(xMajorTicksSequence)

    val yMajorTicksSequence = yAxis.majorTickLayoutToSequence
    val yMinorTicksSequence =
      yAxis.minorTickLayoutToSequence(yMajorTicksSequence)

    val xTicksBounds = xAxis.getTicksBounds(xMajorTicksSequence)
    val yTicksBounds = yAxis.getTicksBounds(yMajorTicksSequence)

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
            xAxis
              .build(xMajorTicksSequence, xMinorTicksSequence, yTicksBounds)
              .on(
                yAxis
                  .build(yMajorTicksSequence, yMinorTicksSequence, xTicksBounds)
              )
          )
          .on(PlotBox(xTicksBounds, yTicksBounds).build)
          .on(
            if grid then
              Grid(
                xTicksBounds,
                yTicksBounds,
                xMajorTicksSequence,
                yMajorTicksSequence
              ).build
            else empty
          )
          .under(
            if legend then
              Legend(layers).build(xTicksBounds.max, yTicksBounds.max)
            else empty
          )
          .margin(5)
          .below(plotTitle)
          .above(xTitle)
      )
  }
}

object Plot {
  type PlotAlg = Layout & Path & Style & Shape & Text &
    doodle.algebra.Transform & Size

  /** Utility constructor to create a `Plot` from a single layer. */
  def apply[Alg <: Algebra](layer: Layer[?, Alg]): Plot[Alg] =
    Plot(layers = List(layer))
}
