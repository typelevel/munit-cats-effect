/*
 * Copyright 2020 Typelevel
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

package munit

import cats.effect.IO

trait CatsEffectAssertions { self: Assertions =>

  /**
    * Asserts that an [[IO]] returns an expected value.
    *
    * The "returns" value (second argument) must have the same type or be a
    * subtype of the one "contained" inside the `IO` (first argument). For example:
    * {{{
    *   assertIO(IO(Option(1)), returns = Some(1)) // OK
    *   assertIO(IO(Some(1)), returns = Option(1)) // Error: Option[Int] is not a subtype of Some[Int]
    * }}}
    *
    * The "clue" value can be used to give extra information about the failure in case the
    * assertion fails.
    *
    * @param obtained the IO under testing
    * @param returns the expected value
    * @param clue a value that will be printed in case the assertions fails
    */
  def assertIO[A, B](
      obtained: IO[A],
      returns: B,
      clue: => Any = "values are not the same"
  )(implicit loc: Location, ev: B <:< A): IO[Unit] =
    obtained.flatMap(a => IO(assertEquals(a, returns, clue)))

}

object CatsEffectAssertions extends Assertions with CatsEffectAssertions
