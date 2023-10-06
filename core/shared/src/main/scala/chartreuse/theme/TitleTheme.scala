package chartreuse.theme

import cats.Applicative
import doodle.core.Color
import doodle.core.font.*

final case class TitleTheme[F[_]: Applicative](
    family: F[FontFamily],
    style: F[FontStyle],
    weight: F[FontWeight],
    size: F[FontSize],
    strokeColor: F[Option[Color]],
    fillColor: F[Option[Color]]
) extends FontTheme[F, TitleTheme[F]] {
  def withFamily(family: F[FontFamily]): TitleTheme[F] =
    this.copy(family = family)

  def withStyle(style: F[FontStyle]): TitleTheme[F] =
    this.copy(style = style)

  def withWeight(weight: F[FontWeight]): TitleTheme[F] =
    this.copy(weight = weight)

  def withSize(size: F[FontSize]): TitleTheme[F] =
    this.copy(size = size)

  def withStrokeColor(strokeColor: F[Option[Color]]): TitleTheme[F] =
    this.copy(strokeColor = strokeColor)

  def withFillColor(fillColor: F[Option[Color]]): TitleTheme[F] =
    this.copy(fillColor = fillColor)
}
