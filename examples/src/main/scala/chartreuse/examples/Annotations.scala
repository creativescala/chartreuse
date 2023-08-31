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
import chartreuse.component.*
import chartreuse.layout.*
import chartreuse.theme.PlotTheme
import doodle.core.Point
import doodle.svg.*
import doodle.syntax.all.*

import scala.collection.immutable.ArraySeq
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Annotations")
object Annotations {
  val xData = ArraySeq.from(
    "7.78\n9.74\n6.76\n6.17\n0.93\n2.14\n0.3\n1.82\n0.81\n1.98\n4.56\n2.05\n5.3\n0\n7.85\n8.49\n7.52\n5.38\n3.42\n0.09\n12.14\n11.74\n2.48\n4.65\n6.29\n3.1\n20.95\n8.26\n17.95\n3.02\n7.84\n0.88\n4.56\n2.11\n11.48\n10.52\n9.61\n7.24\n12.96\n9.17\n19.01\n0.04\n9.52\n6.6\n4.62\n3.83\n1.07\n7.32\n13.5\n4.71\n5.58\n0.78\n4.1\n0.03\n16.2\n7.73\n7.04\n2.13\n6.51\n6.67\n5.47\n7.71\n7.3\n7.88\n3.79\n1.93\n6.11\n5.36\n11.36\n5.07\n6.41\n5.49\n11\n5.49\n2.46\n14.18\n4.35\n4.87\n20.02\n8.82\n12.09\n11.81\n13.6\n16.41\n11.4\n14.09\n4.97\n5.21\n6.93\n9.75\n8.1\n14.53\n1.66\n6.78\n1.37\n6.79\n0\n4.96\n3.87\n6.67\n6.63\n3.72\n4.37\n6.78\n6.21\n4.45\n4.59\n7.39\n3.84\n3.48\n4.38\n4.92\n4.72\n6.5\n4.08\n0.76\n4.6\n4.35\n5.25\n4.14\n6.59\n7.11\n4.17\n4.77\n7.47\n5.64\n4\n3.93\n7.01\n6.4\n1.02\n7.12\n1.2\n3.01\n3.79\n7.29\n6.56\n1.81\n4.6\n4.16\n5.47\n1.38\n4.3\n6.13\n5.88\n5.16\n6.66\n0.31\n1.73\n3.97\n4.77\n3.79\n6.56\n5.07\n4.17\n2.87\n10.01\n6.33\n1.3\n4.16\n5.68\n4.27\n6.31\n6.58\n2.27\n6.35\n4.21\n1.97\n3.43\n6.27\n6.89\n6.28\n2.76\n4.1\n7.21\n7.02\n8.38\n6.38\n7.52\n6.22\n7.9\n11.61\n7.97\n6.11\n3.11\n6.92\n9.31\n7.89\n6.66\n0.89\n6.3\n2.52\n0.02\n5.54\n4.34\n2.91\n6.21\n5.16\n0.1\n4.64\n5.47\n5.48\n5.42\n6.58\n5.62\n5.07\n4.6\n2.69\n6.18\n6.65\n5.81\n6.13\n7.08\n6.28\n6.51\n6.54\n7\n6.34\n7.7\n6.37\n6.19\n2.71\n0\n7.13\n3.57\n1.72\n5.9\n3.8\n6.46\n5.42\n5.97\n7.55\n5.02\n4.06\n6.36\n5.03\n5.23\n5.26\n0.86\n6.78\n6.66\n6.63\n7.35\n5.54\n6.73\n6.62\n6.46\n8.41\n7.84\n5.88\n4.57\n6.5\n5.86\n5.5\n5.8\n7.16\n1.39\n8.23\n4.67\n2.16\n5.02\n0.05\n5.29\n3.09\n5.83\n5.56\n4.89\n1.98\n3.17\n6.11\n2.35\n6.35\n3.28\n6.23\n5.09\n4.68\n5.42\n4.67\n2.76\n3.24\n0.63\n4.95\n4.4\n0.03\n4.64\n4.94\n0.79\n2.68\n2.22\n5.17\n4.99\n3.47\n6.3\n5.46\n4.66\n1.84\n1.26\n3.97\n2.33\n1.1\n6.53\n1.23\n6.28\n6.17\n4.84\n3.11\n3.04\n3.05\n5\n1.25\n4.65\n3.41\n1.03\n5.92\n4.06\n6.13\n5.4\n6.83\n7.2\n6.88\n6.65\n6.8\n7.03\n3.07\n1.07\n2.66\n6.55\n3.6\n5.21\n1.27\n3.49\n3.85\n4.01\n0.93\n0.75\n2.26\n0.7\n6.06\n2.86\n5.94\n6.31\n4.76\n6.47\n5.61\n9.03\n6.95\n7.38\n6.69\n6.96\n6.86\n7.1\n4.66\n8.31\n6.64\n8.18\n3.36\n7.8\n1.83\n5.06\n6.95\n5.61\n4.87\n7.06\n2.34\n4.27\n7.08\n3.04\n1.88\n6.57\n4.88\n2.08\n4.17\n5.67\n3.7\n3.57\n4.4\n3.66\n4.56\n3.63\n0\n0\n4.16\n2.54\n3.49\n1.4\n2.28\n1.4\n0.46\n2.5\n2.32\n6.19\n6.93\n6.76\n6.83\n6.77\n6.54\n6.85\n7.08\n6.57\n6.87\n6.76\n6.73\n6.81\n6.26\n6.85\n6.2\n6.78\n6.92\n6.91\n6.84\n4.51\n6.93\n6.93\n6.92\n6.96\n6.14\n6.73\n6.81\n6.83\n7.13\n5.71\n6.97\n6.91\n6.71\n5.79\n6.93\n6.99\n7.03\n6.96\n5.94\n6.71\n6.94\n6.96\n6.93\n5.71\n6.98\n5.72\n6.23\n5.9\n5.94\n6.02\n6.25\n6.04\n6.3\n5.9\n5.55\n6.41\n6.63\n6.56\n6.3\n5.81\n5.74\n5.83\n6.63\n6.89\n6.87\n5.78\n5.66\n6.03\n6.21\n5.77\n6.08\n6.24\n6.08\n6.57\n5.57\n6.57\n5.52\n6.32\n5.65\n6.12\n6.7\n6.9\n6.19\n6.51\n6.28\n6.22\n6.05\n6.68\n6.41\n6.14\n6.97\n6.93\n6.93\n6.89\n6.28\n6.98\n7.02\n7.03\n7.03\n6.83\n7.12\n6.72\n6.9\n6.94\n6.92\n6.84\n7.11\n6.73\n6.84"
      .split("\n")
  )
  val yData = ArraySeq.from(
    "1.510555556\n2.177222222\n4.671666667\n1.768333333\n0.298611111\n0.422222222\n0.64\n1.010833333\n0.179166667\n0.387777778\n2.419444444\n0.690833333\n3.019444444\n0.365\n4.141111111\n1.658055556\n2.157222222\n2.294166667\n0.841944444\n0.032222222\n2.134166667\n2.886111111\n2.436944444\n1.505555556\n3.114722222\n0.595833333\n3.658333333\n3.460555556\n3.543611111\n0.709722222\n4.953055556\n0.3475\n2.161111111\n1.487222222\n1.998333333\n3.685555556\n3.028055556\n3.803333333\n3.755833333\n3.053888889\n3.315555556\n0.033333333\n1.914722222\n1.405555556\n1.151666667\n0.885277778\n0.367222222\n1.527222222\n2.549722222\n1.074722222\n1.247222222\n0.241111111\n1.03\n0.021111111\n2.934722222\n1.651111111\n2.568611111\n0.828611111\n2.320833333\n4.082222222\n1.961388889\n3.696111111\n3.531388889\n3.363333333\n4.147222222\n0.998055556\n2.003888889\n3.470555556\n2.311944444\n3.3375\n2.395555556\n2.971111111\n1.894444444\n2.801666667\n0.8025\n3.964444444\n3.0975\n0.865277778\n3.610555556\n3.551666667\n3.708333333\n2.093055556\n2.681388889\n3.776388889\n4.437777778\n3.643055556\n0.891388889\n1.646388889\n3.39\n3.046388889\n1.538333333\n3.69\n0.313611111\n3.231388889\n1.932777778\n2.981388889\n0.208888889\n3.185277778\n2.426111111\n3.805\n3.613333333\n4.049444444\n3.788888889\n2.585\n2.127777778\n2.473611111\n3.652777778\n2.544444444\n3.805277778\n1.244722222\n2.551944444\n2.144722222\n3.881388889\n2.665833333\n1.468333333\n0.263611111\n3.771666667\n3.986944444\n2.526388889\n2.178888889\n3.727222222\n2.335277778\n1.356388889\n2.771666667\n3.498055556\n3.431388889\n2.575555556\n3.980555556\n3.440555556\n3.618611111\n4.056944444\n3.983611111\n0.401388889\n0.989444444\n2.571944444\n3.592777778\n3.5325\n3.805555556\n3.796388889\n3.208333333\n3.293888889\n3.680555556\n3.47\n2.751388889\n1.890555556\n2.881111111\n3.178055556\n1.846111111\n0.578333333\n1.311666667\n3.406666667\n2.088611111\n3.940555556\n3.983611111\n2.016388889\n3.966666667\n3.018055556\n3.591944444\n3.461944444\n3.081111111\n2.894444444\n4.144166667\n3.543055556\n3.320833333\n0.527777778\n2.434444444\n2\n0.652222222\n1.426666667\n2.291666667\n3.619722222\n2.4475\n4.057222222\n55.23805556\n5.545\n2.406111111\n3.414166667\n4.266388889\n2.507222222\n2.394166667\n3.575555556\n3.900833333\n2.901388889\n3.258055556\n5.142222222\n3.090555556\n5.278333333\n5.3325\n3.495833333\n0.305\n5.699166667\n0.810277778\n0.040555556\n2.731388889\n1.373333333\n0.936388889\n3.309722222\n2.709444444\n0.056666667\n1.434722222\n2.24\n4.051388889\n2.312777778\n4.7025\n2.935555556\n2.595833333\n2.603611111\n0.909722222\n3.215555556\n4.683055556\n2.453888889\n1.936388889\n2.533611111\n6.431666667\n4.811944444\n4.633055556\n2.559722222\n4.770277778\n4.715833333\n5.851111111\n4.843888889\n2.830555556\n2.034166667\n2.717777778\n1.362222222\n3.228333333\n2.2925\n1.211944444\n2.484722222\n1.769444444\n2.128888889\n4.105833333\n3.878611111\n1.894444444\n3.225277778\n2.274166667\n5.021666667\n3.684722222\n4.394444444\n4.247777778\n4.000277778\n3.31\n5.597222222\n4.304722222\n5.718055556\n4.476111111\n4.751388889\n3.080277778\n5.924722222\n4.701111111\n1.624166667\n2.371666667\n2.741111111\n3.410277778\n2.7825\n2.897222222\n1.059722222\n4.961111111\n2.707777778\n2.993055556\n2.743888889\n2.7125\n3.136388889\n1.789722222\n4.828888889\n2.511111111\n4.941111111\n0.655833333\n2.920833333\n1.952777778\n1.155\n3.046944444\n1.075277778\n3.077222222\n6.0525\n2.746111111\n5.501944444\n2.830833333\n0.873055556\n1.47\n3.604444444\n3.078611111\n4.66\n1.292222222\n1.91\n1.765277778\n2.613611111\n1.903888889\n0.729444444\n1.638611111\n1.700555556\n5.408611111\n5.258333333\n8.659722222\n1.871666667\n0.761388889\n2.421944444\n1.247222222\n0.738611111\n5.046666667\n2.330277778\n4.665\n2.253611111\n8.845555556\n2.864444444\n4.438888889\n4.811111111\n4.861666667\n2.780833333\n4.065277778\n2.813333333\n1.227222222\n2.474166667\n4.397222222\n1.321666667\n3.918055556\n3.180555556\n2.872222222\n2.936388889\n2.235\n2.1575\n2.252777778\n3.877777778\n1.411388889\n0.351666667\n2.429166667\n2.298333333\n3.634166667\n3.629444444\n1.245555556\n3.039444444\n2.763888889\n1.846666667\n2.365277778\n1.611944444\n3.851944444\n3.658611111\n3.189166667\n3.894722222\n2.954166667\n3.472222222\n2.911111111\n6.602777778\n3.413055556\n3.140277778\n2.455277778\n2.483055556\n2.245833333\n4.738333333\n3.580277778\n3.724444444\n3.179722222\n3.830277778\n2.188611111\n3.791944444\n1.1\n2.905833333\n0.645555556\n7.072777778\n2.506388889\n1.878333333\n1.822222222\n2.358611111\n1.533333333\n1.463611111\n2.867777778\n0.970277778\n1.301388889\n3.6975\n1.576944444\n0.726111111\n1.379166667\n2.029722222\n1.219166667\n1.149444444\n1.431111111\n1.1925\n1.511666667\n1.207222222\n0.014722222\n0.024722222\n1.439722222\n0.881111111\n1.141666667\n0.481388889\n1.188055556\n0.803888889\n1.213055556\n0.832777778\n0.791111111\n2.345833333\n2.854444444\n2.728055556\n2.680555556\n2.867222222\n2.863055556\n2.180555556\n2.927777778\n2.958055556\n3.407222222\n4.591388889\n3.650555556\n3.285\n3.314166667\n3.2075\n3.704166667\n4.896944444\n3.515555556\n2.901666667\n2.910555556\n1.681944444\n4.260277778\n3.617222222\n4.618888889\n3.319722222\n3.069166667\n3.272222222\n3.139166667\n3.595833333\n3.493888889\n4.517222222\n3.234444444\n3.895277778\n3.236944444\n3.220277778\n3.965\n3.01\n2.895\n3.689722222\n3.354166667\n3.279166667\n3.099722222\n2.601388889\n2.737777778\n3.648888889\n2.873888889\n3.420833333\n3.838611111\n3.48\n3.309722222\n3.531388889\n3.732222222\n3.286944444\n3.454444444\n3.896666667\n3.061944444\n3.445\n3.0725\n3.429722222\n3.493333333\n3.949166667\n3.036944444\n3.5025\n4.174444444\n2.351666667\n3.669166667\n2.051666667\n2.265\n2.755833333\n2.459166667\n3.181388889\n2.946111111\n2.3525\n4.2075\n2.503055556\n3.225555556\n2.775555556\n3.193611111\n3.411944444\n2.620555556\n4.318888889\n3.826388889\n3.128611111\n3.520833333\n2.580833333\n2.627222222\n2.714444444\n3.173333333\n2.823055556\n3.058055556\n6.293888889\n3.2575\n3.793888889\n3\n3.269722222\n3.428055556\n3.113611111\n2.631111111\n2.958333333\n3.289722222\n3.370833333\n3.436944444\n2.9725\n2.588888889\n3.293611111\n2.821111111\n3.196388889\n3.213055556\n3.121944444\n3.106111111"
      .split("\n")
  )
  val chargeData: ArraySeq[Point] = xData.zip(yData).map { case (x, y) =>
    Point(x.toDouble, y.toDouble)
  }

  val scatter =
    Scatter
      .default[Point]

  val error = chargeData.filter(_.y > 50).head

  val annotation: Annotation = Annotation(
    error,
    AnnotationType.CircleWithText(15, "Bad battery")
  )

  val plot =
    Plot(scatter.toLayer(chargeData))
      .withPlotTitle("EV Charging")
      .withYTitle("Charge Time (in hours)")
      .withXTitle("kwhTotal")
      .withGrid(true)

  @JSExport
  def drawDefault(id: String): Unit =
    plot
      .addAnnotation(annotation)
      .draw(640, 480, PlotTheme.fiveThirtyEight)
      .drawWithFrame(Frame(id))

  @JSExport
  def drawWithPositioning(id: String): Unit =
    plot
      .addAnnotation(
        annotation.withAnnotationPosition(AnnotationPosition.bottomRight)
      )
      .draw(640, 480, PlotTheme.fiveThirtyEight)
      .drawWithFrame(Frame(id))

  @JSExport
  def drawWithPositioningAndArrow(id: String): Unit =
    plot
      .addAnnotation(
        annotation
          .withAnnotationPosition(AnnotationPosition.bottomRight)
          .withArrow()
      )
      .draw(640, 480, PlotTheme.fiveThirtyEight)
      .drawWithFrame(Frame(id))
}
