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

import cats.Applicative

/** A [[chartreuse.Themeable]] value is one that can be set from a theme. There
  * are two cases:
  *
  *   - A [[chartreuse.Themeable.Default]] which can be overriden by the theme's
  *     value; and
  *   - A [[chartreuse.Themeable.Override]] which will override the theme's
  *     value.
  */
enum Themeable[A] {

  /** A default value that can be overriden by a theme */
  case Default(value: A)

  /** A value that overrides the theme's value */
  case Override(value: A)

  /** Convert the value within this [[chartreuse.Themeable]] while keeping the
    * status of default or override the same.
    */
  def map[B](f: A => B): Themeable[B] =
    this match {
      case Default(value)  => Default(f(value))
      case Override(value) => Override(f(value))
    }

  /** Convert this themeable value to a default */
  def toDefault: Themeable[A] =
    this match {
      case Default(_)      => this
      case Override(value) => Default(value)
    }

  /** Convert this themeable value to an override */
  def toOverride: Themeable[A] =
    this match {
      case Default(value) => Override(value)
      case Override(_)    => this
    }

  /** Combined this themeable value with the associated value from a theme. */
  def theme(themeValue: A): A =
    this match {
      case Default(_)      => themeValue
      case Override(value) => value
    }

  /** Get the value within this themeable container. */
  def extract: A =
    this match {
      case Default(value)  => value
      case Override(value) => value
    }
}
object Themeable {
  given Applicative[Themeable] with {
    def pure[A](value: A): Themeable[A] = Default(value)
    def ap[A, B](ff: Themeable[A => B])(fa: Themeable[A]): Themeable[B] =
      fa.map(a => ff.extract(a))
  }
}
