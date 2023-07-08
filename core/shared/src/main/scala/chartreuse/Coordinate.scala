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

object Coordinate {
  opaque type ScreenCoordinate = Point

  object ScreenCoordinate {
    def apply(x: Double, y: Double): ScreenCoordinate = Point(x, y)
    def apply(point: Point): ScreenCoordinate = point

    def unapply(point: ScreenCoordinate): Option[Point] = Some(point)

    extension (screenCoordinate: ScreenCoordinate) {
      def x: Double = screenCoordinate.x
      def y: Double = screenCoordinate.y
    }
  }

  opaque type DataCoordinate = Point

  object DataCoordinate {
    def apply(x: Double, y: Double): DataCoordinate = Point(x, y)
    def apply(point: Point): DataCoordinate = point

    def unapply(point: DataCoordinate): Option[Point] = Some(point)

    extension (dataCoordinate: DataCoordinate) {
      def x: Double = dataCoordinate.x
      def y: Double = dataCoordinate.y
    }
  }
}
