package org.aksw.databugger.ui.components;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
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

    public TestResultsComponent() {
        initLayout();

    }


    public void clearTableRowsAndHide() {
        resultsTable.removeAllItems();
        resultsTable.setVisible(false);
    }

    public void initLayout() {
        this.setWidth("100%");

        //resultsTable.setSizeFull();
        resultsTable.addContainerProperty("S", String.class, null);
        resultsTable.addContainerProperty("Test", String.class, null);
        resultsTable.addContainerProperty("Errors", String.class, null);
        resultsTable.addContainerProperty("Prevalence", String.class, null);
        resultsTable.setColumnCollapsingAllowed(true);
        resultsTable.setSelectable(true);
        resultsTable.setVisible(false);
        this.addComponent(resultsTable);


        //


    }

    @Override
    public void testingStarted(final long numberOfTests) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                resultsTable.setVisible(true);
                resultsTable.setPageLength((int) Math.min(15, numberOfTests));

            }
        });
    }

    @Override
    public void singleTestStarted(final UnitTest test) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                resultsTable.addItem(new Object[]{
                        "R", test.getTestURI(), "", ""}, test);
                resultsTable.setItemCaption(test, test.getSparql());
                resultsTable.setCurrentPageFirstItemIndex(resultsTable.getCurrentPageFirstItemIndex() + 1);

            }
        });

    }

    @Override
    public void singleTestExecuted(final UnitTest test, final String uri, final long errors, final long prevalence) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                Item item = resultsTable.getItem(test);
                // Access a property in the item

                Property<String> statusProperty =
                        item.getItemProperty("S");
                statusProperty.setValue(errors == 0 ? "S" : (errors > 0 ? "F" : "-"));

                Property<String> errorsProperty =
                        item.getItemProperty("Errors");
                errorsProperty.setValue(errors < 0 ? "-" : "" + errors);

                Property<String> prevProperty =
                        item.getItemProperty("Prevalence");
                prevProperty.setValue(errors < 0 ? "-" : "" + prevalence);
            }
        });

    }

    @Override
    public void testingFinished() {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
