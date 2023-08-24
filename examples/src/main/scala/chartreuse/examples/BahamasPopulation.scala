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

package chartreuse.examples

import cats.effect.unsafe.implicits.global
import chartreuse.*
import chartreuse.layout.*
import doodle.core.Color
import doodle.core.Point
import doodle.svg.*
import doodle.syntax.all.*

import scala.collection.immutable.ArraySeq
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("BahamasPopulation")
object BahamasPopulation {
  // Population taken from Our World in Data data set at https://ourworldindata.org/population-growth
  val population =
    ArraySeq(
      Point(0, 1148),
      Point(100, 1252),
      Point(200, 1366),
      Point(300, 1490),
      Point(400, 1626),
      Point(500, 1774),
      Point(600, 1935),
      Point(700, 2111),
      Point(800, 2303),
      Point(900, 2513),
      Point(1000, 2741),
      Point(1100, 2990),
      Point(1200, 3262),
      Point(1300, 3559),
      Point(1400, 3882),
      Point(1500, 4235),
      Point(1600, 4620),
      Point(1700, 5040),
      Point(1710, 5535),
      Point(1720, 6077),
      Point(1730, 6673),
      Point(1740, 7327),
      Point(1750, 8046),
      Point(1760, 8835),
      Point(1770, 9701),
      Point(1780, 10652),
      Point(1790, 11696),
      Point(1800, 27350),
      Point(1801, 27350),
      Point(1802, 27350),
      Point(1803, 27350),
      Point(1804, 27350),
      Point(1805, 27350),
      Point(1806, 27350),
      Point(1807, 27350),
      Point(1808, 27350),
      Point(1809, 27350),
      Point(1810, 27350),
      Point(1811, 27350),
      Point(1812, 27350),
      Point(1813, 27350),
      Point(1814, 27350),
      Point(1815, 27350),
      Point(1816, 27350),
      Point(1817, 27350),
      Point(1818, 27350),
      Point(1819, 27354),
      Point(1820, 27363),
      Point(1821, 27376),
      Point(1822, 27393),
      Point(1823, 27414),
      Point(1824, 27436),
      Point(1825, 27457),
      Point(1826, 27479),
      Point(1827, 27500),
      Point(1828, 27522),
      Point(1829, 27543),
      Point(1830, 27565),
      Point(1831, 27587),
      Point(1832, 27608),
      Point(1833, 27630),
      Point(1834, 27651),
      Point(1835, 27673),
      Point(1836, 27695),
      Point(1837, 27716),
      Point(1838, 27738),
      Point(1839, 27760),
      Point(1840, 27782),
      Point(1841, 27803),
      Point(1842, 27825),
      Point(1843, 27847),
      Point(1844, 27869),
      Point(1845, 27891),
      Point(1846, 27912),
      Point(1847, 27934),
      Point(1848, 27956),
      Point(1849, 28100),
      Point(1850, 28369),
      Point(1851, 28820),
      Point(1852, 29403),
      Point(1853, 30124),
      Point(1854, 30861),
      Point(1855, 31617),
      Point(1856, 32391),
      Point(1857, 33184),
      Point(1858, 33996),
      Point(1859, 34745),
      Point(1860, 35428),
      Point(1861, 36041),
      Point(1862, 36583),
      Point(1863, 37049),
      Point(1864, 37521),
      Point(1865, 38000),
      Point(1866, 38484),
      Point(1867, 38974),
      Point(1868, 39470),
      Point(1869, 39983),
      Point(1870, 40512),
      Point(1871, 41059),
      Point(1872, 41623),
      Point(1873, 42204),
      Point(1874, 42794),
      Point(1875, 43392),
      Point(1876, 43998),
      Point(1877, 44612),
      Point(1878, 45235),
      Point(1879, 45835),
      Point(1880, 46411),
      Point(1881, 46962),
      Point(1882, 47489),
      Point(1883, 47989),
      Point(1884, 48495),
      Point(1885, 49006),
      Point(1886, 49522),
      Point(1887, 50043),
      Point(1888, 50570),
      Point(1889, 51134),
      Point(1890, 51736),
      Point(1891, 52378),
      Point(1892, 53060),
      Point(1893, 53782),
      Point(1894, 54515),
      Point(1895, 55257),
      Point(1896, 56010),
      Point(1897, 56772),
      Point(1898, 57544),
      Point(1899, 58230),
      Point(1900, 58828),
      Point(1901, 59335),
      Point(1902, 59751),
      Point(1903, 60072),
      Point(1904, 60394),
      Point(1905, 60718),
      Point(1906, 61044),
      Point(1907, 61371),
      Point(1908, 61700),
      Point(1909, 61917),
      Point(1910, 62021),
      Point(1911, 62013),
      Point(1912, 61891),
      Point(1913, 61655),
      Point(1914, 61420),
      Point(1915, 61186),
      Point(1916, 60953),
      Point(1917, 60720),
      Point(1918, 60387),
      Point(1919, 60270),
      Point(1920, 60371),
      Point(1921, 60692),
      Point(1922, 61233),
      Point(1923, 61998),
      Point(1924, 62772),
      Point(1925, 63555),
      Point(1926, 64348),
      Point(1927, 65152),
      Point(1928, 65965),
      Point(1929, 66810),
      Point(1930, 67687),
      Point(1931, 68598),
      Point(1932, 69542),
      Point(1933, 70521),
      Point(1934, 71513),
      Point(1935, 72520),
      Point(1936, 73540),
      Point(1937, 74575),
      Point(1938, 75625),
      Point(1939, 76504),
      Point(1940, 77209),
      Point(1941, 77738),
      Point(1942, 78088),
      Point(1943, 78256),
      Point(1944, 78425),
      Point(1945, 78593),
      Point(1946, 78762),
      Point(1947, 78932),
      Point(1948, 79101),
      Point(1949, 79993),
      Point(1950, 81651),
      Point(1951, 82915),
      Point(1952, 84605),
      Point(1953, 86752),
      Point(1954, 89364),
      Point(1955, 92458),
      Point(1956, 96021),
      Point(1957, 100016),
      Point(1958, 104423),
      Point(1959, 109247),
      Point(1960, 114512),
      Point(1961, 120227),
      Point(1962, 126320),
      Point(1963, 132655),
      Point(1964, 138798),
      Point(1965, 144862),
      Point(1966, 151343),
      Point(1967, 158210),
      Point(1968, 165469),
      Point(1969, 172761),
      Point(1970, 179149),
      Point(1971, 184447),
      Point(1972, 189194),
      Point(1973, 193577),
      Point(1974, 197771),
      Point(1975, 201900),
      Point(1976, 206106),
      Point(1977, 210540),
      Point(1978, 215032),
      Point(1979, 219437),
      Point(1980, 223761),
      Point(1981, 227971),
      Point(1982, 232184),
      Point(1983, 236595),
      Point(1984, 241176),
      Point(1985, 245945),
      Point(1986, 250815),
      Point(1987, 255626),
      Point(1988, 260458),
      Point(1989, 265505),
      Point(1990, 270690),
      Point(1991, 276072),
      Point(1992, 281982),
      Point(1993, 288172),
      Point(1994, 294006),
      Point(1995, 299567),
      Point(1996, 304944),
      Point(1997, 310183),
      Point(1998, 315410),
      Point(1999, 320282),
      Point(2000, 325033),
      Point(2001, 329639),
      Point(2002, 334015),
      Point(2003, 338505),
      Point(2004, 343097),
      Point(2005, 347815),
      Point(2006, 352672),
      Point(2007, 357678),
      Point(2008, 362807),
      Point(2009, 368064),
      Point(2010, 373277),
      Point(2011, 377956),
      Point(2012, 382073),
      Point(2013, 385660),
      Point(2014, 389137),
      Point(2015, 392707),
      Point(2016, 395986),
      Point(2017, 399027),
      Point(2018, 401911),
      Point(2019, 404563),
      Point(2020, 406478),
      Point(2021, 407920)
    )

  val line =
    Line
      .default[Point]
      .forThemeable(theme =>
        theme
          .withStrokeColor(Themeable.Override(Some(Color.darkBlue)))
          .withStrokeWidth(Themeable.Override(3.0))
      )
  val curve =
    Curve
      .default[Point]
      .forThemeable(theme =>
        theme
          .withStrokeColor(Themeable.Override(Some(Color.lawngreen)))
          .withStrokeWidth(Themeable.Override(7.0))
      )

  val plot =
    Plot(
      List(
        line.toLayer(population).withColor(Color.darkBlue).withLabel("Line"),
        curve.toLayer(population).withColor(Color.lawngreen).withLabel("Curve")
      )
    )
      .withPlotTitle("Bahamas Population")
      .withYTitle("Estimated Population")
      .withXTitle("Year")
      .withLegend(true)

  @JSExport
  def draw(id: String): Unit =
    plot.draw(640, 480).drawWithFrame(Frame(id))
}
