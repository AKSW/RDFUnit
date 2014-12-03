package org.aksw.rdfunit.webdemo.view;

/**
 * Abstract the tasks for each step
 *
 * @author Dimitris Kontokostas
 * @since 8 /30/14 11:13 PM
 * @version $Id: $Id
 */
public interface WorkflowItem {
    /**
     * Check if the current item is ready.
     *
     * @return true / false
     */
    public boolean isReady();

    /**
     * Sets ready state to an item.
     *
     * @param isReady isReady boolean
     */
    public void setReady(boolean isReady);

    /**
     * Sets the previous item in the workflow chain
     *
     * @param item the item
     */
    public void setPreviousItem(WorkflowItem item);

    /**
     * Sets the next item in the workflow chain
     *
     * @param item the item
     */
    public void setNextItem(WorkflowItem item);

    /**
     * Gets the previous item in the workflow chain
     *
     * @return the previous item
     */
    public WorkflowItem getPreviousItem();

    /**
     * Gets the next item in the workflow chain
     *
     * @return the next item
     */
    public WorkflowItem getNextItem();

    /**
     * Execute boolean.
     *
     * @return the boolean
     */
    public boolean execute();

    /**
     * Set a message to be written on the item.
     *
     * @param message the message
     * @param isError true if the message is an error message
     */
    public void setMessage(String message, boolean isError);

}
