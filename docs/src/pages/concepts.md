# Core Concepts

To understand Chartreuse it helps to understand the core concepts behind the library.


## Visualizations are Created by Composition

Chartreuse is built around the idea of *composition*, which means constructing bigger things out of smaller things. In the case of Chartreuse, this means a `Plot` is constructed from `Layers`, a `Layer` combines `Data` and a `Layout`, and so on. Each component is reusable in different situations. So you can add the same `Data` to different `Layers` in the same `Plot` if you want.


## Cross-Platform

Chartreuse builds on [Doodle][doodle], and runs everywhere Doodle runs. This means you can create plots on the JVM and in the web browser, simply by using a different Doodle backend.


## Ease-of-Use

Chartreuse is designed to be easy to use, while keeping to the principles above. It should be straightforward to create basic visualizations, and possible to create complex ones.


[doodle]: https://creativescala.org/doodle
