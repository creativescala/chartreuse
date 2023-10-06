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

import cats.Id
import chartreuse.Layer
import chartreuse.Plot.PlotAlg
import chartreuse.theme.PlotTheme
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

final case class Legend[-Alg <: Algebra](
    layers: Seq[Layer[?, Alg]],
    theme: PlotTheme[Id]
) {
  def build(x: Double, y: Double): Picture[Alg & PlotAlg, Unit] = {
    val circleRadius = 8
    val legendMargin = 6

    val legendContent =
      (layers
        .zip(theme.layerThemesIterator))
        .foldLeft(empty[Alg & PlotAlg])((content, layerAndTheme) => {
          // This code is not ideal, because we're recreating the themed value here,
          // which is created by the layer when it draws.
          val (layer, layerTheme) = layerAndTheme
          val themed = layerTheme.theme(layer.layout.themeable)

          // TODO: take text fill from style
          content.above(
            circle(circleRadius)
              .fillColor(
                themed.strokeColor
                  .orElse(themed.fillColor)
                  .getOrElse(Color.white)
              )
              .margin(0, legendMargin, 0, 0)
              .beside(theme.normalText(layer.label))
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
