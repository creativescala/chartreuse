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

import doodle.core.Point

/** A Scale is a bijection (invertible function) from data coordinates to plot
  * coordinates and back.
  */
final case class Scale(dataToPlot: Point => Point, plotToData: Point => Point)
object Scale {
  def linear(
      dataMinX: Double,
      dataMaxX: Double,
      dataMinY: Double,
      dataMaxY: Double,
      plotWidth: Int,
      plotHeight: Int
  ): Scale = {
    val dataWidth = dataMaxX - dataMinX
    val dataHeight = dataMaxY - dataMinY

    val xOffset = -plotWidth / 2.0
    val yOffset = -plotHeight / 2.0

    val xScale = plotWidth.toDouble / dataWidth
    val yScale = plotHeight.toDouble / dataHeight

    Scale(
      dataToPlot = pt =>
        Point(
          (pt.x - dataMinX) * xScale + xOffset,
          (pt.y - dataMinY) * yScale + yOffset
        ),
      plotToData = pt =>
        Point(
          ((pt.x - xOffset) / xScale) + dataMinX,
          ((pt.y - yOffset) / yScale) + dataMinY
        )
    )
  }
}
