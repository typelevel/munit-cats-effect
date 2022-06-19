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
import cats.effect.Resource
import cats.effect.SyncIO

import scala.concurrent.Promise
import scala.concurrent.duration._

class CatsEffectFunFixturesSpec extends CatsEffectSuite with CatsEffectFunFixtures {
  val latch: Promise[Unit] = Promise[Unit]()

  @volatile var completedFromTest: Option[Boolean] = None

  @volatile var completedFromResourceAcquire: Option[Boolean] = None
  @volatile var completedFromResourceRelease: Option[Boolean] = None

  val latchOnTeardown: SyncIO[FunFixture[String]] =
    ResourceFunFixture[String](
      Resource
        .make[IO, String](
          IO {
            completedFromResourceAcquire = Some(true)
            "test"
          }
        )(_ =>
          IO {
            completedFromResourceRelease = Some(true)
          }
        )
    )

  override def afterAll(): Unit = {
    // resource was created before setup
    assertEquals(completedFromResourceAcquire, Some(false))
    // resource was released after teardown
    assertEquals(completedFromResourceRelease, Some(true))
    // promise was completed first by the test
    assertEquals(completedFromTest, Some(true))
  }

  latchOnTeardown.test("teardown runs only after test completes") { _ =>
    // Simulate some work here, which increases the certainty that this test
    // will fail by design and not by lucky scheduling if the happens-before
    // relationship between the test and teardown is removed.
    IO.sleep(50.millis).flatTap { _ =>
      IO {
        completedFromTest = Some(latch.trySuccess(()))
      }
    }
  }
}
