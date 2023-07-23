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
import chartreuse.layout.ScatterPlot
import chartreuse.{*, given}
import doodle.core.Point
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

@JSExportTopLevel("QuickStartExample")
object QuickStartExample {
  val data =
    Data(List.fill(100)(Point(Random.nextGaussian(), Random.nextGaussian())))

  val layout = ScatterPlot.default[Point]
  val layer = Layer(data, layout)(pt => pt)
  val plot = Plot(layer)
    .withPlotTitle("Our Amazing Plot")
    .withXTitle("Awesomeness")
    .withYTitle("Marvellousness")

  @JSExport
  def draw(id: String): Unit =
    plot.draw(640, 480).drawWithFrame(Frame(id))
}
