package net.techcable.techutils.scheduler;

/**
 * Represents a tech task that returns a value
 *
 * @author Techcable
 */
public interface FutureTechTask<V> extends TechTask {

    /**
     * Add a completion listener to this techtask
     *
     * @param listener the completion listener to add
     */
    public void addCompletionListener(CompletionListener<V> listener);
    /**
     * A listener for success of a techtask
     *
     */
    public static interface CompletionListener<V> {
        public void onSuccess(V value);
    }
}
