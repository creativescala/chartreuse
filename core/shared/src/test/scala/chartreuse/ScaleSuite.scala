package chartreuse

import doodle.core.Point
import munit.FunSuite

class ScaleSuite extends FunSuite {
  test("linear scale maps data to expected plot coordinates") {
    val scale = Scale.linear(-100, 100, -10, 20, 360, 240)

    assertEquals(scale.dataToPlot(Point(-100, -10)), Point(-180, -120))
    assertEquals(scale.dataToPlot(Point(100, -10)), Point(180, -120))
    assertEquals(scale.dataToPlot(Point(100, 20)), Point(180, 120))
    assertEquals(scale.dataToPlot(Point(-100, 20)), Point(-180, 120))
  }

  test("linear scale maps plot to expected data coordinates") {
    val scale = Scale.linear(-100, 100, -10, 20, 360, 240)

    assertEquals(scale.plotToData(Point(-180, -120)), Point(-100, -10))
    assertEquals(scale.plotToData(Point(180, -120)), Point(100, -10))
    assertEquals(scale.plotToData(Point(180, 120)), Point(100, 20))
    assertEquals(scale.plotToData(Point(-180, 120)), Point(-100, 20))
  }
}
