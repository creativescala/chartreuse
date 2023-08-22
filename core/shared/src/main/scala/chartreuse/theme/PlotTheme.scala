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

  /** Convert a six-character hex string, which does not start with #, to a
    * `Color`.
    */
  def hexToColor(hex: String): Color = {
    val r = Integer.parseInt(hex.substring(0, 2), 16)
    val g = Integer.parseInt(hex.substring(2, 4), 16)
    val b = Integer.parseInt(hex.substring(4, 6), 16)

    Color.rgb(r, g, b)
  }

  /** Convert a sequence of colors to a [[PlotTheme]]. */
  def fromColors(colors: NonEmptySeq[Color]): PlotTheme[Id] =
    PlotTheme[Id](
      colors.map(c =>
        LayoutTheme.default[Id].withFillColor(c).withStrokeColor(c)
      )
    )

  /** A simple default theme, with colours chosen to look reasonably pleasing.
    */
  val default =
    fromColors(
      NonEmptySeq.of(
        Color.orange,
        Color.purple,
        Color.blue,
        Color.turquoise,
        Color.red,
        Color.pink,
        Color.green
      )
    )

  /** Base 16 themes taken from
    * https://github.com/chriskempson/base16-default-schemes
    */
  object base16 {
    val defaultLight: PlotTheme[Id] =
      fromColors(
        NonEmptySeq
          .of(
            "f8f8f8",
            "e8e8e8",
            "d8d8d8",
            "b8b8b8",
            "585858",
            "383838",
            "282828",
            "181818",
            "ab4642",
            "dc9656",
            "f7ca88",
            "a1b56c",
            "86c1b9",
            "7cafc2",
            "ba8baf",
            "a16946"
          )
          .map(hexToColor)
      )

    val defaultDark: PlotTheme[Id] =
      fromColors(
        NonEmptySeq
          .of(
            "181818",
            "282828",
            "383838",
            "585858",
            "b8b8b8",
            "d8d8d8",
            "e8e8e8",
            "f8f8f8",
            "ab4642",
            "dc9656",
            "f7ca88",
            "a1b56c",
            "86c1b9",
            "7cafc2",
            "ba8baf",
            "a16946"
          )
          .map(hexToColor)
      )

    val cupcake: PlotTheme[Id] =
      fromColors(
        NonEmptySeq
          .of(
            "fbf1f2",
            "f2f1f4",
            "d8d5dd",
            "bfb9c6",
            "a59daf",
            "8b8198",
            "72677E",
            "585062",
            "D57E85",
            "EBB790",
            "DCB16C",
            "A3B367",
            "69A9A7",
            "7297B9",
            "BB99B4",
            "BAA58C"
          )
          .map(hexToColor)
      )

    val eighties: PlotTheme[Id] =
      fromColors(
        NonEmptySeq
          .of(
            "2d2d2d",
            "393939",
            "515151",
            "747369",
            "a09f93",
            "d3d0c8",
            "e8e6df",
            "f2f0ec",
            "f2777a",
            "f99157",
            "ffcc66",
            "99cc99",
            "66cccc",
            "6699cc",
            "cc99cc",
            "d27b53"
          )
          .map(hexToColor)
      )

    val mocha: PlotTheme[Id] =
      fromColors(
        NonEmptySeq
          .of(
            "3B3228",
            "534636",
            "645240",
            "7e705a",
            "b8afad",
            "d0c8c6",
            "e9e1dd",
            "f5eeeb",
            "cb6077",
            "d28b71",
            "f4bc87",
            "beb55b",
            "7bbda4",
            "8ab3b5",
            "a89bb9",
            "bb9584"
          )
          .map(hexToColor)
      )

    val ocean: PlotTheme[Id] =
      fromColors(
        NonEmptySeq
          .of(
            "2b303b",
            "343d46",
            "4f5b66",
            "65737e",
            "a7adba",
            "c0c5ce",
            "dfe1e8",
            "eff1f5",
            "bf616a",
            "d08770",
            "ebcb8b",
            "a3be8c",
            "96b5b4",
            "8fa1b3",
            "b48ead",
            "ab7967"
          )
          .map(hexToColor)
      )
  }
}
