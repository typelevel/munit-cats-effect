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
import scala.concurrent.Future

class CatsEffectSuiteSpec extends CatsEffectSuite {

  test("nested IO fail") { IO(IO(1)) }
  test("nested IO and SyncIO fail") { IO(SyncIO(1)) }
  test("nested IO and Future fail") { IO(Future(1)) }
  test("nested SyncIO fail") { SyncIO(SyncIO(1)) }
  test("nested SyncIO and IO fail") { SyncIO(IO(1)) }
  test("nested SyncIO and Future fail") { SyncIO(Future(1)) }
}
