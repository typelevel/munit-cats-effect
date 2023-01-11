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

import cats.effect.IO
import cats.syntax.all._
import scala.concurrent.duration._
import cats.effect.SyncIO

class CatsEffectAssertionsSpec extends CatsEffectSuite {

  private val exception = new IllegalArgumentException("BOOM!")

  test("assertIO works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(2)

    assertIO(io, returns = 2)
  }
  test("assertIO works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(2)

    assertIO(io, returns = 3)
  }

  test("assertIOPredicate works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(2)

    assertIOPredicate(io)(_ < 3)
  }
  test("assertIOPredicate works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(2)

    assertIOPredicate(io)(_ > 3)
  }

  test("assertIO_ works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO.unit

    assertIO_(io)
  }
  test("assertIO_ works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO.raiseError[Unit](exception)

    assertIO_(io)
  }

  test("interceptIO works (successful assertion)") {
    val io = exception.raiseError[IO, Unit]

    interceptIO[IllegalArgumentException](io)
  }

  test("interceptIO works (failed assertion: different exception)".fail) {
    val io = IO(fail("BOOM!"))

    interceptIO[IllegalArgumentException](io)
  }

  test("interceptIO works (sucessful assertion on `FailException`)") {
    val io = IO(fail("BOOM!"))

    interceptIO[FailException](io)
  }

  test("interceptIO works (failed assertion: IO does not fail)".fail) {
    val io = IO(42)

    interceptIO[IllegalArgumentException](io)
  }

  test("interceptMessageIO works (successful assertion)") {
    val io = exception.raiseError[IO, Unit]

    interceptMessageIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("interceptMessageIO works (failed assertion: different exception)".fail) {
    val io = IO(fail("BOOM!"))

    interceptMessageIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("interceptMessageIO works (failed assertion: different message)".fail) {
    val io = IO(fail("BOOM!"))

    interceptMessageIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("interceptMessageIO works (failed assertion: IO does not fail)".fail) {
    val io = IO(42)

    interceptMessageIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("assertSyncIO works (successful assertion)") {
    val io = SyncIO(2)

    assertSyncIO(io, returns = 2)
  }

  test("assertSyncIO works (failed assertion)".fail) {
    val io = SyncIO(2)

    assertSyncIO(io, returns = 3)
  }

  test("assertSyncIOPredicate works (successful assertion)") {
    val io = SyncIO(2)

    assertSyncIOPredicate(io)(_ < 3)
  }

  test("assertSyncIOPredicate works (failed assertion)".fail) {
    val io = SyncIO(2)

    assertSyncIOPredicate(io)(_ > 3)
  }

  test("assertSyncIO_ works (successful assertion)") {
    val io = SyncIO.unit

    assertSyncIO_(io)
  }

  test("assertSyncIO_ works (failed assertion)".fail) {
    val io = SyncIO.raiseError[Unit](exception)

    assertSyncIO_(io)
  }

  test("interceptSyncIO works (successful assertion)") {
    val io = exception.raiseError[SyncIO, Unit]

    interceptSyncIO[IllegalArgumentException](io)
  }

  test("interceptSyncIO works (failed assertion: different exception)".fail) {
    val io = SyncIO(fail("BOOM!"))

    interceptSyncIO[IllegalArgumentException](io)
  }

  test("interceptSyncIO works (sucessful assertion on `FailException`)") {
    val io = SyncIO(fail("BOOM!"))

    interceptSyncIO[FailException](io)
  }

  test("interceptSyncIO works (failed assertion: SyncIO does not fail)".fail) {
    val io = SyncIO(42)

    interceptSyncIO[IllegalArgumentException](io)
  }

  test("interceptMessageSyncIO works (successful assertion)") {
    val io = exception.raiseError[SyncIO, Unit]

    interceptMessageSyncIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("interceptMessageSyncIO works (failed assertion: different exception)".fail) {
    val io = SyncIO(fail("BOOM!"))

    interceptMessageSyncIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("interceptMessageSyncIO works (failed assertion: different message)".fail) {
    val io = SyncIO(fail("BOOM!"))

    interceptMessageSyncIO[IllegalArgumentException]("BOOM!")(io)
  }

  test("interceptMessageSyncIO works (failed assertion: SyncIO does not fail)".fail) {
    val io = SyncIO(42)

    interceptMessageSyncIO[IllegalArgumentException]("BOOM!")(io)
  }

}
