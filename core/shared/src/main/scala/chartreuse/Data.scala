/*
 * Copyright 2023 Creative Scala
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
import doodle.core.BoundingBox
import doodle.core.Point

/** Represent a data set with elements of type `A` that can be drawn in a graph.
  */
enum Data[+A] {
  case FromIterable(data: Iterable[A])
  case FromTraverse[F[_], A](data: F[A], traverse: Traverse[F]) extends Data[A]

  def boundingBox(toPoint: A => Point): BoundingBox =
    this match {
      case FromIterable(data) =>
        if data.isEmpty then BoundingBox.empty
        else {
          val pt = toPoint(data.head)
          val bb = BoundingBox(pt.x, pt.y, pt.x, pt.y)
          data.tail.foldLeft(bb) { (bb, a) => bb.enclose(toPoint(a)) }
        }
      case FromTraverse(data, traverse) =>
        traverse.reduceLeftToOption(data) { (a: A) =>
          val pt = toPoint(a)
          BoundingBox(pt.x, pt.y, pt.x, pt.y)
        }((bb, a) => bb.enclose(toPoint(a))) match {
          case Some(value) => value
          case None        => BoundingBox.empty
        }

    }

  def foldLeft[B](z: B)(f: (B, A) => B): B =
    this match {
      case FromIterable(data)           => data.foldLeft(z)(f)
      case FromTraverse(data, traverse) => traverse.foldLeft(data, z)(f)
    }
}
object Data {
  def apply[A](data: Iterable[A]): Data[A] =
    FromIterable(data)

  def apply[F[_], A](data: F[A])(using traverse: Traverse[F]): Data[A] =
    FromTraverse(data, traverse)

}
