# Quick Start

This section shows how to get started using Chartreuse. 
Refer to other sections for more details on how the library works.


## Using Chartreuse

To use Chartreuse in your project, add the following to your `build.sbt`:

```scala
libraryDependencies += "org.creativescala" %% "chartreuse" % "@VERSION@"
```

Then, in your code, import the library.

```scala
import chartreuse.{*, given}
```
```scala mdoc:invisible
// Scala complains that the `given` import is unused
import chartreuse.*
```


## Creating Data

You need some data to create a visualization.
You presumably have some real data but for this demonstration we'll create some simple fake data.

```scala mdoc:silent
import doodle.core.Point
import scala.util.Random

val data =
  List.fill(100)(Point(Random.nextGaussian(), Random.nextGaussian()))
```

Chartreuse can work with just about any collection class, so it probably doesn't matter how you've stored your data.


## Creating a Plot

Now we have data we can create a plot.
The simplest way to create a plot is to start with the `Layout`,
which determines how the data will be drawn in the visualization.
We'll create a simple scatter plot from our data.
This uses the `Scatter` layout.

```scala mdoc:silent
import chartreuse.layout.Scatter

val layout = Scatter.default[Point]
```

We need to add our data to the a `Layout` to create a `Plot` that we can draw.
Once we have a `Plot` we can set the title and other properties.

```scala mdoc:silent
val plot = layout
  .toPlot(data)
  .withPlotTitle("Our Amazing Plot")
  .withXTitle("Awesomeness")
  .withYTitle("Marvellousness")
```

We can convert a `Plot` to a Doodle `Picture` using the `draw` method, to which we pass the size of the output.

```scala
val picture = plot.draw(640, 480)
```

Then we can render the `picture` in the usual way for Doodle, which depends on the backend in use. On the JVM we just call the `draw` method. On the JS backend we call `drawWithFrame` passing in the id of the DOM element (a `String`) where we should draw it.

Here are complete examples. Firstly for the JVM.

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
