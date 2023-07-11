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

package chartreuse.examples

import cats.effect.unsafe.implicits.global
import chartreuse.Plot
import chartreuse.*
import chartreuse.layout.ScatterPlot
import doodle.core.Point
import doodle.language.Basic
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

@JSExportTopLevel("PlotExample")
object PlotExample {
  def randomLayer: Layer[Point, Basic] = {
    val points =
      List.fill(100)(Point(Random.nextGaussian(), Random.nextGaussian()))

    val data = Data(points)
    val layout = ScatterPlot.default[Point]
    Layer(data, layout)(pt => pt)
  }

  val plot: Plot[Basic] = Plot(
    List.fill(20)(randomLayer)
  )

  @JSExport
  def draw(id: String): Unit =
    plot.draw(640, 480).drawWithFrame(Frame(id))
}
