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
import cats.effect.kernel.Deferred
import cats.syntax.all._

trait CatsEffectFunFixtures extends FunFixtures { self: CatsEffectSuite =>

  @deprecated("Use ResourceFunFixture", "2.0.0")
  object ResourceFixture {
    def apply[A](
        resource: Resource[IO, A]
    ): SyncIO[FunFixture[A]] = ResourceFunFixture(_ => resource)

    @deprecated("ResourceFunFixture(TestOptions => Resource[IO, T])", "2.0.0")
    def apply[T](
        resource: Resource[IO, T],
        setup: (TestOptions, T) => IO[Unit],
        teardown: T => IO[Unit]
    ): SyncIO[FunFixture[T]] = ResourceFunFixture { options =>
      resource.flatTap(t => Resource.make(setup(options, t))(_ => teardown(t)))
    }
  }

  object ResourceFunFixture {

    def apply[A](
        resource: Resource[IO, A]
    ): SyncIO[FunFixture[A]] = apply(_ => resource)

    def apply[A](resource: TestOptions => Resource[IO, A]): SyncIO[FunFixture[A]] =
      Deferred.in[SyncIO, IO, IO[Unit]].map { deferred =>
        FunFixture.async(
          setup = { testOptions =>
            resource(testOptions).allocated
              .flatMap { case (a, release) =>
                deferred.complete(release).as(a)
              }
              .unsafeToFuture()
          },
          teardown = { _ => deferred.get.flatten.unsafeToFuture() }
        )
      }

  }

  implicit class SyncIOFunFixtureOps[T](private val fixture: SyncIO[FunFixture[T]]) {
    def test(name: String)(
        body: T => Any
    )(implicit loc: Location): Unit = {
      fixture.unsafeRunSync().test(TestOptions(name))(body)
    }

    def test(options: TestOptions)(
        body: T => Any
    )(implicit loc: Location): Unit = {
      fixture.unsafeRunSync().test(options)(body)
    }
  }

}
