/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.techcable.techutils.proxy;

import lombok.*;
import net.techcable.techutils.Reflection;
import org.apache.commons.lang.ArrayUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

/**
 * Allows for generation of classes at runtime
 */
public class TechProxy {
    private static final TechClassLoader loader = new TechClassLoader();
    private final Class<?> clazz;
    private final Object handlerInstance;
    private final Map<MethodData, MethodHandlerWrapper> handlers = new HashMap<>();

    private final Constructor constructor;

    public TechProxy(Class<?> clazz, Object handlerInstance) {
        this.clazz = clazz;
        this.handlerInstance = handlerInstance;
        this.constructor  = Reflection.makeConstructor(clazz, TechProxy.class);
    }

    public Object newInstance() {
        return Reflection.callConstructor(constructor, this);
    }

    public static TechProxy create(Object handlerInstance, Class<?> superclass, Class<?>... interfaces) {
        Method[] possibleToHandle = superclass.getDeclaredMethods();
        Set<MethodHandlerWrapper> handlers = new HashSet<>();
        for (Class<?> toImplement : interfaces) {
            possibleToHandle = (Method[]) ArrayUtils.addAll(possibleToHandle, toImplement.getDeclaredMethods());
        }
        for (Method method : handlerInstance.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(MethodHandler.class)) continue;
            MethodHandlerWrapper handler = new MethodHandlerWrapper(method, method.getAnnotation(MethodHandler.class), possibleToHandle);
            handlers.add(handler);
        }
        Class<?> bytecode = generate(superclass, interfaces, handlers);
        TechProxy proxy = new TechProxy(bytecode, handlerInstance);
        for (MethodHandlerWrapper handler : handlers) {
            MethodData toHandle = new MethodData(handler.getToHandle());
            proxy.handlers.put(toHandle, handler);
        }
        return proxy;
    }

    public Object handleInvocation(Object instance, String stringMethodData, Object[] args) {
        MethodData methodData = MethodData.fromString(stringMethodData);
        MethodHandlerWrapper wrapper = handlers.get(methodData);
        return Reflection.callMethod(wrapper.getHandler(), handlerInstance, args);
    }

    private static final AtomicInteger generatedClasses = new AtomicInteger(0);
    private static Class<?> generate(Class<?> superClass, Class<?>[] interfaces, Set<MethodHandlerWrapper> handlers) {
        String[] internalInterfaceNames = new String[interfaces.length];
        int i = 0;
        for (Class<?> toImplement : interfaces) {
            internalInterfaceNames[i] = Type.getInternalName(toImplement);
            i++;
        }
        ClassWriter writer  = new ClassWriter(0);
        String className = TechProxy.class.getPackage().getName().replace('.', '/') + "/proxies/Proxy" + generatedClasses.incrementAndGet();
        writer.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, Type.getInternalName(superClass), internalInterfaceNames);
        FieldVisitor fieldWriter = writer.visitField(ACC_PRIVATE, "proxy", "L" + TechProxy.class.getName().replace('.', '/') + ";", null, null);
        // Create a 1-args constructor that accepts a TechProxy
        MethodVisitor methodWriter = writer.visitMethod(ACC_PUBLIC, "<init>", "(L" + TechProxy.class.getName().replace('.', '/') + ";)V", null, null);
        methodWriter.visitCode();
        methodWriter.visitVarInsn(ALOAD, 0);
        methodWriter.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        methodWriter.visitVarInsn(ALOAD, 0);
        methodWriter.visitVarInsn(ALOAD, 1);
        methodWriter.visitFieldInsn(PUTFIELD, className, "proxy", "L" + TechProxy.class.getName().replace('.', '/') + ";");
        methodWriter.visitInsn(RETURN);
        methodWriter.visitMaxs(2, 2);
        methodWriter.visitEnd();
        for (MethodHandlerWrapper handler : handlers) {
            String[] exceptions = new String[handler.getToHandle().getExceptionTypes().length];
            i = 0;
            for (Class<?> exceptionType : handler.getToHandle().getExceptionTypes()) {
                exceptions[i] = Type.getDescriptor(exceptionType);
                i++;
            }
            methodWriter = writer.visitMethod(ACC_PUBLIC, handler.getToHandle().getName(), Type.getMethodDescriptor(handler.getToHandle()), null, exceptions);
            methodWriter.visitCode();
            methodWriter.visitVarInsn(ALOAD, 0);
            methodWriter.visitFieldInsn(GETFIELD, className, "proxy", "L" + TechProxy.class.getName().replace('.', '/') + ";");
            methodWriter.visitVarInsn(ALOAD, 0);
            methodWriter.visitLdcInsn(new MethodData(handler.getToHandle()).toString());
            methodWriter.visitLdcInsn(handler.getToHandle().getParameterCount());
            methodWriter.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            for (i = 0; i < handler.getToHandle().getParameterCount(); i++) {
                String type = Type.getDescriptor(handler.getToHandle().getParameterTypes()[i]);
                switch (type) {
                    case "Z" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(ILOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    case "B" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(ILOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    case "C" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(ILOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    case "D" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(DLOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    case "F" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(FLOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    case "I" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(ILOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        methodWriter.visitInsn(AASTORE);
                    case "J" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(LLOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    case "S" :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(ILOAD, i + 1);
                        methodWriter.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
                        methodWriter.visitInsn(AASTORE);
                        break;
                    default :
                        methodWriter.visitInsn(DUP);
                        methodWriter.visitLdcInsn(i);
                        methodWriter.visitVarInsn(ALOAD, i + 1);
                        methodWriter.visitInsn(AASTORE);
                        break;
                }
            }
            methodWriter.visitMethodInsn(INVOKEVIRTUAL, TechProxy.class.getName().replace('.', '/'), "handleInvocation", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
            String returnType = Type.getDescriptor(handler.getToHandle().getReturnType());
            switch (returnType) {
                case "Z" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
                    methodWriter.visitInsn(IRETURN);
                    break;
                case "B" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Byte");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
                    methodWriter.visitInsn(IRETURN);
                    break;
                case "C" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Character");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
                    methodWriter.visitInsn(IRETURN);
                    break;
                case "S" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Short");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
                    methodWriter.visitInsn(IRETURN);
                    break;
                case "I" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
                    methodWriter.visitInsn(IRETURN);
                    break;
                case "D" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Double");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
                    methodWriter.visitInsn(DRETURN);
                    break;
                case "F" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Float");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
                    methodWriter.visitInsn(FRETURN);
                    break;
                case "J" :
                    methodWriter.visitTypeInsn(CHECKCAST, "java/lang/Long");
                    methodWriter.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
                    methodWriter.visitInsn(LRETURN);
                    break;
                case "V" :
                    methodWriter.visitInsn(RETURN);
                    break;
                default :
                    methodWriter.visitTypeInsn(CHECKCAST, Type.getInternalName(handler.getToHandle().getReturnType()));
                    methodWriter.visitInsn(ARETURN);
            }
            methodWriter.visitMaxs(8, Integer.MAX_VALUE);
            methodWriter.visitEnd();
        }
        writer.visitEnd();
        Class<?> clazz = loader.createClass(writer, className);
        return clazz;
    }

    private static class TechClassLoader extends ClassLoader {
        public Class<?> createClass(ClassWriter writer, String className) {
            byte[] bytes = writer.toByteArray();
            Class<?> clazz = defineClass(className.replace('/', '.'), bytes, 0, bytes.length);
            return clazz;
        }
    }
}

