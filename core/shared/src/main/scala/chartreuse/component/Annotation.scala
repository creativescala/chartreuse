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
  val arrowWidth = 35
  val annotationMargin = (if arrow then arrowWidth else 5) + textMargin

  def draw(
      scale: Bijection[Point, Point]
  ): Picture[PlotAlg, Unit] = {
    val mappedPointOfInterest = scale(pointOfInterest)

    val annotation = annotationType match {
      case AnnotationType.Circle(radius) =>
          circle(radius).strokeWidth(strokeWidth).strokeColor(strokeColor).at(mappedPointOfInterest)
      case AnnotationType.CircleWithText(radius, content) =>
        circle(radius).strokeWidth(strokeWidth).strokeColor(strokeColor).at(mappedPointOfInterest)
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
      mappedPointOfInterest: Point
  ): Picture[PlotAlg, Unit] = {
    val rotatedArrowWidth = Math.sqrt(Math.pow(arrowWidth, 2) * 2)
    val arrowHeight = 7
    val degree90 = 1.5708
    val arrow = OpenPath.rightArrow(arrowWidth, arrowHeight).path
    val rotatedArrow = OpenPath.rightArrow(rotatedArrowWidth, arrowHeight).path

    annotationPosition match
      case AnnotationPosition.Center => empty
      case AnnotationPosition.Top =>
        arrow
          .rotate(Angle(degree90 * 3))
          .originAt(Landmark.percent(0, -100))
          .at(mappedPointOfInterest)
      case AnnotationPosition.Bottom =>
        arrow
          .rotate(Angle(degree90))
          .originAt(Landmark.percent(0, 100))
          .at(mappedPointOfInterest)
      case AnnotationPosition.Left =>
        arrow.originAt(Landmark.percent(100, 0)).at(mappedPointOfInterest)
      case AnnotationPosition.Right =>
        arrow
          .rotate(Angle(degree90 * 2))
          .originAt(Landmark.percent(-100, 0))
          .at(mappedPointOfInterest)
      case AnnotationPosition.TopLeft =>
        rotatedArrow
          .rotate(Angle(degree90 * 3.5))
          .originAt(Landmark.bottomRight)
          .at(mappedPointOfInterest)
      case AnnotationPosition.TopRight =>
        rotatedArrow
          .rotate(Angle(degree90 * 2.5))
          .originAt(Landmark.bottomLeft)
          .at(mappedPointOfInterest)
      case AnnotationPosition.BottomLeft =>
        rotatedArrow
          .rotate(Angle(degree90 / 2))
          .originAt(Landmark.topRight)
          .at(mappedPointOfInterest)
      case AnnotationPosition.BottomRight =>
        rotatedArrow
          .rotate(Angle(degree90 * 1.5))
          .originAt(Landmark.topLeft)
          .at(mappedPointOfInterest)
  }

  private def adjustPosition(
      picture: Picture[PlotAlg, Unit],
      pointOfInterest: Point
  ): Picture[PlotAlg, Unit] = {
    annotationPosition match
      case AnnotationPosition.Center => picture.at(pointOfInterest)
      case AnnotationPosition.Top =>
        picture
          .originAt(Landmark.percent(0, -100))
          .at(pointOfInterest.x, pointOfInterest.y + annotationMargin)
      case AnnotationPosition.Bottom =>
        picture
          .originAt(Landmark.percent(0, 100))
          .at(pointOfInterest.x, pointOfInterest.y - annotationMargin)
      case AnnotationPosition.Left =>
        picture
          .originAt(Landmark.percent(100, 0))
          .at(pointOfInterest.x - annotationMargin, pointOfInterest.y)
      case AnnotationPosition.Right =>
        picture
          .originAt(Landmark.percent(-100, 0))
          .at(pointOfInterest.x + annotationMargin, pointOfInterest.y)
      case AnnotationPosition.TopLeft =>
        picture
          .originAt(Landmark.bottomRight)
          .at(
            pointOfInterest.x - annotationMargin,
            pointOfInterest.y + annotationMargin
          )
      case AnnotationPosition.TopRight =>
        picture
          .originAt(Landmark.bottomLeft)
          .at(
            pointOfInterest.x + annotationMargin,
            pointOfInterest.y + annotationMargin
          )
      case AnnotationPosition.BottomLeft =>
        picture
          .originAt(Landmark.topRight)
          .at(
            pointOfInterest.x - annotationMargin,
            pointOfInterest.y - annotationMargin
          )
      case AnnotationPosition.BottomRight =>
        picture
          .originAt(Landmark.topLeft)
          .at(
            pointOfInterest.x + annotationMargin,
            pointOfInterest.y - annotationMargin
          )
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
      AnnotationPosition.Center,
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
      AnnotationPosition.Center,
      false,
      Color.whiteSmoke,
      Color.black,
      1.0
    )
}
