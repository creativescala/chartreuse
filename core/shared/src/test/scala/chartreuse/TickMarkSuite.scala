/*
 * Copyright 2015 Creative Scala
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

import munit.FunSuite

class TickMarkSuite extends FunSuite {
  test("tick mark algorithm chooses the right min, max and step values") {
    assertEquals(
      TickMarkCalculator.calculateTickScale(0.0, 1.0, 12),
      Ticks(0.0, 1.0, 0.1)
    )
    assertEquals(
      TickMarkCalculator.calculateTickScale(0.0, 10.0, 12),
      Ticks(0.0, 10.0, 1.0)
    )
    assertEquals(
      TickMarkCalculator.calculateTickScale(-5.0, 5.0, 12),
      Ticks(-5.0, 5.0, 1.0)
    )
    assertEquals(
      TickMarkCalculator.calculateTickScale(1000.0, 1000000.0, 12),
      Ticks(0.0, 1000000.0, 100000.0)
    )
    assertEquals(
      TickMarkCalculator.calculateTickScale(1.25, 15.12345, 12),
      Ticks(0.0, 16.5, 1.5)
    )
  }
}
