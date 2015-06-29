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

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = {"name", "signature"})
public class MethodData {

    public MethodData(org.objectweb.asm.commons.Method method) {
        this.signature = method.getDescriptor();
        this.name = method.getName();
    }

    private final String name;
    private final String signature;

    public MethodData(Method method) {
        this.name = method.getName();
        this.signature = Type.getMethodDescriptor(method);
    }

    public static MethodData fromString(String s) {
        String[] parts = s.split(":");
        if (parts.length < 1 || parts.length > 2) throw new IllegalArgumentException();
        return new MethodData(parts[0], parts[1]);
    }

    @Override
    public String toString() {
        return name + ':' + signature;
    }
}
