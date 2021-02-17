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

import cats.effect.{IO, SyncIO}

import scala.concurrent.duration._

class CatsEffectSuiteSpec extends CatsEffectSuite {

  test("nested IO fail".fail) {
    IO {
      IO.sleep(2.millis)
        .flatMap { _ =>
          IO {
            IO.sleep(2.millis)
              .flatMap { _ =>
                IO {
                  IO.sleep(2.millis)
                    .map(_ => assertEquals(false, true))
                }
              }
          }
        }
    }
  }
  test("nested IO success") {
    IO {
      IO.sleep(2.millis)
        .flatMap { _ =>
          IO {
            IO.sleep(2.millis)
              .flatMap { _ =>
                IO {
                  IO.sleep(2.millis)
                    .map(_ => assertEquals(true, true))
                }
              }
          }
        }
    }
  }

  test("nested SyncIO fail".fail) {
    SyncIO
      .pure(())
      .flatMap { _ =>
        SyncIO
          .pure(())
          .flatMap { _ =>
            SyncIO
              .pure(())
              .map(_ => assertEquals(false, true))
          }
      }
  }
  test("nested SyncIO success") {
    SyncIO
      .pure(())
      .flatMap { _ =>
        SyncIO
          .pure(())
          .flatMap { _ =>
            SyncIO
              .pure(())
              .map(_ => assertEquals(true, true))
          }
      }
  }
}
