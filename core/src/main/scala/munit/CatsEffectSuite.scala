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
import cats.effect.{IO, ResourceIO, SyncIO}

import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration.*
import munit.internal.NestingChecks.{checkNestingIO, checkNestingSyncIO, checkNestingResourceIO}

abstract class CatsEffectSuite
    extends FunSuite
    with CatsEffectAssertions
    with CatsEffectFixtures
    with CatsEffectFunFixtures {

  @deprecated("Use munitIORuntime", "2.0.0")
  def munitIoRuntime: IORuntime = IORuntime.global
  implicit def munitIORuntime: IORuntime = munitIoRuntime: @nowarn

  override implicit def munitExecutionContext: ExecutionContext = munitIORuntime.compute

  /** The timeout for [[cats.effect.IO IO]]-based tests. When it expires it will gracefully cancel
    * the fiber running the test and invoke any finalizers before ultimately failing the test.
    *
    * Note that the fiber may still hang while running finalizers or even be uncancelable. In this
    * case the [[munitTimeout]] will take effect, with the caveat that the hanging fiber will be
    * leaked.
    */
  def munitIOTimeout: Duration = 30.seconds

  /** The overall timeout applicable to all tests in the suite, including those written in terms of
    * [[scala.concurrent.Future Future]] or synchronous code. This is implemented by the MUnit
    * framework itself.
    *
    * When this timeout expires, the suite will immediately fail the test and proceed without
    * waiting for its cancelation or even attempting to cancel it. For that reason it is recommended
    * to set this to a greater value than [[munitIOTimeout]], which performs graceful cancelation of
    * [[cats.effect.IO IO]]-based tests. The default grace period for cancelation is 1 second.
    */
  @deprecatedOverriding(
    "Override munitIOTimeout instead. This method will not be finalized, but the only reason to" +
      "override it would be to adjust the grace period for fiber cancelation (default 1 second).",
    "2.0.0"
  )
  override def munitTimeout: Duration = munitIOTimeout + 1.second

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms ++ List(
      munitIOTransform,
      munitResourceIOTransform,
      munitSyncIOTransform
    )

  private val munitIOTransform: ValueTransform =
    new ValueTransform(
      "IO",
      { case e: IO[_] =>
        val unnestedIO = checkNestingIO(e)

        val timedIO = unnestedIO.timeoutTo(
          munitIOTimeout,
          IO.raiseError(new TimeoutException(s"test timed out after $munitIOTimeout"))
        )

        timedIO.unsafeToFuture()
      }
    )

  private val munitResourceIOTransform: ValueTransform =
    new ValueTransform(
      "ResourceIO",
      { case e: ResourceIO[_] =>
        val unnestedResourceIO = checkNestingResourceIO(e)

        val timedIO = unnestedResourceIO.use_.timeoutTo(
          munitIOTimeout,
          IO.raiseError(new TimeoutException(s"test timed out after $munitIOTimeout"))
        )

        timedIO.unsafeToFuture()
      }
    )

  private val munitSyncIOTransform: ValueTransform =
    new ValueTransform(
      "SyncIO",
      { case e: SyncIO[_] => Future(checkNestingSyncIO(e).unsafeRunSync())(munitExecutionContext) }
    )
}
