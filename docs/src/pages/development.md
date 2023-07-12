# Development

This section contains notes on the development of Chartreuse.


## Components

Composition is a core principle of Chartreuse.
This means visualizations are built from components, where each component represents part of a visualization or some computation needed to glue things together.
There are two main kinds of components:

* interfaces, which usually represent the visual parts of a visualization; and
* builders, which usually represent the computational glue between components.


## Interfaces

The majority of visualizations have a well defined visual structure. 
For example, most plots will have axes, a legend, and so on. 
The exact rendering of these elements is something the user will want to customize.
This means the visual functionality in Chartreuse can be described in terms of the operations the components support; it's interface.
So, for example, a `Layout` only needs to be able to draw itself.
The details of how the `Layout` draws itself is something the particular `Layout` can decide on.
There are an infinite number of possible `Layouts`, so we cannot enumerate them all, but we don't need to so long as the interface is consistent.
Interfaces are represented as Scala `traits`.


## Builders

Components that have extensive options, but are limited in the number of possible implementations, are represented as builders.
`Plot` is an example.
It acts a container for all the other elements of a visualization.
It's appearance is determined by it's components, but the user will probably wish to change the title, axes, and so on.
Chartreuse uses the builder pattern extensively to make it easier for users to construct these kinds of components,
overriding only the defaults they care about.

The builder pattern has the following details:

* Types are implemented as `final case classes` or `enums` as appropriate.
* Builder methods always return a copy of the object. There is no mutable state.
* Changing a parameter called `name` is done by a method called `withName`, which takes a new value for `name`.
* Changing a nested object called `name` is done by a method called `forName`, which takes a `name => name` function.
