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

import scala.math.{log10, ceil, floor, pow}

object TickMarkCalculator {
  def calculateTickScale(min: Double, max: Double): (Double, Double, Double) = {
    val margin: Double = (max - min) / 1e6
    val newMax = max + margin
    val newMin = min - margin
    val range: Double = newMax - newMin

    val tickCount: Int = 12
    val roughTickSize: Double = range / (tickCount - 1)
    val goodNormalizedTickSizes: List[Double] = List(1, 1.5, 2, 2.5, 5, 7.5, 10)

    val tickSizePower: Double = pow(10, -log10(roughTickSize.abs).floor)
    val normalizedTickSize: Double = roughTickSize * tickSizePower
    val goodNormalizedTick: Double = goodNormalizedTickSizes.find(_ >= normalizedTickSize).get
    val tickSize: Double = goodNormalizedTick / tickSizePower

    val tickMax: Double = ceil(max / tickSize) * tickSize
    val tickMin: Double = floor(min / tickSize) * tickSize

    (tickMin, tickMax, tickSize)
  }
}
