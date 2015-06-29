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

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;

import com.google.common.base.Preconditions;

public class TechSchedulerImpl extends TechScheduler {

    public TechSchedulerImpl() {
        TickUtils.addTickListener(new Runnable() {

            @Override
            public void run() {
                tick();
            }
        });
    }

    private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 4);

    @Override
    protected void addTask(ListenableFutureTechTask<?> task) {
        registerQueue.add(task);
    }

    private final Queue<ListenableFutureTechTask<?>> registerQueue = new ConcurrentLinkedQueue<>();
    private final Set<ListenableFutureTechTask<?>> tasks = new HashSet<>();

    public void tick() {
        Preconditions.checkState(Bukkit.isPrimaryThread(), "Must be ticked from main thread");
        for (ListenableFutureTechTask task : registerQueue) {
            tasks.add(task);
        }
        for (ListenableFutureTechTask<?> toTick : tasks) {
            toTick.tick(executor);
        }
    }
}
