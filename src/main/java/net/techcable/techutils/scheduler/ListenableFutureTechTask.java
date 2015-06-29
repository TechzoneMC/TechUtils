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

import lombok.*;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import net.techcable.techutils.collect.Either;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ListenableFutureTechTask<V> extends AbstractFuture<V> implements FutureTechTask<V> {

    private final Either<Callable<V>, Runnable> task;
    @Getter
    private final boolean sync;
    private boolean repeating;
    private long period;
    private long ticksTillNextExecution = 0;

    protected void tick(Executor executor) {
        if (ticksTillNextExecution != 0) {
            ticksTillNextExecution--;
            return;
        }
        executor = sync ? MoreExecutors.sameThreadExecutor() : executor;
        Runnable runnable = task.hasFirst() ? new Runnable() {

            @Override
            public void run() {
                try {
                    V value = task.getFirst().call();
                    set(value);
                } catch (Throwable t) {
                    setException(t);
                }
            }
        } : task.getSecond();
        executor.execute(runnable);
        if (repeating) {
            ticksTillNextExecution = period;
        }
    }

    @Override
    public void addCompletionListener(final Runnable r) {
        addCompletionListener(new CompletionListener<V>() {

            @Override
            public void onSuccess(V value) {
                r.run();
            }
        });
    }

    @Override
    public boolean cancel() {
        return cancel(false);
    }

    static <V> ListenableFutureTechTask<V> create(Either<Callable<V>, Runnable> task, boolean sync, long delay) {
        ListenableFutureTechTask<V> techTask = new ListenableFutureTechTask<>(task, sync);
        techTask.ticksTillNextExecution = delay;
        return techTask;
    }


    static <V> ListenableFutureTechTask<V> createRepeating(Either<Callable<V>, Runnable> task, boolean sync, long delay, long period) {
        ListenableFutureTechTask<V> techTask = new ListenableFutureTechTask<>(task, sync);
        techTask.ticksTillNextExecution = delay;
        techTask.repeating = true;
        techTask.period = period;
        return techTask;
    }

    @Override
    public void addCompletionListener(final CompletionListener<V> listener) {
        addListener(new Runnable() {

            @Override
            public void run() {
                V value = Futures.getUnchecked(ListenableFutureTechTask.this);
                listener.onSuccess(value);
            }
        }, MoreExecutors.sameThreadExecutor());
    }
}
