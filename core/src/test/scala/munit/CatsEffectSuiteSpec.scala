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
import scala.annotation.nowarn
import scala.concurrent.Future
import scala.concurrent.duration._

class CatsEffectSuiteSpec extends CatsEffectSuite {

  override def munitIOTimeout = 100.millis

  @nowarn
  override def munitTimeout = Int.MaxValue.nanos // so only our timeout is in effect

  test("times out".fail) { IO.sleep(1.second) }

  test("nested IO fail".fail) { IO(IO(1)) }
  test("nested IO and SyncIO fail".fail) { IO(SyncIO(1)) }
  test("nested IO and Future fail".fail) { IO(Future.successful(1)) }
  test("nested SyncIO fail".fail) { SyncIO(SyncIO(1)) }
  test("nested SyncIO and IO fail".fail) { SyncIO(IO(1)) }
  test("nested SyncIO and Future fail".fail) { SyncIO(Future.successful(1)) }
}
