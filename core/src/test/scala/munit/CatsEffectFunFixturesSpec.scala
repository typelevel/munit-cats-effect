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
import cats.effect.Ref
import cats.effect.Resource
import cats.effect.SyncIO

import scala.concurrent.duration._

class CatsEffectFunFixturesSpec extends CatsEffectSuite with CatsEffectFunFixtures {
  val counter: Ref[IO, Int] = Ref.unsafe[IO, Int](0)

  @volatile var acquired: Option[Int] = None
  @volatile var tested: Option[Int] = None
  @volatile var released: Option[Int] = None

  val countingFixture: SyncIO[FunFixture[Unit]] =
    ResourceFunFixture[Unit](
      Resource
        .make[IO, Unit](
          counter.getAndUpdate(_ + 1).flatMap(i => IO(this.acquired = Some(i)))
        )(_ => counter.getAndUpdate(_ + 1).flatMap(i => IO(this.released = Some(i))))
    )

  override def afterAll(): Unit = {
    // resource was acquired first
    assertEquals(acquired, Some(0))
    // then the test was run
    assertEquals(tested, Some(1))
    // then it was released
    assertEquals(released, Some(2))
  }

  countingFixture.test("fixture runs before/after test") { _ =>
    // Simulate some work here, which increases the certainty that this test
    // will fail by design and not by lucky scheduling if the happens-before
    // relationship between the test and teardown is removed.
    IO.sleep(50.millis) *> counter.getAndUpdate(_ + 1).flatMap(i => IO(this.tested = Some(i)))
  }
}
