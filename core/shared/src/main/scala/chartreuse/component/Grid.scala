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

import chartreuse.TicksBounds
import chartreuse.component.Axis.TicksSequence
import chartreuse.component.Axis.axisMargin
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*
import chartreuse.x
import chartreuse.y

case class Grid(
    xTicksBounds: TicksBounds,
    yTicksBounds: TicksBounds,
    xTicksSequence: TicksSequence,
    yTicksSequence: TicksSequence
) {
  def build = {
    xTicksSequence
      .foldLeft(empty[Layout & Path & Style & Shape])((plot, tick) =>
        val (screenCoordinate, _) = tick

        plot
          .on(
            OpenPath.empty
              .moveTo(screenCoordinate.x, yTicksBounds.min - axisMargin)
              .lineTo(screenCoordinate.x, yTicksBounds.max + axisMargin)
              .path
              .strokeColor(Color.gray)
              .strokeWidth(0.5)
          )
      )
      .on(
        yTicksSequence
          .foldLeft(empty[Layout & Path & Style & Shape])((plot, tick) =>
            val (screenCoordinate, _) = tick

            plot
              .on(
                OpenPath.empty
                  .moveTo(xTicksBounds.min - axisMargin, screenCoordinate.y)
                  .lineTo(xTicksBounds.max + axisMargin, screenCoordinate.y)
                  .path
                  .strokeColor(Color.gray)
                  .strokeWidth(0.5)
              )
          )
      )
  }
}
