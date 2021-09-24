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

import cats.effect.testkit.TestControl
import cats.effect.IO

/** Suite that runs an IO deterministically through [[cats.effect.testkit.TestControl]]. This suite
  * would help to test programs that can cause deadlock which would otherwise complete normally.
  * Also, it allows testing programs that involve [[IO.sleep]]s of any length to complete almost
  * instantly with correct semantics. For more details, dig into [[TestControl.executeEmbed]].
  */
abstract class CatsEffectDeterministicSuite extends CatsEffectSuite {

  /** Seed that used by [[TestControl.executeEmbed]] for an IO execution.
    */
  def ioExecutionSeed: Option[String] = None

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms.filterNot(_ == munitIOTransform) ++ List(
      munitDeterministicIOTransform
    )

  private val munitDeterministicIOTransform: ValueTransform =
    new ValueTransform(
      "Deterministic IO",
      { case e: IO[_] =>
        TestControl.executeEmbed(e, munitIoRuntime.config, ioExecutionSeed).unsafeToFuture()
      }
    )

}
