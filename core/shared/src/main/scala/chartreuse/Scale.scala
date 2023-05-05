package chartreuse

import doodle.core.Point

/** A Scale is a bijection (invertible function) from data coordinates to plot
  * coordinates and back.
  */
final case class Scale(dataToPlot: Point => Point, plotToData: Point => Point)
object Scale {
  def linear(
      dataMinX: Double,
      dataMaxX: Double,
      dataMinY: Double,
      dataMaxY: Double,
      plotWidth: Int,
      plotHeight: Int
  ): Scale = {
    val dataWidth = dataMaxX - dataMinX
    val dataHeight = dataMaxY - dataMinY

    val xOffset = -plotWidth / 2.0
    val yOffset = -plotHeight / 2.0

    val xScale = plotWidth.toDouble / dataWidth
    val yScale = plotHeight.toDouble / dataHeight

    Scale(
      dataToPlot = pt =>
        Point(
          (pt.x - dataMinX) * xScale + xOffset,
          (pt.y - dataMinY) * yScale + yOffset
        ),
      plotToData = pt =>
        Point(
          ((pt.x - xOffset) / xScale) + dataMinX,
          ((pt.y - yOffset) / yScale) + dataMinY
        )
    )
  }
}
