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

import doodle.core.BoundingBox
import doodle.core.Point

/** The scale determines how data coordinates are mapped to screen coordinates.
  */
trait Scale {
  def build(
      extent: BoundingBox,
      width: Int,
      height: Int
  ): Bijection[Point, Point]
}
object Scale {
  def linear: Scale =
    new Scale {
      def build(
          extent: BoundingBox,
          width: Int,
          height: Int
      ): Bijection[Point, Point] = {
        val dataMinX: Double = extent.left
        val dataMaxX: Double = extent.right
        val dataMinY: Double = extent.bottom
        val dataMaxY: Double = extent.top

        val dataWidth = dataMaxX - dataMinX
        val dataHeight = dataMaxY - dataMinY

        val xOffset = -width / 2.0
        val yOffset = -height / 2.0

        val xScale = width.toDouble / dataWidth
        val yScale = height.toDouble / dataHeight

        Bijection(
          to = pt =>
            Point(
              (pt.x - dataMinX) * xScale + xOffset,
              (pt.y - dataMinY) * yScale + yOffset
            ),
          from = pt =>
            Point(
              ((pt.x - xOffset) / xScale) + dataMinX,
              ((pt.y - yOffset) / yScale) + dataMinY
            )
        )
      }
    }
}
