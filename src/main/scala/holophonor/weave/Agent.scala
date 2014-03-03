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

import java.lang.instrument.{Instrumentation, ClassFileTransformer}
import java.security.ProtectionDomain
import holophonor.org.objectweb.asm.{ClassVisitor, ClassWriter, ClassReader}


object Agent {
  def premain(options: String, instrumentation: Instrumentation) {
    instrumentation.addTransformer(new Agent)
  }
}

class Agent extends ClassFileTransformer {

  def transform(loader: ClassLoader, className: String, @SuppressWarnings(Array("rawtypes")) classBeingRedefined: Class[_], protectionDomain: ProtectionDomain, classfileBuffer: Array[Byte]): Array[Byte] = {
    try {
      if (className.startsWith("org/springframework/samples")) {
        println(className + " is getting a bit of magic")
        val reader: ClassReader = new ClassReader(classfileBuffer)
        val writer: ClassWriter = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES)
        val adapter: ClassVisitor = new ClassWeaver(writer, reader.getClassName)
        reader.accept(adapter, ClassReader.EXPAND_FRAMES)
        writer.toByteArray
      } else {
        null
      }
    }
    catch {
      case e: Error => {
        e.printStackTrace
        throw e
      }
    }
  }

}


