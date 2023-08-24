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
import chartreuse.component.Axis.axisMargin
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

final case class PlotBox(xTicksBounds: TicksBounds, yTicksBounds: TicksBounds) {
  def build: Picture[Path, Unit] = {
    ClosedPath.empty
      .moveTo(xTicksBounds.min - axisMargin, yTicksBounds.min - axisMargin)
      .lineTo(xTicksBounds.max + axisMargin, yTicksBounds.min - axisMargin)
      .lineTo(xTicksBounds.max + axisMargin, yTicksBounds.max + axisMargin)
      .lineTo(xTicksBounds.min - axisMargin, yTicksBounds.max + axisMargin)
      .path
  }
}
