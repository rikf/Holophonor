/*
 * Copyright 2014 Richard Friend.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package holophonor.weave

import org.scalatest.{Matchers, FlatSpec}
import holophonor.org.objectweb.asm.{ClassWriter, ClassReader}
import java.lang.reflect.Method
import holophonor.collect.StatsCollector


class MethodWeaverSpec extends FlatSpec with Matchers {

  def instrumentClass(clazz: String, collectorClass: String): Class[_] = {
    val cr = new ClassReader(clazz, this.getClass.getClassLoader)
    val className = cr.getClassName.replaceAll("/", ".")
    val writer: ClassWriter = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
    cr.accept(new ClassWeaver(writer, className, collectorClass, "recieveStats"), ClassReader.EXPAND_FRAMES)


    val m: Method = classOf[ClassLoader].getDeclaredMethod("defineClass", classOf[String], classOf[Array[Byte]], classOf[Int], classOf[Int])
    m.setAccessible(true)
    val callResult = m.invoke(getClass.getClassLoader, "holophonor.weave.Nike", writer.toByteArray, 0: java.lang.Integer, writer.toByteArray.length: java.lang.Integer)
    callResult.asInstanceOf[Class[_]]
  }

  "MethodWeaver" should "Weave instrumentation bytecode into methods" in {

    val nike = instrumentClass("holophonor.weave.Nike", "holophonor/weave/StubCollector").newInstance.asInstanceOf[Nike]

    val preInvocationCallCount = StubCollector.callCount
    nike.doIt()
    val postInvocationCallCount = StubCollector.callCount

    postInvocationCallCount - preInvocationCallCount should equal(1)
  }
}


object StubCollector extends StatsCollector {
  var callCount = 0

  override def recieveStats(name: String, cpuTime: Long, wallTime: Long): Unit = {
    callCount = callCount + 1
  }
}




