# munit-cats-effect ![Continuous Integration](https://github.com/typelevel/munit-cats-effect/workflows/Continuous%20Integration/badge.svg) [![Maven Central](https://img.shields.io/github/v/release/typelevel/munit-cats-effect?display_name=tag)](https://img.shields.io/github/v/release/typelevel/munit-cats-effect?display_name=tag)

Integration library for [MUnit](https://scalameta.org/munit/) and [cats-effect](https://github.com/typelevel/cats-effect/).

## Binaries

### For versions `2.0.0-M1` and above:

```scala
libraryDependencies += "org.typelevel" %%% "munit-cats-effect" % version % "test"
```

> Please note that only Cats Effect 3 is supported for versions `2.0.0-M1` and above.

### For versions `1.0.7` and below:

Cats Effect 2 integration is provided via:

```scala
libraryDependencies += "org.typelevel" %%% "munit-cats-effect-2" % version % "test"
```

Cats Effect 3 integration is provided via:

```scala
libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % version % "test"
```

Builds are available for Scala 2.12, 2.13, and 3 for both the JVM and Scala.js.

## Getting Started

The `munit.CatsEffectSuite` trait provides the ability to write tests that return `IO` and `SyncIO` values without needing to call any unsafe methods (e.g. `unsafeRunSync()`).

```scala
import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class ExampleSuite extends CatsEffectSuite {

  test("tests can return IO[Unit] with assertions expressed via a map") {
    IO(42).map(it => assertEquals(it, 42))
  }

  test("alternatively, assertions can be written via assertIO") {
    assertIO(IO(42), 42)
  }

  test("or via assertEquals syntax") {
    IO(42).assertEquals(42)
  }

  test("or via plain assert syntax on IO[Boolean]") {
    IO(true).assert
  }

  test("SyncIO works too") {
    SyncIO(42).assertEquals(42)
  }

  import cats.effect.std.Dispatcher

  val dispatcher = ResourceFixture(Dispatcher.parallel[IO])

  dispatcher.test("resources can be lifted to munit fixtures") { dsp =>
    dsp.unsafeRunAndForget(IO(42))
  }
}
```

There are more assertion functions like `interceptIO` and `interceptMessageIO` as well as syntax versions `intercept` and `interceptMessage`. See the `CatsEffectAssertions` trait for full details.

Every assertion in `CatsEffectAssertions` for IO-value or SyncIO-value is an IO computation under the hood. If you are planning to use multiple assertions per one test suite, therefore, they should be composed. Otherwise will calculate only the last assertion.

```scala
import cats.syntax.all._
import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class MultipleAssertionsExampleSuite extends CatsEffectSuite {
  test("multiple IO-assertions should be composed") {
    assertIO(IO(42), 42) *>
      assertIO_(IO.unit)
  }

  test("multiple IO-assertions should be composed via for-comprehension") {
    for {
      _ <- assertIO(IO(42), 42)
      _ <- assertIO_(IO.unit)
    } yield ()       
  }

  test("multiple SyncIO-assertions should be composed") {
    assertSyncIO(SyncIO(42), 42) *>
      assertSyncIO_(SyncIO.unit)
  }
    
  test("multiple SyncIO-assertions should be composed via for-comprehension") {
    for {
      _ <- assertSyncIO(SyncIO(42), 42)
      _ <- assertSyncIO_(SyncIO.unit)
    } yield ()       
  }
}
```

## Suite-local fixtures

MUnit supports reusable suite-local fixtures that are instantiated only once for the entire test suite. This is useful when an expensive resource (like an HTTP client) is needed for each test case but it is undesirable to allocate a new one each time.

```scala
import cats.effect.{IO, Resource}
import fs2.io.file.Files

class SuiteLocalExampleSuite extends CatsEffectSuite {

  val myFixture = ResourceSuiteLocalFixture(
    "my-fixture",
    Files[IO].tempFile
  )
  
  val tempFileFixture = ResourceSuitLocalFixture(
    "temp-file",
    Files[IO].tempFile
  )

  override def munitFixtures = List(myFixture, tempFileFixture)

  test("first test") {
    IO(myFixture()).assertEquals(())
  }

  test("second test") {
    IO(myFixture()).assertEquals(())
  }
  
  test("third test") {
    IO(tempFileFixture()).flatMap { file =>
      Files[IO].exists(file).assert
    }
  }

}
```

Notice that this integration is not pure; `myFixture` is mutated internally when the framework initializes the fixture, so the same reference that is used from test cases must be specified in `munitFixtures`. Otherwise an exception `FixtureNotInstantiatedException` will be thrown.
