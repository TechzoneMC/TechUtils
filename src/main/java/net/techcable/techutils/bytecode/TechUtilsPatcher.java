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
package net.techcable.techutils.bytecode;

import com.google.common.annotations.Beta;
import javassist.CtClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import net.techcable.cbpatcher.AbstractCBPatcher;
import net.techcable.cbpatcher.ClassTransformListener;
import org.bukkit.Bukkit;

import java.io.File;

@Beta
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TechUtilsPatcher extends AbstractCBPatcher {
    @Getter
    private static TechUtilsPatcher instance = new TechUtilsPatcher();

    @Override
    protected File getNativeDir() {
        return new File(Bukkit.getWorldContainer().getParentFile(), "natives");
    }

    @Override
    protected void log0(String s) {
        Bukkit.getLogger().info(s);
    }

    public static void inject(Class<?> toInject) {
        getInstance().injectClass(toInject);
        getInstance().update();
    }

    @Override
    public void injectClass(Class<?> toInject) {
        needsTransform = true;
        super.injectClass(toInject);
    }

    public static void addListener(ClassTransformListener listener) {
        getInstance().addTransformListener(listener);
        getInstance().update();
    }

    @Override
    public void addTransformListener(ClassTransformListener listener) {
        needsTransform = true;
        super.addTransformListener(listener);
    }

    private volatile boolean needsTransform;
    private boolean setup;
    private final Object transformLock = new Object();
    public void update() {
        if (!setup) return;
        if (needsTransform) {
            synchronized (transformLock) {
                if (needsTransform) {
                    log("[TechUtils] Retransforming all classes");
                    transformAll();
                }
            }
            needsTransform = false;
        }
    }

    @Synchronized("transformLock")
    public void setup() {
        if (setup) return;
        setup = true;
        boolean oldNeedsTransform = this.needsTransform;
        addTransformListener(new ClassTransformListener() {
            @Override
            public void onTransform(CtClass ctClass) {
                ClassTransformEvent.fire(ctClass);
            }
        });
        if (ClassTransformEvent.getHandlerList().getRegisteredListeners().length == 0) {
            this.needsTransform = oldNeedsTransform; // Reset transformation needed status if there are no bukkit listeners
        }
        update();
    }
}
