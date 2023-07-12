# Quick Start

This section shows how to get started using Chartreuse. 
Refer to other sections for more details on how the library works.


## Library Dependency

To use Chartreuse in your project, add the following to your `build.sbt`:

```scala
libraryDependencies += "org.creativescala" %% "chartreuse" % "@VERSION@"
```


## Creating Data

You need some data to create a visualization.
You presumably have some real data but for this demonstration we'll create some simple fake data.

```scala mdoc:silent
import chartreuse.*
import doodle.core.Point
import scala.util.Random

val data =
  Data(List.fill(100)(Point(Random.nextGaussian(), Random.nextGaussian())))
```

Chartreuse can work with just about any collection class, so it probably doesn't matter how you've stored your data.


## Creating a Plot

Now we have data we can create a plot.
We'll create a simple scatter plot from this data.
In Chartreuse, a plot is made up of layers.
Each layer associates some data with a layout.
In our case the layout is a scatter plot.

```scala mdoc:silent
import chartreuse.layout.ScatterPlot

val layout = ScatterPlot.default[Point]
val layer = Layer(data, layout)(pt => pt)
```

When we constructed the `Layer` we had to specify how to convert the data to `Points`. 
In our case this is just the identity function.

Now we add the layer to a plot, and we can set the title and other properties.

```scala mdoc:silent
val plot = Plot(layer)
  .withPlotTitle("Our Amazing Plot")
  .withXTitle("Awesomeness")
  .withYTitle("Marvellousness")
```

We can convert a `Plot` to a Doodle `Picture` using the `draw`, to which we pass the size of the output.

```scala
val picture = plot.draw(640, 480)
```

Then we can render the `picture` in the usual way for Doodle, which depends on the backend in use. On the JVM we just call the `draw` method. On the JS backend we call `drawWithFrame` passing in the id of the DOM element (a `String`) where we should draw it.

Here are examples. Firstly for the JVM.

```scala
import cats.effect.unsafe.implicits.global
import doodle.java2d.*
import doodle.syntax.all.*

picture.draw()
```

Now for SVG output in the browser.

```scala
import cats.effect.unsafe.implicits.global
import doodle.svg.*
import doodle.syntax.all.*

picture.drawWithFrame("theId")
```

This will produce output like that shown below.

@:doodle(quick-start-example, QuickStartExample.draw)
