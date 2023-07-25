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

trait ToData[F[_]] {
  def toData[A](data: F[A]): Data[A]
}
object ToData {
  given fromIterable: ToData[Iterable] with {
    def toData[A](data: Iterable[A]): Data[A] =
      Data(data)
  }

  given fromTraverse[F[_]](using traverse: Traverse[F]): ToData[F] with {
    def toData[A](data: F[A]): Data[A] =
      Data(data)(using traverse)
  }
}
