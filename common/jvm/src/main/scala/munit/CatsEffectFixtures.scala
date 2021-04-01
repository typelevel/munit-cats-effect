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

trait CatsEffectFixtures { self: CatsEffectSuite =>

  object ResourceSuiteLocalFixture {

    final class FixtureNotInstantiatedException(name: String) extends Exception(s"The fixture `$name` was not instantiated. Override `munitFixtures` and include a reference to this fixture.")

    def apply[T](name: String, resource: Resource[IO, T]): Fixture[T] =
      new Fixture[T](name) {
        var value: Option[(T, IO[Unit])] = None

        def apply(): T = value match {
          case Some(v) => v._1
          case None => throw new FixtureNotInstantiatedException(name)
        }

        override def beforeAll(): Unit = {
          val resourceEffect = resource.allocated
          val (t, cleanup) = resourceEffect.unsafeRunSync()
          value = Some(t -> cleanup)
        }

        override def afterAll(): Unit = {
          value.get._2.unsafeRunSync()
        }
      }
  }

}
