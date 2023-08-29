# Creating Visualizations

Visualizations in Chartreuse are built from small components. The most important components are:

1. Layouts, which define a way to layout data. For example, a layout can be a line or curve, or a scatterplot.
2. Layers, which associate a layout with data, and metadata like a name and a scale.
3. Plots, which consist of one or more layers, and metadata like axes and title.

So creating a plot consists of creating each of the above components and combining them together. There are some shortcuts that simplify common cases.

Here's an example, taken from the [Quick Start](../quick-start.md). We start with some data, which is always needed for a visualization.

```scala mdoc:silent
import doodle.core.Point
import scala.util.Random

val data =
  List.fill(100)(Point(Random.nextGaussian(), Random.nextGaussian()))
```

Now we create a `Layout`, the first component described above.

```scala mdoc:silent
import chartreuse.*
import chartreuse.layout.Scatter

val layout = Scatter.default[Point]
```

The next component is a `Layer`, which associates the layout and the data.
The easiest way to create a `Layer` is with the `toLayer` method on `Layout`.

```scala mdoc:silent
val layer = layout.toLayer(data)
```

If we want to set metadata we call methods on `Layer`, such as `withLabel` to give a name to this layer, or `withScale` to use a scale different to the normal linear scale.

Now we can create a `Plot`. We can convert a `Layer` to a `Plot` using the `toPlot` method.

```scala mdoc:silent
val plot = layer.toPlot
```


## Builders and Constructors

Builder methods are any method starting with 

- `with`, such as `withLabel` on `Layer`; or
- `for`, such as `forThemeable`, on `Layout`.

These methods are used to set metadata. 
A method beginning with `with` will accept a new value for metadata. 
So `withLabel` will allow you to change the label of a `Layer` by passing it the new label.
The example below shows the effect of changing the label of the `Layer` we created above.

```scala mdoc
layer.label

layer.withLabel("The New Label").label
```

A method beginning with `for` accepts a function that is passed the current value of the metdata, and returns the updated value.
This allows you to update just the values of interest within a nested object.
The example below shows how we can modify just the `strokeColor` of a `Layout's` themeable values using `forThemeable`.

```scala mdoc:silent
import doodle.core.Color

layout
  .forThemeable(themeable => themeable.withStrokeColor(Themeable.Override(Some(Color.chartreuse))))
```

Builder methods *always* return a modified copy of the object they're called on, 
so it's always safe to call a builder method even if you used a component in another place.
This is a core part of Chartreuse's design philosophy, as described in [Core Concepts](../concepts.md)

In the example above we used `toLayer` and `toPlot` to convert types. These are convenience methods.
You can, for example, construct a `Plot` by calling its constructor but it's much simpler to type `.` and follow the auto-complete to turn a `Layer` into a `Plot`.

Most types have other convenience constructors on their companion object.
For example, you usually don't construct a `Scatter` layout by calling it's constructor. 
Instead you call the `Scatter.default` convenience constructor, and then perhaps change the few metadata settings using the builder methods.
