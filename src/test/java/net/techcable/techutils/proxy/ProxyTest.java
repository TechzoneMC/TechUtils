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
