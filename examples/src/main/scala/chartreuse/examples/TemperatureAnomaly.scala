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
import chartreuse.layout.*
import doodle.core.Color
import doodle.core.Point
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("TemperatureAnomaly")
object TemperatureAnomaly {
  import HadCrut5.*

  // Filter out 2023 because the data is not complete
  val dataByYear =
    data.groupBy(_.year).filter((year, _) => year < 2023)

  val layers =
    dataByYear
      .map((year, records) =>
        Line
          .default[Record]
          .forThemeable(themeable =>
            // Highlight the most recent years by themeing them. Other years become grey
            if year < 2013 then
              themeable.withStrokeColor(Themeable.Override(Some(Color.grey)))
            else themeable
          )
          .toLayer(records.sortBy(_.month))(record =>
            Point(record.month, record.anomaly)
          )
          .withLabel(year.toString)
      )
      .toList
      .sortBy(_.label)

  val plot = Plot(layers.toList)
    .withPlotTitle(
      "Global Average Temperature Anomaly (2022-2013 Highlighted)"
    )
    .withYTitle("°C anomaly from 1961-1990")
    .withXTitle("Month")
    .withLegend(false)

  @JSExport
  def draw(id: String): Unit =
    plot.draw(480, 360).drawWithFrame(Frame(id))

}
