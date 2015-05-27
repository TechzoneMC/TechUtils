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

import java.util.concurrent.*;

import com.google.common.util.concurrent.*;
import lombok.Getter;

import lombok.Setter;

import com.google.common.annotations.Beta;
import net.techcable.techutils.collect.Either;

/**
 * An alternative to BukkitScheduler that doesn't need a plugin
 * Uses guava ListenableFutures when possible
 * 
 * @author Techcable
 */
@Beta
public abstract class TechScheduler {
	protected TechScheduler() {}

    @Getter
    @Setter
    private static TechScheduler instance = new TechSchedulerImpl();
	private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

	/**
	 * Execute a task in another thread
	 * <p>
	 * This should be used (if possible) so you don't lag the main thread
	 * <br>
	 * <b>Remember! Most bukkit calls aren't thread safe</b><br>
	 * If you need to use a bukkit method call scheduleSyncTask
	 *
	 * @param delay delay before first execution  (in ticks)
	 * @param task the task to execute
	 * @return a future tech task to listen for completion
	 */
	public static TechTask scheduleAsyncTask(final Runnable task, long delay) {
        ListenableFutureTechTask techTask = ListenableFutureTechTask.create(Either.<Callable<Void>, Runnable>ofSecond(task), false, delay);
        getInstance().addTask(techTask);
        return techTask;
    }
	
	/**
	 * Execute a task in another thread
	 * <p>
	 * This should be used (if possible) so you don't lag the main thread
	 * <br>
	 * <b>Remember! Most bukkit calls aren't thread safe</b><br>
	 * If you need to use a bukkit method call scheduleSyncTask
	 * 
	 * 
	 * @param task the task to execute
	 * @param delay delay before first execution (in ticks)
	 * @return a future tech task to retrieve the result and listen for completion
	 */
	public static <T> FutureTechTask<T> scheduleAsyncTask(Callable<T> task, long delay) {
        ListenableFutureTechTask<T> techTask = ListenableFutureTechTask.create(Either.<Callable<T>, Runnable>ofFirst(task), false, delay);
	    getInstance().addTask(techTask);
        return techTask;
    }
	
	/**
	 * Executes a task repeatedly in another thread
	 * <p>
	 * This should be used (if possible) so you don't lag the main thread
	 * <br>
	 * <b>Remember! Most bukkit calls aren't thread safe</b><br>
	 * If you need to use a bukkit method call scheduleSyncTask
	 * 
	 * @param task the task to execute
	 * @param delay the dealy before the first execution (in ticks)
	 * @param interval time to wait between executions (in ticks)
	 * @return a tech task to control execution 
	 */
	public static TechTask scheduleAsyncTask(Runnable task, long delay, long interval) {
        ListenableFutureTechTask techTask = ListenableFutureTechTask.createRepeating(Either.<Callable<Void>, Runnable>ofSecond(task), false, delay, interval);
        getInstance().addTask(techTask);
        return techTask;
	}
	
	/**
	 * Execute a task in the main thread
	 * <p>
	 * Long running code should be scheduled asynchronously, but only sync tasks are safe to call most bukkit methods
	 * 
	 * @param delay delay before first execution  (in ticks)
	 * @param task the task to execute
	 * @return a future tech task to retreive the result and listen for completion
	 */
	public static TechTask scheduleSyncTask(final Runnable task, long delay) {
        ListenableFutureTechTask techTask = ListenableFutureTechTask.create(Either.<Callable<Void>, Runnable>ofSecond(task), true, delay);
        getInstance().addTask(techTask);
        return techTask;
	}
	
	/**
	 * Execute a task in the main thread
	 * <p>
	 * Long running code should be scheduled asynchronously, but only sync tasks are safe to call most bukkit methods
	 * 
	 * @param delay delay before first execution  (in ticks)
	 * @param task the task to execute
	 * @return a future tech task to retrieve the result and listen for completion
	 */
	public static <V> FutureTechTask<V> scheduleSyncTask(Callable<V> task, long delay) {
        ListenableFutureTechTask<V> techTask = ListenableFutureTechTask.create(Either.<Callable<V>, Runnable>ofFirst(task), true, delay);
        getInstance().addTask(techTask);
        return techTask;
	}

	/**
	 * Execute a task in the main thread
	 * <p>
	 * Long running code should be scheduled asynchronously, but only sync tasks are safe to call most bukkit methods
	 * 
	 * @param task the task to execute
	 * @param delay delay before first execution  (in ticks)
	 * @param period time to wait between executions (in ticks)
	 * @return a tech task to control execution
	 */
	public static TechTask scheduleSyncTask(Runnable task, long delay, long period) {
        ListenableFutureTechTask<?> techTask = ListenableFutureTechTask.create(Either.<Callable<Void>, Runnable>ofSecond(task), true, delay);
        getInstance().addTask(techTask);
        return techTask;
    }

    protected abstract void addTask(ListenableFutureTechTask<?> task);
}