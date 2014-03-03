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

import holophonor.org.objectweb.asm.commons.AdviceAdapter
import holophonor.org.objectweb.asm._

class MethodWeaver(mv: MethodVisitor, access: Int, className: String, name: String, desc: String, weaveClass : String, weaveMethod : String) extends AdviceAdapter(Opcodes.ASM4, mv, access, name, desc) {
  val threadMxBean = newLocal(Type.INT_TYPE)
  val threadCpuStartTime = newLocal(Type.LONG_TYPE)
  val wallStartTime = newLocal(Type.LONG_TYPE)

  protected override def onMethodEnter(): Unit = {
    if ("<init>".equalsIgnoreCase(name)) return
    mv.visitCode()
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/management/ManagementFactory", "getThreadMXBean", "()Ljava/lang/management/ThreadMXBean;")
    mv.visitVarInsn(Opcodes.ASTORE, threadMxBean)
    mv.visitVarInsn(Opcodes.ALOAD, threadMxBean)
    mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/management/ThreadMXBean", "getCurrentThreadCpuTime", "()J")
    mv.visitVarInsn(Opcodes.LSTORE, threadCpuStartTime)
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J")
    mv.visitVarInsn(Opcodes.LSTORE, wallStartTime)
  }


  protected override def onMethodExit(opcode: Int): Unit = {
    if ("<init>".equalsIgnoreCase(name)) return
    val wallTime = newLocal(Type.LONG_TYPE)
    val cpuTime = newLocal(Type.LONG_TYPE)

    mv.visitVarInsn(Opcodes.ALOAD, threadMxBean)
    mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/management/ThreadMXBean", "getCurrentThreadCpuTime", "()J")
    mv.visitVarInsn(Opcodes.LLOAD, threadCpuStartTime)
    mv.visitInsn(Opcodes.LSUB)
    mv.visitVarInsn(Opcodes.LSTORE, cpuTime)
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J")
    mv.visitVarInsn(Opcodes.LLOAD, wallStartTime)
    mv.visitInsn(Opcodes.LSUB)
    mv.visitVarInsn(Opcodes.LSTORE, wallTime)

    mv.visitLdcInsn(className + "#" + name + desc)
    mv.visitVarInsn(Opcodes.LLOAD, cpuTime)
    mv.visitVarInsn(Opcodes.LLOAD, wallTime)
    mv.visitMethodInsn(Opcodes.INVOKESTATIC,weaveClass , weaveMethod, "(Ljava/lang/String;JJ)V")

  }
}

object MethodWeaver {
  def apply(mv: MethodVisitor, access: Int, className: String, name: String, desc: String, weaveClass : String, weaveMethod : String) =
    new MethodWeaver(mv,access,className,name,desc,weaveClass,weaveMethod)
}

class ClassWeaver(cs: ClassVisitor, className: String, weaveClass : String, weaveMethod : String) extends ClassVisitor(Opcodes.ASM4, cs) {
   def this(cs: ClassVisitor, className: String) = this(cs,className,"holophonor/collect/PrintingStatsCollector","recieveStats")

  override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]): MethodVisitor = {
    val mv: MethodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
    MethodWeaver(mv, access, className, name, desc,weaveClass,weaveMethod)
  }
}

object ClassWeaver {
  def apply(cs: ClassVisitor, className: String, weaveClass : String, weaveMethod : String) = new ClassWeaver(cs,className,weaveClass,weaveMethod)
  def apply(cs: ClassVisitor, className: String) = new ClassWeaver(cs,className)
}

