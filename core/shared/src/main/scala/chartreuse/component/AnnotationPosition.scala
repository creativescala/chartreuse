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
import doodle.syntax.all.*

/** Represents a specific positioning for annotations along with an arrow angle
  * and placement logic.
  *
  * @param landmark
  *   The landmark used as the reference point for annotation placement.
  * @param arrowAngle
  *   The angle of the arrow indicating the annotation.
  * @param toPoint
  *   A function that takes a base point and a margin offset, and returns the
  *   final annotation point. For example, `(pt, offset) => Point(pt.x - margin,
  *   pt.y + margin)` will place the annotation diagonally above and to the left
  *   of the point of interest.
  */
final case class AnnotationPosition(
    landmark: Landmark,
    arrowAngle: Angle,
    toPoint: (Point, Double) => Point
)

/** Provides predefined annotation positioning options for ease of use */
object AnnotationPosition {
  val center: AnnotationPosition =
    AnnotationPosition(Landmark.origin, 0.degrees, (pt, _) => pt)

  val top: AnnotationPosition =
    AnnotationPosition(
      Landmark.percent(0, -100),
      270.degrees,
      (pt, margin) => Point(pt.x, pt.y + margin)
    )

  val bottom: AnnotationPosition =
    AnnotationPosition(
      Landmark.percent(0, 100),
      90.degrees,
      (pt, margin) => Point(pt.x, pt.y - margin)
    )

  val left: AnnotationPosition =
    AnnotationPosition(
      Landmark.percent(100, 0),
      0.degrees,
      (pt, margin) => Point(pt.x - margin, pt.y)
    )

  val right: AnnotationPosition =
    AnnotationPosition(
      Landmark.percent(-100, 0),
      180.degrees,
      (pt, margin) => Point(pt.x + margin, pt.y)
    )

  val topLeft: AnnotationPosition =
    AnnotationPosition(
      Landmark.bottomRight,
      315.degrees,
      (pt, margin) => Point(pt.x - margin, pt.y + margin)
    )

  val topRight: AnnotationPosition =
    AnnotationPosition(
      Landmark.bottomLeft,
      225.degrees,
      (pt, margin) => Point(pt.x + margin, pt.y + margin)
    )

  val bottomLeft: AnnotationPosition =
    AnnotationPosition(
      Landmark.topRight,
      45.degrees,
      (pt, margin) => Point(pt.x - margin, pt.y - margin)
    )

  val bottomRight: AnnotationPosition =
    AnnotationPosition(
      Landmark.topLeft,
      135.degrees,
      (pt, margin) => Point(pt.x + margin, pt.y - margin)
    )
}
