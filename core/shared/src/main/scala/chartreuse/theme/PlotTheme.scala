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
import cats.syntax.all.*
import doodle.algebra.Picture
import doodle.algebra.Style
import doodle.algebra.Text
import doodle.core.Color
import doodle.core.font.Font
import doodle.syntax.all.*

/** Generic container for the theme of a [[chartreuse.Plot]]. Contains one or
  * more [[chartreuse.theme.LayoutTheme]]. Each layer in the plot will take a
  * successive theme from this sequence. In case there are more layers than
  * themes, the sequence will start again at the beginning.
  */
// There are more elements that could be themed, such as font family, size, and
// so on. Expand this as needed.
final case class PlotTheme[F[_]: Applicative](
    textStrokeColor: F[Option[Color]],
    textFillColor: F[Option[Color]],
    headingFont: F[Font],
    normalFont: F[Font],
    layerThemes: NonEmptySeq[LayoutTheme[F]]
) {

  /** Builder method to set the fillColor for text of this [[PlotTheme]]. */
  def withTextFillColor(fillColor: F[Option[Color]]): PlotTheme[F] =
    this.copy(textFillColor = fillColor)

  /** Convenience builder method to set the fillColor for text of this
    * [[PlotTheme]]. This will call the `pure` method on `F` to construct an
    * instance of `F[Option[Color]]`. For a [[chartreuse.Themeable]] value this
    * will be `Default` value, not an `Override`, which is probably not what you
    * want. Use the other overloaded `withFillColor` method instead.
    */
  def withTextFillColor(fillColor: Color): PlotTheme[F] =
    this.copy(textFillColor = fillColor.some.pure[F])

  /** Convenience builder method to set the fillColor for text of this
    * [[PlotTheme]] to `None`. This will call the `pure` method on `F` to
    * construct an instance of `F[Option[Color]]`. For a
    * [[chartreuse.Themeable]] value this will be `Default` value, not an
    * `Override`, which is probably not what you want. Use the `withFillColor`
    * method instead.
    */
  def withTextNoFill: PlotTheme[F] =
    this.copy(textFillColor = none.pure[F])

  /** Builder method to set the strokeColor for text of this [[PlotTheme]]. */
  def withTextStrokeColor(strokeColor: Color): PlotTheme[F] =
    this.copy(textStrokeColor = strokeColor.some.pure[F])

  /** Convenience builder method to set the strokeColor for text of this
    * [[PlotTheme]]. This will call the `pure` method on `F` to construct an
    * instance of `F[Option[Color]]`. For a [[chartreuse.Themeable]] value this
    * will be `Default` value, not an `Override`, which is probably not what you
    * want. Use the other overloaded `withStrokeColor` method instead.
    */
  def withTextStrokeColor(strokeColor: F[Option[Color]]): PlotTheme[F] =
    this.copy(textStrokeColor = strokeColor)

  /** Convenience builder method to set the strokeColor for text of this
    * [[PlotTheme]] to `None`. This will call the `pure` method on `F` to
    * construct an instance of `F[Option[Color]]`. For a
    * [[chartreuse.Themeable]] value this will be `Default` value, not an
    * `Override`, which is probably not what you want. Use the `withStrokeColor`
    * method instead.
    */
  def withTextNoStroke: PlotTheme[F] =
    this.copy(textStrokeColor = none.pure[F])

  /** Builder method to set the font for normal text of this [[PlotTheme]]. */
  def withNormalFont(font: F[Font]): PlotTheme[F] =
    this.copy(normalFont = font)

  /** Builder method to set the font for heading text of this [[PlotTheme]]. */
  def withHeadingFont(font: F[Font]): PlotTheme[F] =
    this.copy(headingFont = font)

  /** Construct text styled using the values from this [[PlotTheme]] for normal
    * text.
    */
  def normalText(string: String)(using
      Comonad[F]
  ): Picture[Style & Text, Unit] = {
    val t1 = text(string).font(normalFont.extract)
    val t2 = textStrokeColor.extract.fold(t1.noStroke)(c => t1.strokeColor(c))
    val t3 = textFillColor.extract.fold(t2.noFill)(c => t1.fillColor(c))

    t3
  }

  /** Construct text styled using the values from this [[PlotTheme]] for heading
    * text.
    */
  def headingText(string: String)(using
      Comonad[F]
  ): Picture[Style & Text, Unit] = {
    val t1 = text(string).font(headingFont.extract)
    val t2 = textStrokeColor.extract.fold(t1.noStroke)(c => t1.strokeColor(c))
    val t3 = textFillColor.extract.fold(t2.noFill)(c => t1.fillColor(c))

    t3
  }

  def addLayerTheme(theme: LayoutTheme[F]): PlotTheme[F] =
    this.copy(layerThemes = layerThemes :+ theme)

  def withLayerThemes(themes: NonEmptySeq[LayoutTheme[F]]): PlotTheme[F] =
    this.copy(layerThemes = themes)

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
    PlotTheme(
      textStrokeColor = Color.black.some.pure[F],
      textFillColor = Color.black.some.pure[F],
      headingFont = Font.defaultSansSerif.size(18).pure[F],
      normalFont = Font.defaultSansSerif.size(12).pure[F],
      layerThemes = NonEmptySeq.of(layerTheme)
    )

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
      textStrokeColor = Color.black.some.pure[Id],
      textFillColor = Color.black.some.pure[Id],
      headingFont = Font.defaultSansSerif.size(18).pure[Id],
      normalFont = Font.defaultSansSerif.size(12).pure[Id],
      layerThemes = colors.map(c =>
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

  /** FiveThirtyEight theme, modeled after https://fivethirtyeight.com/
    *
    * Taken from
    * https://github.com/matplotlib/matplotlib/blob/main/lib/matplotlib/mpl-data/stylelib/fivethirtyeight.mplstyle
    * Copyright (c) 2012- Matplotlib Development Team; All Rights Reserved
    */
  val fiveThirtyEight: PlotTheme[Id] =
    fromColors(
      NonEmptySeq
        .of("008fd5", "fc4f30", "e5ae38", "6d904f", "8b8b8b", "810f7c")
        .map(hexToColor)
    )

  /** Bayesian Methods for Hackers theme, modeled after
    * https://dataorigami.net/Probabilistic-Programming-and-Bayesian-Methods-for-Hackers/
    *
    * Taken from
    * https://github.com/matplotlib/matplotlib/blob/main/lib/matplotlib/mpl-data/stylelib/bmh.mplstyle
    * Copyright (c) 2012- Matplotlib Development Team; All Rights Reserved
    */
  val bmh: PlotTheme[Id] =
    fromColors(
      NonEmptySeq
        .of(
          "348ABD",
          "A60628",
          "7A68A6",
          "467821",
          "D55E00",
          "CC79A7",
          "56B4E9",
          "009E73",
          "F0E442",
          "0072B2"
        )
        .map(hexToColor)
    )

  /** ggplot theme
    *
    * Taken from
    * https://github.com/matplotlib/matplotlib/blob/main/lib/matplotlib/mpl-data/stylelib/ggplot.mplstyle
    * Copyright (c) 2012- Matplotlib Development Team; All Rights Reserved
    */
  val ggplot: PlotTheme[Id] =
    fromColors(
      NonEmptySeq
        .of(
          "E24A33",
          "348ABD",
          "988ED5",
          "777777",
          "FBC15E",
          "8EBA42",
          "FFB5B8"
        )
        .map(hexToColor)
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
