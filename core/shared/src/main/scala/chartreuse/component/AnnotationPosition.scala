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

import doodle.core.*

final case class AnnotationPosition(
    landmark: Landmark,
    arrowAngle: Angle,
    toPoint: (Point, Double) => Point
)

object AnnotationPosition {
  val degree90 = 1.5708

  val center =
    AnnotationPosition(Landmark.origin, Angle(0), (pt, _) => pt)
  val top =
    AnnotationPosition(
      Landmark.percent(0, -100),
      Angle(degree90 * 3),
      (pt, margin) => Point(pt.x, pt.y + margin)
    )
  val bottom =
    AnnotationPosition(
      Landmark.percent(0, 100),
      Angle(degree90),
      (pt, margin) => Point(pt.x, pt.y - margin)
    )
  val left =
    AnnotationPosition(
      Landmark.percent(100, 0),
      Angle(0),
      (pt, margin) => Point(pt.x - margin, pt.y)
    )
  val right =
    AnnotationPosition(
      Landmark.percent(-100, 0),
      Angle(degree90 * 2),
      (pt, margin) => Point(pt.x + margin, pt.y)
    )
  val topLeft =
    AnnotationPosition(
      Landmark.bottomRight,
      Angle(degree90 * 3.5),
      (pt, margin) => Point(pt.x - margin, pt.y + margin)
    )
  val topRight =
    AnnotationPosition(
      Landmark.bottomLeft,
      Angle(degree90 * 2.5),
      (pt, margin) => Point(pt.x + margin, pt.y + margin)
    )
  val bottomLeft =
    AnnotationPosition(
      Landmark.topRight,
      Angle(degree90 / 2),
      (pt, margin) => Point(pt.x - margin, pt.y - margin)
    )
  val bottomRight =
    AnnotationPosition(
      Landmark.topLeft,
      Angle(degree90 * 1.5),
      (pt, margin) => Point(pt.x + margin, pt.y - margin)
    )
}
