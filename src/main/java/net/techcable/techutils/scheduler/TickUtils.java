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
package net.techcable.techutils.scheduler;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.util.proxy.ProxyFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import net.techcable.cbpatcher.ClassTransformListener;
import net.techcable.techutils.bytecode.ClassTransformEvent;
import net.techcable.techutils.bytecode.TechUtilsPatcher;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import static net.techcable.techutils.Reflection.*;

@Beta
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TickUtils {
    private static final Class<? extends BukkitScheduler> schedulerClass =  Bukkit.getScheduler().getClass();

    private static volatile long currentTick;
    public static long getCurrentTick() {
        return currentTick;
    }

    private static void tick() {
        Preconditions.checkState(Bukkit.isPrimaryThread(), "Not on main thread");
        currentTick++;
        for (Runnable tickListener : tickListeners) {
            try {
                tickListener.run();
            } catch (Exception ignored) {}
        }
    }

    private static final Set<Runnable> tickListeners = new HashSet<>();
    private static void injectTicker() {
        TechUtilsPatcher.addListener(new ClassTransformListener() {
            @Override
            public void onTransform(CtClass ctClass) {
                if (!ctClass.getName().equals(schedulerClass.getName())) return;
                try {
                    CtMethod heartbeatMethod = ctClass.getDeclaredMethod("mainThreadHeartbeat");
                    heartbeatMethod.insertBefore("net.techcable.techutils.scheduler.TickUtils.tick();");
                } catch (NotFoundException | CannotCompileException e) {
                    Bukkit.getLogger().severe("Unable to inject into scheduler heartbeat method.");
                }
            }
        });
    }

    private static boolean setup;
    @Synchronized
    public static void addTickListener(Runnable tickListener) {
        tickListeners.add(tickListener);
        if (setup) return;
        injectTicker();
        setup = true;
    }

    @Getter
    private static final Executor mainThreadExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            TechScheduler.scheduleSyncTask(command, 0);
        }
    };
}
