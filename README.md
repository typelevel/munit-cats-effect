# munit-cats-effect

Integration library for [MUnit](https://scalameta.org/munit/) and [cats-effect](https://github.com/typelevel/cats-effect/).

## Binaries

Cats Effect 2 integration is provided via:

```scala
libraryDependencies += "org.typelevel" %%% "munit-cats-effect-2" % "0.13.1" % "test"
```

Cats Effect 3 integration is provided via:

```scala
libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "0.13.1" % "test"
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

  test("alternatively, asertions can be written via assertIO") {
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

  val dispatcher = ResourceFixture(Dispatcher[IO])

  dispatcher.test("resources can be lifted to munit fixtures") { dsp =>
    dsp.unsafeRunAndForget(IO(42))
  }
}
```

There are more assertion functions like `interceptIO` and `interceptMessageIO` as well as syntax versions `intercept` and `interceptMessage`. See the `CatsEffectAssertions` trait for full details.

