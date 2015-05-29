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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProxyTest {
    private TechProxy proxy;

    @Before
    public void setup() {
        proxy = TechProxy.create(new ProxyTestHandler(), Object.class, IProxyTest.class);
    }

    @Test
    public void noArgsStringReturnTest() {IProxyTest tester = (IProxyTest) proxy.newInstance();
        assertEquals(tester.hookeyPokey(), "I'm doing something special");
    }
    @Test
    public void paramaterTest() {
        IProxyTest tester = (IProxyTest) proxy.newInstance();
        assertEquals(tester.doSomethingToANumber(Long.MAX_VALUE), 3);
    }

    public static class ProxyTestHandler {
        @MethodHandler("hookeyPokey")
        public String proxiedHokeyPokey() {
            return "I'm doing something special";
        }
        @MethodHandler("doSomethingToANumber")
        public int doSomethingToANumber(long l) {
            for (int i = 0; i < 5; i++) {
                l = (long) Math.sqrt(l);
            }
            return (int) l;
        }
    }

    public static interface IProxyTest {
        public String hookeyPokey();
        public int doSomethingToANumber(long l);
    }

}
