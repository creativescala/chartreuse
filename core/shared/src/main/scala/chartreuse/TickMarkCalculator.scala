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

import scala.math.ceil
import scala.math.floor
import scala.math.log10
import scala.math.pow

object TickMarkCalculator {

  /** Calculates tick marks for a given range and tick count.
    *
    * Takes a minimum value, maximum value, and tick count as input parameters
    * and calculates tick marks that evenly divide the range between the minimum
    * and maximum values. Tick marks are the values displayed on an axis of a
    * graph or chart to indicate reference points. The purpose of calculating
    * tick marks is to determine appropriate values for the axis labels and grid
    * lines to ensure that data points are visually represented in a clear and
    * understandable manner.
    *
    * The algorithm always tries to generate round tick values so even if you
    * ask for seven ticks, you might get six if that fits better with the
    * rounding.
    *
    * The algorithm doesn't necessarily maintain any of the bounds. It may leave
    * the output bound the same as the input bound if it aligns with the output
    * `tickSize`,
    * i.e. if it is a multiple of `tickSize`. If the output bound is not a
    * multiple of the `tickSize`, it will be adjusted to the nearest `tickSize`
    * multiple. For example, `calculateTickScale(0.0, 10.0, 3)` outputs
    * `Ticks(0.0, 15.0, 7.5)` and `calculateTickScale(-0.5, 10.0, 3)` outputs
    * `Ticks(-7.5, 15.0, 7.5)`.
    *
    * The method maintains the following invariants:
    *   1. `tickCount` should be greater than or equal to 2. This ensures that
    *      there are at least two tick marks to define a range.
    *   1. `max` should be greater than `min`. It assumes that the range is
    *      valid and not empty.
    *   1. `goodNormalizedTickSizes` list should always be sorted in ascending
    *      order. This is important for the algorithm to select the smallest
    *      value greater than or equal to the normalized tick size.
    *   1. `tickSize` should be a positive value greater than 0, as it
    *      represents a non-empty interval between tick marks.
    *
    * @param min
    *   The minimum value of the range.
    * @param max
    *   The maximum value of the range.
    * @param tickCount
    *   The desired number of tick marks.
    * @return
    *   An instance of the Ticks class containing the coordinates of the first
    *   and last tick marks and the step size.
    * @version 1.0
    * @see
    *   [[Ticks]] [[Plot]]
    */
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
