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
package catseffect

import cats.effect.IO
import cats.effect.Resource

object ResourceFixture {

  final class FixtureNotInstantiatedException(name: String)
      extends Exception(
        s"The fixture `$name` was not instantiated. Override `munitFixtures` and include a reference to this fixture."
      )

  def testLocal[A](name: String, resource: Resource[IO, A]): IOFixture[A] =
    new IOFixture[A](name) {
      @volatile var value: Option[(A, IO[Unit])] = None

      def apply() = value match {
        case Some(v) => v._1
        case None    => throw new FixtureNotInstantiatedException(name)
      }

      override def beforeEach(context: BeforeEach) = resource.allocated.flatMap { value =>
        IO(this.value = Some(value))
      }

      override def afterEach(context: AfterEach) = value.fold(IO.unit)(_._2)
    }

  def suiteLocal[A](name: String, resource: Resource[IO, A]): IOFixture[A] =
    new IOFixture[A](name) {
      @volatile var value: Option[(A, IO[Unit])] = None

      def apply() = value match {
        case Some(v) => v._1
        case None    => throw new FixtureNotInstantiatedException(name)
      }

      override def beforeAll() = resource.allocated.flatMap { value =>
        IO(this.value = Some(value))
      }

      override def afterAll() = value.fold(IO.unit)(_._2)
    }

}

abstract class IOFixture[A](name: String) extends AnyFixture[A](name: String) {

  override def beforeAll(): IO[Unit] = IO.unit

  override def beforeEach(context: BeforeEach): IO[Unit] = IO.unit

  override def afterEach(context: AfterEach): IO[Unit] = IO.unit

  override def afterAll(): IO[Unit] = IO.unit

}
