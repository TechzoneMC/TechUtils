package net.techcable.techutils.scheduler;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.techcable.techutils.collect.Either;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

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
