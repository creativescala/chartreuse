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

package chartreuse

import doodle.core.BoundingBox
import doodle.core.Point
import munit.FunSuite

class ScaleSuite extends FunSuite {
  test("linear scale maps data to expected plot coordinates") {
    val bb = BoundingBox(-100, 20, 100, -10)
    val scale = Scale.linear.build(bb, 360, 240)

    assertEquals(scale(Point(-100, -10)), Point(-180, -120))
    assertEquals(scale(Point(100, -10)), Point(180, -120))
    assertEquals(scale(Point(100, 20)), Point(180, 120))
    assertEquals(scale(Point(-100, 20)), Point(-180, 120))
  }

  test("linear scale maps plot to expected data coordinates") {
    val bb = BoundingBox(-100, 20, 100, -10)
    val scale = Scale.linear.build(bb, 360, 240)

    assertEquals(scale.invert(Point(-180, -120)), Point(-100, -10))
    assertEquals(scale.invert(Point(180, -120)), Point(100, -10))
    assertEquals(scale.invert(Point(180, 120)), Point(100, 20))
    assertEquals(scale.invert(Point(-180, 120)), Point(-100, 20))
  }
}
