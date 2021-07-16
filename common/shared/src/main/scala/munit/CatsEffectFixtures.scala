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

import cats.effect.{IO, Resource}

trait CatsEffectFixtures extends CatsEffectFixturesPlatform { self: CatsEffectSuite =>

  import CatsEffectSuite.Deferred

  object ResourceSuiteLocalDeferredFixture {

    def apply[T](name: String, resource: Resource[IO, T]): Fixture[IO[T]] =
      new Fixture[IO[T]](name) {
        val value: Deferred[IO, (T, IO[Unit])] = Deferred.unsafe

        def apply(): IO[T] = value.get.map(_._1)

        override def beforeAll(): Unit = {
          val resourceEffect = resource.allocated.flatMap(value.complete)
          unsafeRunAndForget(resourceEffect)
        }

        override def afterAll(): Unit = {
          unsafeRunAndForget(value.get.flatMap(_._2))
        }
      }
  }

}
