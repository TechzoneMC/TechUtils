package net.techcable.techutils;

import static net.techcable.techutils.Reflection.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * An alternative to BukkitScheduler that doesn't need a plugin
 * Uses guava ListenableFutures when possible
 * 
 * @author Techcable
 */
@Beta
public class TechScheduler {
	private TechScheduler() {}
	
	private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1));
	
	/**
	 * Execute a task in another thread
	 * <p>
	 * This should be used (if possible) so you don't lag the main thread
	 * <br>
	 * <b>Remember! Most bukkit calls aren't thread safe</b><br>
	 * If you need to use a bukkit method call scheduleSyncTask
	 * 
	 * @param delay delay before first execution  (in millis)
	 * @param task the task to execute
	 * @return a future tech task to listen for completion
	 */
	public static FutureTechTask<?> scheduleAsyncTask(Runnable task, long delay) {
		return new ListenableFutureTechTask<>(executor.schedule(task, delay, TimeUnit.MILLISECONDS), false);
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
	 * @param delay delay before first execution (in millis)
	 * @return a future tech task to retrieve the result and listen for completion
	 */
	public static <T> FutureTechTask<T> scheduleAsyncTask(Callable<T> task, long delay) {
		return new ListenableFutureTechTask<>(executor.schedule(task, delay, TimeUnit.MILLISECONDS), false);
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
	 * @param delay the dealy before the first execution (in millis)
	 * @param interval time to wait between executions (in millis)
	 * @return a tech task to control execution 
	 */
	public static TechTask scheduleAsyncTask(Runnable task, long delay, long interval) {
		return new ListenableFutureTechTask<>(executor.scheduleAtFixedRate(task, delay, interval, TimeUnit.MILLISECONDS), false);
	}
	
	/**
	 * Execute a task in the main thread
	 * <p>
	 * Long running code should be scheduled asynchronously, but only sync tasks are safe to call most bukkit methods
	 * 
	 * @param delay delay before first execution  (in millis)
	 * @param task the task to execute
	 * @return a future tech task to retreive the result and listen for completion
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static FutureTechTask<?> scheduleSyncTask(final Runnable task, long delay) {
		return scheduleSyncTask(new Callable() {

			@Override
			public Object call() throws Exception {
				task.run();
				return null;
			}
		}, delay);
	}
	
	/**
	 * Execute a task in the main thread
	 * <p>
	 * Long running code should be scheduled asynchronously, but only sync tasks are safe to call most bukkit methods
	 * 
	 * @param delay delay before first execution  (in millis)
	 * @param task the task to execute
	 * @return a future tech task to retrieve the result and listen for completion
	 */
	public static <V> FutureTechTask<V> scheduleSyncTask(Callable<V> task, long delay) {
		return scheduleFutureCraftTask(task, null, getNextId(), delay);
	}
	
	/**
	 * Execute a task in the main thread
	 * <p>
	 * Long running code should be scheduled asynchronously, but only sync tasks are safe to call most bukkit methods
	 * 
	 * @param task the task to execute
	 * @param delay delay before first execution  (in millis)
	 * @param interval time to wait between executions (in millis)
	 * @return a tech task to control execution
	 */
	public static TechTask scheduleSyncTask(Runnable task, long delay, long period) {
		return scheduleCraftTask(null, task, getNextId(), period, delay);
	}
	
	private static final Class<?> CRAFT_TASK_CLASS = Reflection.getCbClass("scheduler.CraftTask");
	private static final Class<?> CRAFT_FUTURE_CLASS = Reflection.getCbClass("scheduler.CraftFuture");
	
	
	
	private static int getNextId() {
		return callMethod(makeMethod(Bukkit.getScheduler().getClass(), "nextId"), Bukkit.getScheduler());
	}
	
	private static TechTask scheduleCraftTask(final Plugin plugin, final Runnable task, final int id, final long period, long delay) {
		if (CRAFT_TASK_CLASS == null) throw new UnsupportedOperationException("Unable to schedule synchronous tasks on non-craftbukkit servers");
		Object craftTask = callConstructor(makeConstructor(CRAFT_TASK_CLASS, Plugin.class, Runnable.class, int.class, long.class), plugin, task, id, period);
		craftTask = callMethod(makeMethod(Bukkit.getScheduler().getClass(), "handle", CRAFT_TASK_CLASS, long.class), Bukkit.getScheduler(), craftTask, delay);
		final BukkitTask bukkitTask = (BukkitTask) craftTask;
		return new TechTask() {
			
			@Override
			public boolean isSync() {
				return bukkitTask.isSync();
			}
			
			@Override
			public boolean cancel() {
				bukkitTask.cancel();
				return true;
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private static <V> FutureTechTask<V> scheduleFutureCraftTask(Callable<V> task, Plugin plugin, int id, long delay) {
		if (CRAFT_FUTURE_CLASS == null) throw new UnsupportedOperationException("Unable to schedule synchronous tasks on non-craftbukkit servers");
		Object craftTask = callConstructor(makeConstructor(CRAFT_FUTURE_CLASS, Callable.class, Plugin.class, int.class), task, plugin, id);
		craftTask = callMethod(makeMethod(Bukkit.getScheduler().getClass(), "handle", CRAFT_TASK_CLASS, long.class), Bukkit.getScheduler(), craftTask, delay);
		return new ListenableFutureTechTask<>(JdkFutureAdapters.listenInPoolThread((Future<V>)craftTask), true);
	}

	public static interface TechTask {
		
	    /**
	     * Returns true if the Task is a sync task.
	     *
	     * @return true if the task is run by main thread
	     */
	    public boolean isSync();
	    
		/**
		 * Attempt to cancel this task
		 *
		 * Won't Interrupt if running
		 * 
		 * Returns false if failed
		 * A return value of true doesn't always indicate success
		 * 
		 * @return false if successful
		 */
		public boolean cancel();
	}
	
	public static interface FutureTechTask<V> extends TechTask, Future<V> {}
	
	@Getter
	@RequiredArgsConstructor
	private static class ListenableFutureTechTask<V> implements FutureTechTask<V> {
		private final ListenableFuture<V> future;
		private final boolean sync;
		
		@Override
		public boolean cancel() {
			return cancel(false);
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return future.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return future.isCancelled();
		}

		@Override
		public boolean isDone() {
			return future.isDone();
		}

		@Override
		public V get() throws InterruptedException, ExecutionException {
			return future.get();
		}

		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			return future.get(timeout, unit);
		}
		
	}
}