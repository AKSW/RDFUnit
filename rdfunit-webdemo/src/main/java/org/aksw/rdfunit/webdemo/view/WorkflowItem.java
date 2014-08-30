package org.aksw.rdfunit.webdemo.view;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 11:13 PM
 */
public interface WorkflowItem {
    public boolean isReady();

    public void setPreviousItem(WorkflowItem item);
    public void setNextItem(WorkflowItem item);

    public WorkflowItem  getPreviousItem();
    public WorkflowItem  getNextItem();

}
