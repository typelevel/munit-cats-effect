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

  test("assertEquals (for IO) works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(2)

    io assertEquals 2
  }
  test("assertEquals (for IO) works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(2)

    io assertEquals 3
  }

  test("assert (for IO) works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(true)

    io.assert
  }
  test("assert (for IO) works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(false)

    io.assert
  }

  test("intercept (for IO) works (successful assertion)") {
    val io = (new IllegalArgumentException("BOOM!")).raiseError[IO, Unit]

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
    val io = (new IllegalArgumentException("BOOM!")).raiseError[IO, Unit]

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

  test("intercept (for SyncIO) works (successful assertion)") {
    val io = (new IllegalArgumentException("BOOM!")).raiseError[SyncIO, Unit]

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
    val io = (new IllegalArgumentException("BOOM!")).raiseError[SyncIO, Unit]

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
