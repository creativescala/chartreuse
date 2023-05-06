import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  // Library Versions
  val catsVersion = "2.9.0"
  val catsEffectVersion = "3.4.10"
  val fs2Version = "3.6.1"

  val doodleVersion = "0.18.0"
  val doodleSvgVersion = "0.15.0"

  val munitVersion = "0.7.29"

  // Libraries
  val doodle =
    Def.setting("org.creativescala" %%% "doodle" % doodleVersion)
  val doodleSvg =
    Def.setting("org.creativescala" %%% "doodle-svg" % doodleSvgVersion)
  val catsEffect =
    Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)
  val catsCore = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
  val catsFree = Def.setting("org.typelevel" %%% "cats-free" % catsVersion)
  val fs2 = Def.setting("co.fs2" %%% "fs2-core" % fs2Version)

  val munit = Def.setting("org.scalameta" %%% "munit" % munitVersion % "test")
  val munitScalaCheck =
    Def.setting("org.scalameta" %%% "munit-scalacheck" % munitVersion % "test")
}
