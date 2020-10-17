/*
 * Copyright 2020 Typelevel
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
import scala.concurrent.duration._

class CatsEffectAssertionsSpec extends CatsEffectSuite {

  test("assertIO works (successful assertion)") {
    val io = IO.sleep(2.millis) *> IO(2)

    assertIO(io, returns = 2)
  }
  test("assertIO works (failed assertion)".fail) {
    val io = IO.sleep(2.millis) *> IO(2)

    assertIO(io, returns = 3)
  }

}
