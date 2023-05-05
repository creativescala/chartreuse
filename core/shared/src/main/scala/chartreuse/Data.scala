/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
