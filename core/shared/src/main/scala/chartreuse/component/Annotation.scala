package chartreuse.component

import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

final case class Annotation(text: String, annotationType: AnnotationType, pointOfInterest: Point) {
  def draw: Picture[Text, Unit] = {
    ???
  }

  def withText(text: String): Annotation = {
    this.copy(text = text)
  }

  def withAnnotationType(annotationType: AnnotationType): Annotation = {
    this.copy(annotationType = annotationType)
  }

  def withPointOfInterest(pointOfInterest: Point): Annotation = {
    this.copy(pointOfInterest = pointOfInterest)
  }
}
