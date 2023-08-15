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

import chartreuse.Layer
import chartreuse.Plot.PlotAlg
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

case class Legend[-Alg <: Algebra](layers: Seq[Layer[?, Alg]]) {
  def build(x: Double, y: Double): Picture[Alg & PlotAlg, Unit] = {
    val circleRadius = 8
    val legendMargin = 6

    val legendContent =
      layers.foldLeft(empty[Alg & PlotAlg])((content, layer) => {
        content.above(
          circle(circleRadius)
            .fillColor(layer.color)
            .margin(0, legendMargin, 0, 0)
            .beside(text(layer.label))
            .originAt(Landmark.topLeft)
        )
      })

    val contentBox =
      legendContent.boundingBox
        .flatMap(bb =>
          rectangle(bb.width + legendMargin * 2, bb.height + legendMargin * 2)
            .fillColor(Color.whiteSmoke)
        )

    legendContent
      .originAt(Landmark.topRight)
      .at(x - legendMargin, y - legendMargin)
      .on(
        contentBox
          .originAt(Landmark.topRight)
          .at(x, y)
      )
  }
}
