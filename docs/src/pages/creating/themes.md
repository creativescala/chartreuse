# Theming Plots

Theming controls colors, fills, and other aspects of the visual appearance of plots.
There are two concepts in Chartreuse's theme system:

- themes, which specify collections of values that control the visual appearance of plots; and
- @:api(chartreuse.Themeable) values, which allow the user to override the theme values with settings of their own choice.


## Themes

Themes are found within the `chartreuse.theme` package.
Most users will apply a theme to a whole plot, using a @:api(chartreuse.theme.PlotTheme).
There are a small number of predefined `PlotThemes` available in Chartreuse:

- @:api(chartreuse.theme.PlotTheme.default), the default theme;
- the themes defined within @:api(chartreuse.theme.PlotTheme.base16): `defaultLight`, `defaultDark`, and so on.
- @:api(chartreuse.theme.PlotTheme.fiveThirtyEight);
- @:api(chartreuse.theme.PlotTheme.bmh); and
- @:api(chartreuse.theme.PlotTheme.ggplot).

To apply a theme to a `Plot`, pass it to the `draw` method. For example, instead of calling

```scala
aPlot.draw(640, 480)
```

call

```scala
aPlot.draw(640, 480, PlotTheme.base16.defaultLight)
```


## Themeable Values

Where a value in Chartreuse can be themed it is represented as a @:api(chartreuse.Themeable) value.
For example, @:api(chartreuse.layout.Curve) contains a @:api(chartreuse.theme.LayoutTheme) which in turn contains `Themeable` values for `strokeColor`, `fillColor`, and so on.

A `Themeable` value specifies either a @:api(chartreuse.Themeable.Default), which will be overriden by the theme's value, if there is one, or an @:api(chartreuse.Themeable.Override), which will override the theme's value. So, for example, if a `Themeable` `strokeColor` is `Themeable.Default(Color.red)`, this value will be overriden by the theme's `strokeColor`. However, if the value is `Themeable.Override(Color.red)` it will override the theme.

When changing `Themeable` values it's usually the case you want to override the theme. Here's an example creating a @:api(chartreuse.layout.Line) layout and overriding the theme with custom stroke color and width.

```scala
Line
  .default[Point]
  .forThemeable(theme =>
    theme
      .withStrokeColor(Themeable.Override(Some(Color.darkBlue)))
      .withStrokeWidth(Themeable.Override(3.0))
  )
```


## Theme Gallery

Here are examples of some of the available themes.

### Default Theme
@:doodle(theme-default, PlotExample.drawDefault)

### BMH Theme
@:doodle(theme-bmh, PlotExample.drawBmh)

### FiveThirtyEight Theme
@:doodle(theme-fivethirtyeight, PlotExample.drawFiveThirtyEight)

### Base16 Default Light Theme
@:doodle(theme-base16-default-light, PlotExample.drawBase16DefaultLight)

### Base16 Ocean Theme
@:doodle(theme-base16-ocean, PlotExample.drawBase16Ocean)
