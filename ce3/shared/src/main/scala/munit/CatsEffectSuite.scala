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

import scala.concurrent.{ExecutionContext, Future}

abstract class CatsEffectSuite
    extends FunSuite
    with CatsEffectSuitePlatform
    with CatsEffectAssertions
    with CatsEffectFixtures
    with CatsEffectFunFixtures {

  implicit def munitIoRuntime: IORuntime = IORuntime.global

  override implicit val munitExecutionContext: ExecutionContext = munitIoRuntime.compute

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms ++ List(munitIOTransform, munitSyncIOTransform)

  private[munit] val munitIOTransform: ValueTransform =
    new ValueTransform(
      "IO",
      { case e: IO[_] => e.unsafeToFuture() }
    )

  private val munitSyncIOTransform: ValueTransform =
    new ValueTransform(
      "SyncIO",
      { case e: SyncIO[_] => Future(e.unsafeRunSync())(munitExecutionContext) }
    )

}

object CatsEffectSuite {
  private[munit] type Deferred[F[_], A] = cats.effect.kernel.Deferred[F, A]
  private[munit] val Deferred = cats.effect.kernel.Deferred
}
