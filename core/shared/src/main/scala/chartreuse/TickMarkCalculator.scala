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
  def calculateTickScale(
      min: Double,
      max: Double,
      tickCount: Double
  ): Ticks = {

    /** The purpose of adding a margin is to ensure that the extreme values of
      * the range are not placed exactly on the edge of the chart, as that could
      * make them less visible. Using 1e6 as a divisor would divide the range
      * into one million equal parts. This would result in a margin that is
      * extremely small compared to the range, ensuring that the extreme values
      * are slightly offset from the edge of the chart.
      */
    val margin: Double = (max - min) / 1e6
    val newMax = max + margin
    val newMin = min - margin
    val range: Double = newMax - newMin

    val roughTickSize: Double = range / (tickCount - 1)

    /** The purpose of calculating tickSizePower is to normalize the tick size
      * to a convenient range, specifically between 1 and 10. It normalizes the
      * rough tick size by multiplying it by a power of 10 which makes it
      * between 1 and 10. For example: if the rough tick size is 0.003, we take
      * log10(0.003), which will result in approximately -2.52. We then floor
      * this value and take the negative of it. The purpose of flooring is for
      * the power to be an integer. The purpose of taking the negative value of
      * the power is to invert the scale of the rough tick size. Finally, to
      * calculate tickSizePower, we have to put 10 to the power of the
      * -log10(roughTickSize.abs).floor. Then roughTickSize will be multiplied
      * by 1000 to get 3, which is in the range between 1 and 10.
      */
    val tickSizePower: Double = pow(10, -log10(roughTickSize.abs).floor)
    val normalizedTickSize: Double = roughTickSize * tickSizePower

    /** These values represent commonly used intervals between tick marks. The
      * function selects the smallest value that is greater than or equal to the
      * normalized tick size. For example, if the normalized tick size is 3.2,
      * it will choose 5 as the good normalized tick size.
      */
    val goodNormalizedTickSizes: List[Double] = List(1, 1.5, 2, 2.5, 5, 7.5, 10)
    val goodNormalizedTick: Double =
      goodNormalizedTickSizes.find(_ >= normalizedTickSize).get

    /** It converts the good normalized tick size back to the original scale by
      * dividing it by the same power of 10 that was used to normalize it. This
      * is the final tick size that will be used for the graph axis.
      */
    val tickSize: Double = goodNormalizedTick / tickSizePower

    /** We are taking ceiling and floor here to adjust the maximum and minimum
      * values for the tick marks to be multiples of the tick size.
      */
    val tickMax: Double = ceil(max / tickSize) * tickSize
    val tickMin: Double = floor(min / tickSize) * tickSize

    Ticks(
      (tickMin * 1000).round / 1000.0,
      (tickMax * 1000).round / 1000.0,
      (tickSize * 1000).round / 1000.0
    )
  }
}
