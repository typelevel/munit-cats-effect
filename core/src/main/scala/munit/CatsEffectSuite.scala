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

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, SyncIO}

import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration._
import munit.internal.NestingChecks.{checkNestingIO, checkNestingSyncIO}

abstract class CatsEffectSuite
    extends FunSuite
    with CatsEffectAssertions
    with CatsEffectFixtures
    with CatsEffectFunFixtures {

  @deprecated("Use munitIORuntime", "2.0.0")
  def munitIoRuntime: IORuntime = IORuntime.global
  implicit def munitIORuntime: IORuntime = munitIoRuntime: @nowarn

  override implicit def munitExecutionContext: ExecutionContext = munitIORuntime.compute

  def munitIOTimeout: Duration = 30.seconds

  // buys us a 1s window to cancel the IO, before munit cancels the Future
  override def munitTimeout: Duration = munitIOTimeout + 1.second

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms ++ List(munitIOTransform, munitSyncIOTransform)

  private val munitIOTransform: ValueTransform =
    new ValueTransform(
      "IO",
      { case e: IO[_] =>
        val unnestedIO = checkNestingIO(e)

        // TODO cleanup after CE 3.4.0 is released
        val fd = Some(munitIOTimeout).collect { case fd: FiniteDuration => fd }
        val timedIO = fd.fold(unnestedIO) { duration =>
          unnestedIO.timeoutTo(
            duration,
            IO.raiseError(new TimeoutException(s"test timed out after $duration"))
          )
        }

        timedIO.unsafeToFuture()
      }
    )

  private val munitSyncIOTransform: ValueTransform =
    new ValueTransform(
      "SyncIO",
      { case e: SyncIO[_] => Future(checkNestingSyncIO(e).unsafeRunSync())(munitExecutionContext) }
    )
}
