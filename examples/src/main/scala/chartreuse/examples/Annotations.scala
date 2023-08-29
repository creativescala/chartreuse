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

package chartreuse.examples

import cats.effect.unsafe.implicits.global
import chartreuse.*
import chartreuse.component.*
import chartreuse.layout.*
import doodle.core.Color
import doodle.core.Point
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

import BahamasPopulation.population

@JSExportTopLevel("Annotations")
object Annotations {
  val line =
    Line
      .default[Point]
      .forThemeable(theme =>
        theme
          .withStrokeColor(Themeable.Override(Some(Color.orchid)))
          .withStrokeWidth(Themeable.Override(4.0))
      )

  val plot =
    Plot(line.toLayer(population).withLabel("Line"))
      .withPlotTitle("Bahamas Population")
      .withYTitle("Estimated Population")
      .withXTitle("Year")
      .addAnnotation(
        Annotation(
          Point(1950, 81651),
          AnnotationType.Text("Rapid growth began here")
        )
          .withAnnotationPosition(AnnotationPosition.topLeft)
          .withArrow()
      )

  @JSExport
  def draw(id: String): Unit =
    plot.draw(640, 480).drawWithFrame(Frame(id))
}
