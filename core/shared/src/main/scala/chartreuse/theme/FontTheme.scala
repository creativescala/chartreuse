package chartreuse.theme

import cats.Applicative
import cats.Comonad
import cats.syntax.all.*
import doodle.core.font.*
import doodle.algebra.Picture
import doodle.algebra.Text
import doodle.algebra.Style
import doodle.core.Color
import doodle.syntax.all.*

/** Mix-in for creating themes that set font properties */
trait FontTheme[F[_]: Applicative, Self] { self: Self =>
  val family: F[FontFamily]
  val style: F[FontStyle]
  val weight: F[FontWeight]
  val size: F[FontSize]
  val strokeColor: F[Option[Color]]
  val fillColor: F[Option[Color]]

  def withFamily(family: F[FontFamily]): Self
  def withStyle(style: F[FontStyle]): Self
  def withWeight(weight: F[FontWeight]): Self
  def withSize(size: F[FontSize]): Self
  def withStrokeColor(strokeColor: F[Option[Color]]): Self
  def withFillColor(fillColor: F[Option[Color]]): Self

  def withFamily(family: FontFamily): Self =
    withFamily(family.pure[F])

  def withStyle(style: FontStyle): Self =
    withStyle(style.pure[F])

  def withWeight(weight: FontWeight): Self =
    withWeight(weight.pure[F])

  def withSize(size: FontSize): Self =
    withSize(size.pure[F])

  def withStrokeColor(strokeColor: Color): Self =
    withStrokeColor(strokeColor.some.pure[F])

  def withNoStroke: Self =
    withStrokeColor(none.pure[F])

  def withFillColor(fillColor: Color): Self =
    withFillColor(fillColor.some.pure[F])

  def withNoFill: Self =
    withFillColor(none.pure[F])

  def font: F[Font] =
    (family, style, weight, size).mapN((family, style, weight, size) =>
      Font(family, style, weight, size)
    )

  def text(string: String)(using Comonad[F]): Picture[Style & Text, Unit] = {
    val t1 = text(string).font(font.extract)
    val t2 = strokeColor.extract.fold(t1.noStroke)(c => t1.strokeColor(c))
    val t3 = fillColor.extract.fold(t2.noFill)(c => t1.fillColor(c))

    t3
  }
}
