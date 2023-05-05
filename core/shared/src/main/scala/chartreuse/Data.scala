package chartreuse

import cats.Traverse

/** Represent a data set with elements of type `A` that can be rendered in a
  * graph.
  */
enum Data[A] {
  case fromIterable(data: Iterable[A])
  case fromTraverse[F[_], A](data: F[A])(using traverse: Traverse[F])
      extends Data[A]
}
