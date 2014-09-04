package org.aksw.rdfunit.webdemo.view;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.sources.EndpointTestSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.QueryGenerationSelectFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.net.URLEncoder;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/20/13 5:20 PM
 */
public class TestExecutionView extends VerticalLayout implements TestExecutorMonitor {

    private final Table resultsTable = new Table("Test Results");
    private Source source = null;

    public TestExecutionView() {
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
        resultsTable.addContainerProperty("Test", Label.class, null);
        resultsTable.addContainerProperty("Errors", AbstractComponent.class, null);
        resultsTable.addContainerProperty("Prevalence", String.class, null);
        resultsTable.setColumnCollapsingAllowed(true);
        resultsTable.setSelectable(true);
        resultsTable.setVisible(false);
        this.addComponent(resultsTable);


        //


    }

    @Override
    public void testingStarted(final Source source, final TestSuite testSuite) {
        this.source = source;
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                resultsTable.setVisible(true);
                resultsTable.setPageLength((int) Math.min(15, testSuite.size()));

            }
        });
    }

    @Override
    public void singleTestStarted(final TestCase test) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                Label testLabel = new Label(test.getTestURI());
                testLabel.setDescription("<pre>  \n" + SafeHtmlUtils.htmlEscape(new QueryGenerationSelectFactory().getSparqlQueryAsString(test)).replaceAll(" +", " ") + "\n  </pre>");
                resultsTable.addItem(new Object[]{
                        "R", testLabel, new Label(""), ""}, test);

                resultsTable.setCurrentPageFirstItemIndex(resultsTable.getCurrentPageFirstItemIndex() + 1);

            }
        });

    }

    @Override
    public void singleTestExecuted(final TestCase test, final TestCaseResultStatus status, final java.util.Collection<TestCaseResult> results) {
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                Item item = resultsTable.getItem(test);
                // Access a property in the item
                long errors = 0, prevalence = 0;
                TestCaseResult result = RDFUnitUtils.getFirstItemInCollection(results);
                if (result != null && result instanceof AggregatedTestCaseResult) {
                    errors = ((AggregatedTestCaseResult) result).getErrorCount();
                    prevalence = ((AggregatedTestCaseResult) result).getPrevalenceCount();
                }

                Property<String> statusProperty =
                        item.getItemProperty("S");
                statusProperty.setValue(errors == 0 ? "S" : (errors > 0 ? "F" : "-"));


                if (errors <= 0) {
                    Property<AbstractComponent> errorsProperty =
                            item.getItemProperty("Errors");
                    errorsProperty.setValue(new Label("-"));
                } else {
                    if (source instanceof EndpointTestSource) {
                        String endpoint = ((EndpointTestSource) source).getSparqlEndpoint();
                        //TODO check default graph uri when array
                        java.util.Collection<String> graphs = ((EndpointTestSource) source).getSparqlGraphs();
                        String graph = RDFUnitUtils.getFirstItemInCollection(graphs);
                        String query = new QueryGenerationSelectFactory().getSparqlQueryAsString(test) + " LIMIT 10";
                        try {
                            String url = endpoint + "?default-graph-uri=" + URLEncoder.encode(graph, "UTF-8") + "&query=" + URLEncoder.encode(query, "UTF-8");
                            Link link = new Link("" + errors, new ExternalResource(url));
                            link.setTargetName("_blank");
                            Property<AbstractComponent> errorsProperty =
                                    item.getItemProperty("Errors");
                            errorsProperty.setValue(link);
                        } catch (Exception e) {
                            Property<AbstractComponent> errorsProperty =
                                    item.getItemProperty("Errors");
                            errorsProperty.setValue(new Label("" + errors));
                        }
                    } else {
                        Property<AbstractComponent> errorsProperty =
                                item.getItemProperty("Errors");
                        errorsProperty.setValue(new Label("" + errors));
                    }
                }

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
