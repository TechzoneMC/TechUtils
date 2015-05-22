package net.techcable.techutils.scheduler;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
