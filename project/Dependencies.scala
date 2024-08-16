import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  // Library Versions
  val doodleVersion = "0.23.0"

  val munitVersion = "0.7.29"

  // Libraries
  val doodle =
    Def.setting("org.creativescala" %%% "doodle" % doodleVersion)

  val munit = Def.setting("org.scalameta" %%% "munit" % munitVersion % "test")
  val munitScalaCheck =
    Def.setting("org.scalameta" %%% "munit-scalacheck" % munitVersion % "test")
}
