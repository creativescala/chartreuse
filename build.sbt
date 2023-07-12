/*
 * Copyright Creative Scala
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
import scala.sys.process._
import laika.config.LaikaKeys
import laika.rewrite.link.LinkConfig
import laika.rewrite.link.ApiLinks
import laika.theme.Theme

val scala3 = "3.3.0"

ThisBuild / organization := "org.creativescala"
ThisBuild / organizationName := "Creative Scala"
ThisBuild / tlBaseVersion := "0.1" // your current series x.y
ThisBuild / tlSitePublishBranch := Some("main")

ThisBuild / crossScalaVersions := List(scala3)
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers ++= List(
  // your GitHub handle and name
  tlGitHubDev("noelwelsh", "Noel Welsh")
)
ThisBuild / scalaVersion := scala3
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

Global / onChangedBuildSource := ReloadOnSourceChanges

// Run this (build) to do everything involved in building the project
commands += Command.command("build") { state =>
  "dependencyUpdates" ::
    "clean" ::
    "compile" ::
    "test" ::
    "scalafixAll" ::
    "scalafmtAll" ::
    "headerCreateAll" ::
    "docs/tlSite" ::
    state
}

lazy val css = taskKey[Unit]("Build the CSS")

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    Dependencies.doodle.value,
    Dependencies.munit.value,
    Dependencies.munitScalaCheck.value
  )
)

lazy val root = tlCrossRootProject.aggregate(core, examples)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      Dependencies.catsCore.value,
      Dependencies.catsEffect.value,
      Dependencies.catsFree.value
    ),
    moduleName := "chartreuse-core"
  )

lazy val docs = project
  .in(file("docs"))
  .settings(
    commonSettings,
    mdocIn := sourceDirectory.value / "pages",
    mdocOut := target.value / "pages",
    laikaConfig := laikaConfig.value.withConfigValue(
      LinkConfig(apiLinks =
        Seq(
          ApiLinks(baseUri =
            "https://javadoc.io/doc/org.creativescala/chartreuse-docs_3/latest/"
          )
        )
      )
    ),
    Laika / sourceDirectories := Seq(
      mdocOut.value,
      sourceDirectory.value / "templates",
      sourceDirectory.value / "js",
      (examples / Compile / fastOptJS / artifactPath).value
        .getParentFile() / s"${(examples / moduleName).value}-fastopt"
    ),
    laikaExtensions ++= Seq(
      laika.markdown.github.GitHubFlavor,
      laika.parse.code.SyntaxHighlighting,
      CreativeScalaDirectives
    ),
    laikaSite / target := target.value / "chartreuse",
    laikaIncludeEPUB := false,
    laikaIncludePDF := false,
    laikaTheme := Theme.empty,
    css := {
      val src = sourceDirectory.value / "css"
      val dest1 = mdocOut.value
      val dest2 = (laikaSite / target).value
      val cmd1 =
        s"npx tailwindcss -i ${src.toString}/creative-scala.css -o ${dest1.toString}/creative-scala.css"
      val cmd2 =
        s"npx tailwindcss -i ${src.toString}/creative-scala.css -o ${dest2.toString}/creative-scala.css"
      cmd1 !

      cmd2 !
    },
    tlSite := Def
      .sequential(
        (examples / Compile / fastLinkJS),
        mdoc.toTask(""),
        css,
        laikaSite
      )
      .value
  )
  .enablePlugins(TypelevelSitePlugin)
  .dependsOn(core.jvm)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin) // also enables the ScalaUnidocPlugin
  .settings(
    name := "chartreuse-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter :=
      inAnyProject -- inProjects(
        docs,
        core.js,
        examples
      )
  )

lazy val examples = project
  .in(file("examples"))
  .settings(commonSettings, libraryDependencies += Dependencies.doodleSvg.value)
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
