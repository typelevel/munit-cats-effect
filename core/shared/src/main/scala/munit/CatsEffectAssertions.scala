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
import cats.syntax.eq._
import scala.reflect.ClassTag
import scala.util.control.NonFatal

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

  /**
    * Intercepts a `Throwable` being thrown inside the provided `IO`.
    *
    * @example
    * {{{
    *   val io = IO.raiseError[Unit](MyException("BOOM!"))
    *
    *   interceptIO[MyException](io)
    * }}}
    *
    * or
    *
    * {{{
    *   interceptIO[MyException] {
    *       IO.raiseError[Unit](MyException("BOOM!"))
    *   }
    * }}}
    */
  def interceptIO[T <: Throwable](io: IO[Any])(implicit T: ClassTag[T], loc: Location): IO[T] =
    io.attempt.flatMap[T](runInterceptIO(None))

  /**
    * Intercepts a `Throwable` with a certain message being thrown inside the provided `IO`.
    *
    * @example
    * {{{
    *   val io = IO.raiseError[Unit](MyException("BOOM!"))
    *
    *   interceptIO[MyException]("BOOM!")(io)
    * }}}
    *
    * or
    *
    * {{{
    *   interceptIO[MyException] {
    *       IO.raiseError[Unit](MyException("BOOM!"))
    *   }
    * }}}
    */
  def interceptMessageIO[T <: Throwable](
      expectedExceptionMessage: String
  )(io: IO[Any])(implicit T: ClassTag[T], loc: Location): IO[T] =
    io.attempt.flatMap[T](runInterceptIO(Some(expectedExceptionMessage)))

  /**
    * Copied from `munit.Assertions` and adapted to return `IO[T]` instead of `T`.
    */
  private def runInterceptIO[T <: Throwable](
      expectedExceptionMessage: Option[String]
  )(implicit T: ClassTag[T], loc: Location): Either[Throwable, Any] => IO[T] = {
    case Right(value) =>
      IO {
        fail(
          s"expected exception of type '${T.runtimeClass.getName()}' but body evaluated successfully",
          clues(value)
        )
      }
    case Left(e: FailException) if !T.runtimeClass.isAssignableFrom(e.getClass()) =>
      IO.raiseError[T](e)
    case Left(NonFatal(e: T))
        if expectedExceptionMessage.map(_ === e.getMessage()).getOrElse(true) =>
      IO(e)
    case Left(NonFatal(e: T)) =>
      IO.raiseError[T] {
        val obtained = e.getClass().getName()

        new FailException(
          s"intercept failed, exception '$obtained' had message '${e.getMessage}', " +
            s"which was different from expected message '${expectedExceptionMessage.get}'",
          cause = e,
          isStackTracesEnabled = false,
          location = loc
        )
      }
    case Left(NonFatal(e)) =>
      IO.raiseError[T] {
        val obtained = e.getClass().getName()
        val expected = T.runtimeClass.getName()

        new FailException(
          s"intercept failed, exception '$obtained' is not a subtype of '$expected",
          cause = e,
          isStackTracesEnabled = false,
          location = loc
        )
      }
  }

}

object CatsEffectAssertions extends Assertions with CatsEffectAssertions
