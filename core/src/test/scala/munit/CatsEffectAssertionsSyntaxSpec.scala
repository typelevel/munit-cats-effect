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

class CatsEffectAssertionsSyntaxSpec extends CatsEffectSuite {

  private val exception = new IllegalArgumentException("BOOM!")

  test("assertEquals (for IO) works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(2)

    io assertEquals 2
  }

  test("assertEquals (for IO) works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(2)

    io assertEquals 3
  }

  test("assert predicate (for IO) works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(2)

    io assert (_ < 3)
  }

  test("assert predicate (for IO) works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(2)

    io assert (_ > 3)
  }

  test("assert (for IO[Boolean]) works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(true)

    io.assert
  }

  test("assert (for IO[Boolean]) works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(false)

    io.assert
  }

  test("assert (for IO[Unit]) works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO.unit

    io.assert
  }

  test("assert (for IO[Unit]) works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO.raiseError[Unit](exception)

    io.assert
  }

  test("mapOrFail (for IO) works (successful mapping)") {
    val io = IO.sleep(2.millis) *> IO.some(42)

    io.mapOrFail { case Some(obtained) if obtained % 2 == 0 => obtained / 2 }
      .assertEquals(21)
  }

  test("mapOrFail (for IO) works (failed mapping)".fail) {
    val io = IO.sleep(2.millis) *> IO.some(42)

    io.mapOrFail { case Some(obtained) if obtained % 2 == 1 => () }
  }

  test("mapOrFail (for IO) works (failed mapping with clue)".fail) {
    val io = IO.sleep(2.millis) *> IO.some(42)

    // This test simply shows what the syntax looks like when a `clue` is provided.
    io.mapOrFail(
      { case Some(obtained) if obtained % 2 == 1 => () },
      "the clue goes here"
    )
  }

  test("intercept (for IO) works (successful assertion)") {
    val io = exception.raiseError[IO, Unit]

    io.intercept[IllegalArgumentException]
  }

  test("intercept (for IO) works (failed assertion: different exception)".fail) {
    val io = IO(fail("BOOM!"))

    io.intercept[IllegalArgumentException]
  }

  test("intercept (for IO) works (sucessful assertion on `FailException`)") {
    val io = IO(fail("BOOM!"))

    io.intercept[FailException]
  }

  test("intercept (for IO) works (failed assertion: IO does not fail)".fail) {
    val io = IO(42)

    io.intercept[IllegalArgumentException]
  }

  test("interceptMessage (for IO) works (successful assertion)") {
    val io = exception.raiseError[IO, Unit]

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("interceptMessage (for IO) works (failed assertion: different exception)".fail) {
    val io = IO(fail("BOOM!"))

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("interceptMessage (for IO) works (failed assertion: different message)".fail) {
    val io = IO(fail("BOOM!"))

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("interceptMessage (for IO) works (failed assertion: IO does not fail)".fail) {
    val io = IO(42)

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("assertEquals (for SyncIO) works (successful assertion)") {
    val io = SyncIO(2)

    io assertEquals 2
  }

  test("assertEquals (for SyncIO) works (failed assertion)".fail) {
    val io = SyncIO(2)

    io assertEquals 3
  }

  test("assert predicate (for SyncIO) works (successful assertion)") {
    val io = SyncIO(2)

    io assert (_ < 3)
  }

  test("assert predicate (for SyncIO) works (failed assertion)".fail) {
    val io = SyncIO(2)

    io assert (_ > 3)
  }

  test("assert (for SyncIO[Unit]) works (successful assertion)") {
    val io = SyncIO.unit

    io.assert
  }

  test("assert (for SyncIO[Unit]) works (failed assertion)".fail) {
    val io = SyncIO.raiseError[Unit](exception)

    io.assert
  }

  test("mapOrFail (for SyncIO) works (successful mapping)") {
    val io = SyncIO.pure(Some(42))

    io.mapOrFail { case Some(obtained) if obtained % 2 == 0 => obtained / 2 }
      .assertEquals(21)
  }

  test("mapOrFail (for SyncIO) works (failed mapping)".fail) {
    val io = SyncIO.pure(Some(42))

    io.mapOrFail { case Some(obtained) if obtained % 2 == 1 => () }
  }

  test("mapOrFail (for SyncIO) works (failed mapping with clue)".fail) {
    val io = SyncIO.pure(Some(42))

    // This test simply shows what the syntax looks like when a `clue` is provided.
    io.mapOrFail(
      { case Some(obtained) if obtained % 2 == 1 => () },
      "the clue goes here"
    )
  }

  test("intercept (for SyncIO) works (successful assertion)") {
    val io = exception.raiseError[SyncIO, Unit]

    io.intercept[IllegalArgumentException]
  }

  test("intercept (for SyncIO) works (failed assertion: different exception)".fail) {
    val io = SyncIO(fail("BOOM!"))

    io.intercept[IllegalArgumentException]
  }

  test("intercept (for SyncIO) works (sucessful assertion on `FailException`)") {
    val io = SyncIO(fail("BOOM!"))

    io.intercept[FailException]
  }

  test("intercept (for SyncIO) works (failed assertion: SyncIO does not fail)".fail) {
    val io = SyncIO(42)

    io.intercept[IllegalArgumentException]
  }

  test("interceptMessage (for SyncIO) works (successful assertion)") {
    val io = exception.raiseError[SyncIO, Unit]

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("interceptMessage (for SyncIO) works (failed assertion: different exception)".fail) {
    val io = SyncIO(fail("BOOM!"))

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("interceptMessage (for SyncIO) works (failed assertion: different message)".fail) {
    val io = SyncIO(fail("BOOM!"))

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

  test("interceptMessage (for SyncIO) works (failed assertion: SyncIO does not fail)".fail) {
    val io = SyncIO(42)

    io.interceptMessage[IllegalArgumentException]("BOOM!")
  }

}
