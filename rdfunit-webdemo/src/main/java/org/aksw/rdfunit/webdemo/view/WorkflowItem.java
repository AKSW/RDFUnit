package org.aksw.rdfunit.webdemo.view;

/**
 * Abstract the tasks for each step
 *
 * @author Dimitris Kontokostas
 * @since 8 /30/14 11:13 PM
 */
public interface WorkflowItem {

  /**
   * Check if the current item is ready.
   *
   * @return true / false
   */
  boolean isReady();

  /**
   * Sets ready state to an item.
   *
   * @param isReady isReady boolean
   */
  void setReady(boolean isReady);

  /**
   * Gets the previous item in the workflow chain
   *
   * @return the previous item
   */
  WorkflowItem getPreviousItem();

  /**
   * Sets the previous item in the workflow chain
   *
   * @param item the item
   */
  void setPreviousItem(WorkflowItem item);

  /**
   * Gets the next item in the workflow chain
   *
   * @return the next item
   */
  WorkflowItem getNextItem();

  /**
   * Sets the next item in the workflow chain
   *
   * @param item the item
   */
  void setNextItem(WorkflowItem item);

  /**
   * Execute boolean.
   *
   * @return the boolean
   */
  boolean execute();

  /**
   * Set a message to be written on the item.
   *
   * @param message the message
   * @param isError true if the message is an error message
   */
  void setMessage(String message, boolean isError);

}
