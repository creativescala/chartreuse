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

package chartreuse.theme

import cats.Applicative
import cats.Comonad
import cats.Id
import cats.data.NonEmptySeq
import doodle.core.Color

/** Generic container for the theme of a [[chartreuse.Plot]]. Contains one or
  * more [[chartreuse.theme.LayoutTheme]]. Each layer in the plot will take a
  * successive theme from this sequence. In case there are more layers than
  * themes, the sequence will start again at the beginning.
  */
// There are more elements that could be themed, such as font family, size, and
// so on. Expand this as needed.
final case class PlotTheme[F[_]: Applicative](
    layerThemes: NonEmptySeq[LayoutTheme[F]]
) {
  def addLayerTheme(theme: LayoutTheme[F]): PlotTheme[F] =
    this.copy(layerThemes = layerThemes :+ theme)

  /** Produces an [[scala.collection.Iterator]] through the layer themes. This
    * iterator never terminates; it *always* produces a value. The values are
    * the layerThemes in-order, wrapping back to the start if needed. In other
    * words it acts as a circular array.
    */
  def layerThemesIterator(using comonad: Comonad[F]): Iterator[LayoutTheme[F]] =
    new Iterator[LayoutTheme[F]] {
      private var elements = layerThemes.toSeq.toArray
      private var idx = 0

      def hasNext(): Boolean = true

      def next(): LayoutTheme[F] = {
        if idx >= elements.size then idx = 0
        val result = elements(idx)
        idx = idx + 1

        result
      }
    }
}
object PlotTheme {

  /** Convenience constructor to create a [[PlotTheme]] from a single
    * [[LayoutTheme]].
    */
  def apply[F[_]: Applicative](layerTheme: LayoutTheme[F]): PlotTheme[F] =
    PlotTheme(NonEmptySeq.of(layerTheme))

  /** A simple default theme, with colours chosen to look something like the
    * ggplot default theme.
    */
  val default =
    PlotTheme[Id](
      NonEmptySeq
        .of(
          Color.orange,
          Color.purple,
          Color.blue,
          Color.turquoise,
          Color.red,
          Color.pink,
          Color.green
        )
        .map(c => LayoutTheme.default[Id].withFillColor(c).withStrokeColor(c))
    )
}
