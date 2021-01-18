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

import cats.effect.{ContextShift, IO, SyncIO, Timer}

import scala.concurrent.{Future, ExecutionContext}

abstract class CatsEffectSuite
    extends FunSuite
    with CatsEffectAssertions
    with CatsEffectFunFixtures {

  implicit def munitContextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  implicit def munitTimer: Timer[IO] =
    IO.timer(ExecutionContext.global)

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms ++ List(munitIOTransform, munitSyncIOTransform)

  private val munitIOTransform: ValueTransform =
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
