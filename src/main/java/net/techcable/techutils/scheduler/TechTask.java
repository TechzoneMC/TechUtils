package net.techcable.techutils.scheduler;

/**
 * A task in a {@link net.techcable.techutils.scheduler.TechScheduler}
 *
 * @author Techcable
 */
public interface TechTask {

    /**
     * Add the specified completion listener to this task
     * <p>
     * A completion listener will run when the task is completed
     * It is up to the implementation to decide which thread the completion listener will run in
     * </p>
     *
     * @param r the completion listener
     */
    public void addCompletionListener(Runnable r);

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
