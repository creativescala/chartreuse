# Annotations

Annotations allow you to highlight particular parts of the data.
There are two concepts in Chartreuse's annotation system:

- @:api(chartreuse.component.AnnotationType), which defines various types of annotations, each with its specific characteristics; and
- @:api(chartreuse.component.AnnotationPosition), which represents a specific positioning for annotations along with an arrow angle and placement logic.


## Types of annotations

Annotation types are found within the `chartreuse.component` package.
Currently, there are four different types of annotations:

- @:api(chartreuse.component.AnnotationType.Circle), a circle with user-specified radius;
- @:api(chartreuse.component.AnnotationType.CircleWithText), text along with a circle with user-specified radius;
- @:api(chartreuse.component.AnnotationType.Text), plain text; and
- @:api(chartreuse.component.AnnotationType.TextWithBox), text in a box.

An example of creating an `AnnotationType`:

```scala
val annotationType = AnnotationType.CircleWithText(15, "Something interesting happened here")
```


## Positioning the annotations

Each annotation in Chartreuse uses `AnnotationPosition` for positioning.

`AnnotationPosition` uses @:api(doodle.core.Landmark), @:api(doodle.core.Angle) and (@:api(doodle.core.Point), Double) => @:api(doodle.core.Point) to place an annotation:

- The `Landmark` is used as the reference point for annotation placement;
- The `Angle` is used to properly rotate the arrow indicating the annotation (More on the arrows in the following block); and
- The `(Point, Double) => Point` is a function that takes a base point and a margin offset, and returns the final annotation point. For example, `(pt, offset) => Point(pt.x - offset, pt.y + offset)` will place the annotation diagonally above and to the left of the point of interest.

Most users will use predefined annotation positioning options for ease of use (e.g. `AnnotationPosition.topLeft`).
But for more precise positioning it is possible to define all the parameters manually.


## Creating annotations

To create an annotation, it's enough to use the `apply` method of `Annotation`, which takes the point of interest and the `AnnotationType`:

```scala
Annotation(
  Point(1950, 81651),
  AnnotationType.Text("Rapid growth began here")
)
```

Annotations can be added to a plot with the `addAnnotation` method:

```scala
val annotatedPlot = plot
      .addAnnotation(annotation)
```

By default, each annotation is placed in the center of the point of interest:

@:doodle(annotation-default, Annotations.drawDefault)

But the position can be adjusted using the `withAnnotationPosition` method:

```scala
Annotation(
  Point(1950, 81651),
  AnnotationType.Text("Rapid growth began here")
)
  .withAnnotationPosition(AnnotationPosition.bottomRight)
```

Which will produce

@:doodle(annotation-with-positioning, Annotations.drawWithPositioning)

In addition, it's possible to draw an arrow between the text and the point of interest. `withArrow` method is used for this.
Arrows are placed automatically:

```scala
Annotation(
  Point(1950, 81651),
  AnnotationType.Text("Rapid growth began here")
)
  .withAnnotationPosition(AnnotationPosition.bottomRight)
  .withArrow()
```

Which will produce

@:doodle(annotation-with-positioning-and-arrow, Annotations.drawWithPositioningAndArrow)
