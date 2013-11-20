package org.aksw.databugger.ui.components;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.aksw.databugger.tests.TestExecutor;
import org.aksw.databugger.tests.UnitTest;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/20/13 5:20 PM
 */
public class TestResultsComponent extends VerticalLayout implements TestExecutor.TestExecutorMonitor {

    private Table resultsTable = new Table("Test Results");

    public TestResultsComponent(){
        initLayout();

    }

    public void initLayout(){
        this.setWidth("100%");

        resultsTable.setSizeFull();
        resultsTable.addContainerProperty("S", String.class, null);
        resultsTable.addContainerProperty("Test", String.class, null);
        resultsTable.addContainerProperty("Errors", String.class, null);
        resultsTable.addContainerProperty("Prevalence", String.class, null);


        //


    }

    @Override
    public void testingStarted(long numberOfTests) {
        this.addComponent(resultsTable);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void singleTestStarted(UnitTest test) {
        resultsTable.addItem(new Object[] {
                "R",test.getTestURI(),"",""},test) ;
        resultsTable.setItemCaption(test,test.getSparql());
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void singleTestExecuted(UnitTest test, String uri, long errors, long prevalence) {
        Item item = resultsTable.getItem(test);
        // Access a property in the item

        Property<String> statusProperty =
                item.getItemProperty("S");
        statusProperty.setValue(errors == 0 ? "S" : (errors > 0 ?  "F" : "-") );

        Property<String> errorsProperty =
                item.getItemProperty("Errors");
        errorsProperty.setValue(errors < 0 ? "-" : "" + errors);

        Property<String> prevProperty =
                item.getItemProperty("Prevalence");
        prevProperty.setValue(errors < 0 ? "-" : "" + prevalence);
    }

    @Override
    public void testingFinished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
