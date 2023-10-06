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

import cats.Id
import chartreuse.component.Axis.*
import chartreuse.component.*
import chartreuse.theme.PlotTheme
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
    xTicks: MajorTickLayout = MajorTickLayout.Algorithmic(12),
    yTicks: MajorTickLayout = MajorTickLayout.Algorithmic(12),
    minorTicks: MinorTickLayout = MinorTickLayout.NoTicks,
    theme: PlotTheme[Id] = PlotTheme.default,
    annotations: List[Annotation] = List.empty[Annotation]
) {
  def addLayer[Alg2 <: Algebra](layer: Layer[?, Alg2]): Plot[Alg & Alg2] = {
    copy(layers = layer :: layers)
  }

  def addAnnotation(annotation: Annotation): Plot[Alg] = {
    copy(annotations = annotation :: annotations)
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

  def draw(
      width: Int,
      height: Int,
      theme: PlotTheme[Id] = PlotTheme.default
  ): Picture[Alg & PlotAlg, Unit] = {
    val dataBoundingBox = layers.foldLeft(BoundingBox.empty) { (bb, layer) =>
      bb.on(layer.boundingBox)
    }

    val dataMinX = dataBoundingBox.left
    val dataMaxX = dataBoundingBox.right
    val dataMinY = dataBoundingBox.bottom
    val dataMaxY = dataBoundingBox.top

    val scale = Scale.linear.build(dataBoundingBox, width, height)

    val xAxis = Axis(
      xTicks,
      minorTicks,
      scale,
      xMajorTickToMinorTick,
      createXTick,
      createXTickLabels,
      p => p.x,
      d => Point(d, 0),
      dataMinX,
      dataMaxX,
      theme
    )

    val yAxis = Axis(
      yTicks,
      minorTicks,
      scale,
      yMajorTickToMinorTick,
      createYTick,
      createYTickLabels,
      p => p.y,
      d => Point(0, d),
      dataMinY,
      dataMaxY,
      theme
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
        .zip(theme.layerThemesIterator)
        .map((layer, theme) => layer.draw(width, height, scale, theme))
        .foldLeft(empty)(_ on _)

    val allAnnotations: Picture[Alg & PlotAlg, Unit] =
      annotations
        .foldLeft(empty)((acc, annotation) => acc.on(annotation.draw(scale)))

    val plot = allLayers
      .on(
        xAxis
          .build(xMajorTicksSequence, xMinorTicksSequence, yTicksBounds)
          .on(
            yAxis
              .build(
                yMajorTicksSequence,
                yMinorTicksSequence,
                xTicksBounds
              )
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
          Legend(layers, theme).build(xTicksBounds.max, yTicksBounds.max)
        else empty
      )
      .under(allAnnotations)

    val plotTitles = plot.boundingBox.flatMap(bb =>
      val titleMargin = 15

      // TODO: take fill from style
      // This is a bit of a hack to fill in the text (by default, text on SVG is not filled)
      // It should be taken from the theme
      theme
        .headingText(this.plotTitle)
        .originAt(Landmark.percent(0, -100))
        .at(bb.left + bb.width / 2, bb.top + titleMargin)
        .on(
          theme
            .normalText(this.xTitle)
            .originAt(Landmark.percent(0, 100))
            .at(bb.left + bb.width / 2, bb.bottom - titleMargin)
        )
        .on(
          theme
            .normalText(this.yTitle)
            .rotate(90.degrees)
            .originAt(Landmark.percent(100, 0))
            .at(bb.left - titleMargin, bb.bottom + bb.height / 2)
        )
    )

    plot
      .on(plotTitles)
  }
}

object Plot {
  type PlotAlg = Layout & Path & Style & Shape & Text &
    doodle.algebra.Transform & Size

  /** Utility constructor to create a `Plot` from a single layer. */
  def apply[Alg <: Algebra](layer: Layer[?, Alg]): Plot[Alg] =
    Plot(layers = List(layer))
}
