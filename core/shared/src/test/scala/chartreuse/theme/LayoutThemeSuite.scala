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

import chartreuse.Themeable
import doodle.core.Color
import munit.FunSuite

class LayoutThemeSuite extends FunSuite {
  val exampleTheme = PlotTheme.base16.defaultLight.layerThemes.head
  val default = LayoutTheme.default[Themeable]

  test("Default Themeable LayoutTheme has all Default values") {
    assert(default.strokeColor.isDefault)
    assert(default.strokeWidth.isDefault)
    assert(default.fillColor.isDefault)
  }

  test("Theming chooses theme values over Default values") {
    val themed = exampleTheme.theme(default)
    assertEquals(themed.strokeColor, exampleTheme.strokeColor)
    assertEquals(themed.strokeWidth, exampleTheme.strokeWidth)
    assertEquals(themed.fillColor, exampleTheme.fillColor)
  }

  test("Theming chooses Override values over theme values") {
    val overriden =
      default
        .withStrokeColor(Themeable.Override(Some(Color.chartreuse)))
        .withFillColor(Themeable.Override(Some(Color.aliceBlue)))
        .withStrokeWidth(Themeable.Override(5.0))

    val themed = exampleTheme.theme(overriden)

    assertEquals(themed.strokeColor, Some(Color.chartreuse))
    assertEquals(themed.strokeWidth, 5.0)
    assertEquals(themed.fillColor, Some(Color.aliceBlue))
  }
}
