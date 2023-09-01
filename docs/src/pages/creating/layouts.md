# Layouts

A @:api(chartreuse.Layout) determines how data is mapped to graphical output.
For example, a @:api(chartreuse.layout.Scatter) represents each data point as a small graphical mark known as a @:api(chartreuse.layout.Glyph), and is used to create [scatter plots][scatter-plots] and [bubble plots][bubble-plot].

## Line

The @:api(chartreuse.layout.Line) layout draws a straight line between each data point. The data is drawn in the order it is received, so you should probably ensure the data is sorted before it is drawn.


## Curve

The @:api(chartreuse.layout.Curve) layout interpolates a smooth curve that passes through each data point. Like @:api(chartreuse.layout.Line), the data is drawn in the order it is received, so you should probably ensure the data is sorted before it is drawn.

The curve is produced using the [Catmul-Rom method][catmul-rom], which is controlled by a `tension` parameter that determines how closely the interpolated curve follows the data.


## Scatter

The @:api(chartreuse.layout.Scatter) layout draws each data point as a small graphical mark known as a @:api(chartreuse.layout.Glyph).


[scatter-plot]: https://en.wikipedia.org/wiki/Scatter_plot
[bubble-plot]: https://en.wikipedia.org/wiki/Bubble_chart
[catmul-rom]: https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Catmull%E2%80%93Rom_spline
