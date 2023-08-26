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

import chartreuse.Bijection
import chartreuse.Plot.PlotAlg
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

final case class Annotation(
    pointOfInterest: Point,
    annotationType: AnnotationType,
    annotationPosition: AnnotationPosition,
    arrow: Boolean,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Double
) {
  val textMargin = 5
  val annotationMargin = (if arrow then 35 else 5) + textMargin

  def draw(
      scale: Bijection[Point, Point]
  ): Picture[PlotAlg, Unit] = {
    val mappedPointOfInterest = scale(pointOfInterest)

    val annotation = annotationType match {
      case AnnotationType.Circle(radius) =>
        circle(radius)
          .strokeWidth(strokeWidth)
          .strokeColor(strokeColor)
          .at(mappedPointOfInterest)
      case AnnotationType.CircleWithText(radius, content) =>
        circle(radius)
          .strokeWidth(strokeWidth)
          .strokeColor(strokeColor)
          .at(mappedPointOfInterest)
          .on(adjustPosition(text(content), mappedPointOfInterest))
      case AnnotationType.Text(content) =>
        adjustPosition(text(content), mappedPointOfInterest)
      case AnnotationType.TextWithBox(content) =>
        val boxContent = text(content)
        val box = boxContent.boundingBox.flatMap(bb =>
          roundedRectangle(
            bb.width + textMargin * 2,
            bb.height + textMargin * 2,
            10
          ).fillColor(fillColor)
            .strokeColor(strokeColor)
            .strokeWidth(strokeWidth)
        )
        adjustPosition(boxContent.on(box), mappedPointOfInterest)
    }

    annotation.on(if arrow then withArrow(mappedPointOfInterest) else empty)
  }

  private def withArrow(
      pointOfInterest: Point
  ): Picture[PlotAlg, Unit] = {
    val annotationPoint =
      annotationPosition.toPoint(pointOfInterest, annotationMargin)
    val arrowWidth = Math.sqrt(
      Math.pow(annotationPoint.x - pointOfInterest.x, 2) +
        Math.pow(annotationPoint.y - pointOfInterest.y, 2)
    ) - textMargin
    val arrowHeight = 7
    val arrow = OpenPath.rightArrow(arrowWidth, arrowHeight).path

    arrow
      .rotate(annotationPosition.arrowAngle)
      .originAt(annotationPosition.landmark)
      .at(pointOfInterest)
  }

  private def adjustPosition(
      picture: Picture[PlotAlg, Unit],
      pointOfInterest: Point
  ): Picture[PlotAlg, Unit] = {
    picture
      .originAt(annotationPosition.landmark)
      .at(annotationPosition.toPoint(pointOfInterest, annotationMargin))
  }

  def withFillColor(fillColor: Color): Annotation = {
    this.copy(fillColor = fillColor)
  }

  def withStrokeColor(strokeColor: Color): Annotation = {
    this.copy(strokeColor = strokeColor)
  }

  def withStrokeWidth(strokeWidth: Double): Annotation = {
    this.copy(strokeWidth = strokeWidth)
  }

  def withArrow(arrow: Boolean = true): Annotation = {
    this.copy(arrow = arrow)
  }

  def withAnnotationType(annotationType: AnnotationType): Annotation = {
    this.copy(annotationType = annotationType)
  }

  def withAnnotationPosition(
      annotationPosition: AnnotationPosition
  ): Annotation = {
    this.copy(annotationPosition = annotationPosition)
  }

  def withPointOfInterest(pointOfInterest: Point): Annotation = {
    this.copy(pointOfInterest = pointOfInterest)
  }
}

object Annotation {
  def apply(
      pointOfInterest: Point,
      annotationType: AnnotationType
  ): Annotation =
    Annotation(
      pointOfInterest,
      annotationType,
      AnnotationPosition.center,
      false,
      Color.whiteSmoke,
      Color.black,
      1.0
    )

  def apply(
      x: Double,
      y: Double,
      annotationType: AnnotationType
  ): Annotation =
    Annotation(
      Point(x, y),
      annotationType,
      AnnotationPosition.center,
      false,
      Color.whiteSmoke,
      Color.black,
      1.0
    )
}
