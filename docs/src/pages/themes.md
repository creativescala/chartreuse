# Theming Plots

Theming controls colors, fills, and other aspects of the visual appearance of plots.
There are two concepts in Chartreuse's theme system:

- themes, which specify collections of values that control the visual appearance of plots; and
- @:api(chartreuse.Themeable) values, which allow the user to override the theme values with settings of their own choice.


## Themes

Themes are found within the `chartreuse.theme` package.
Most users will apply a theme to a whole plot, using @:api(chartreuse.theme.PlotTheme).
There are a small number of predefined `PlotThemes` available in Chartreuse:

- @:api(chartreuse.theme.PlotTheme.default), the default theme; and
- the themes defined within @:api(chartreuse.theme.PlotTheme.base16): `defaultLight`, `defaultDark`, and so on.


## Themeable Values

Where settings in Chartreuse can be themed they are represented as @:api(chartreuse.Themeable) values.
For example, @:api(chartreuse.layout.Curve) contains a @:api(chartreuse.theme.LayoutTheme) which in turn contains `Themeable` values for `strokeColor`, `fillColor`, and so on.

@:api(chartreuse.Themeable) values allow theme choices to be overriden. A `Themeable` value specifies either a @:api(chartreuse.Themeable.Default), which will be overriden by the theme's value, if there is one, or a  @:api(chartreuse.Themeable.Override), which will override the theme's value.

