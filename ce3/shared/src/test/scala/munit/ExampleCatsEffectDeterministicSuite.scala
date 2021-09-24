/*
 * Copyright 2021 Typelevel
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

import cats.effect.std.Dispatcher
import cats.effect.testkit.TestControl
import cats.effect.{IO, SyncIO}

import scala.concurrent.CancellationException
import scala.concurrent.duration._

class ExampleCatsEffectDeterministicSuite extends CatsEffectDeterministicSuite {

  private val simple = IO.unit

  private val longSleeps = for {
    first <- IO.monotonic
    _ <- IO.sleep(1.hour)
    second <- IO.monotonic
    _ <- IO.race(IO.sleep(1.day), IO.sleep(1.day + 1.nanosecond))
    third <- IO.monotonic
  } yield (first.toCoarsest, second.toCoarsest, third.toCoarsest)

  private val deadlock: IO[Unit] = IO.never

  private class ExampleException() extends RuntimeException

  test("tests should pass for simple IO") {
    assertIO_(simple)
  }

  test("tests should pass for an IO with long sleeps") {
    assertIO(longSleeps, (0.nanoseconds, 1.hour, 25.hours))
  }

  test("tests should fail because of a deadlock".fail) {
    // that interception doesn't work because exception comes from the runtime, not from IO itself
    interceptIO[TestControl.NonTerminationException](deadlock)
  }

  test("tests should intercept exception for an IO which produces an error") {
    interceptIO[ExampleException](IO.raiseError[Unit](new ExampleException()))
  }

  test("tests should fail because of an IO which self-cancels".fail) {
    // that interception doesn't work because exception comes from the runtime, not from IO itself
    interceptIO[CancellationException](IO.canceled)
  }

  test("tests can return IO[Unit] with assertions expressed via a map") {
    IO(42).map(it => assertEquals(it, 42))
  }

  test("alternatively, assertions can be written via assertIO") {
    assertIO(IO(42), 42)
  }

  test("or via assertEquals syntax") {
    IO(42).assertEquals(42)
  }

  test("or via plain assert syntax on IO[Boolean]") {
    IO(true).assert
  }

  test("SyncIO works too") {
    SyncIO(42).assertEquals(42)
  }

  private val dispatcher = ResourceFixture(Dispatcher[IO])

  dispatcher.test("resources can be lifted to munit fixtures") { dsp =>
    dsp.unsafeRunAndForget(IO(42))
  }

  dispatcher.test("resources can be lifted to munit fixtures again") { dsp =>
    dsp.unsafeRunAndForget(IO(42))
  }
}
