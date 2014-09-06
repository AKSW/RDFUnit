package org.aksw.rdfunit.webdemo.view;

import com.vaadin.ui.*;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.WorkflowUtils;

import java.io.File;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/20/13 5:20 PM
 */
class TestExecutionView extends VerticalLayout implements WorkflowItem {

    private final Button startTestingButton = new Button("Run tests");
    private final Button startTestingCancelButton = new Button("Cancel");
    private final Button resultsButton = new Button("Display Results");

    private final Label messageLabel = new Label();
    private final TestExecutorMonitor progressMonitor = createProgressMonitor();
    private SimpleTestExecutorMonitor modelMonitor;


    private final ProgressBar testingProgress = new ProgressBar();
    private final Label testingProgressLabel = new Label("0/0");

//    private final Table resultsTable = new Table("Test Results");
//    private Source source = null;


    private WorkflowItem previous;
    private WorkflowItem next;

    private volatile boolean isReady = false;
    private volatile boolean inProgress = false;

    public TestExecutionView() {

        initLayout();

    }


//    public void clearTableRowsAndHide() {
//        resultsTable.removeAllItems();
//        resultsTable.setVisible(false);
//    }

    private void initLayout() {
        this.setWidth("100%");

        HorizontalLayout testHeader = new HorizontalLayout();
        testHeader.setSpacing(true);
        testHeader.setWidth("100%");
        this.addComponent(testHeader);

        testHeader.addComponent(messageLabel);
        testHeader.setExpandRatio(messageLabel, 1.0f);
        testHeader.setComponentAlignment(messageLabel, Alignment.MIDDLE_RIGHT);


        testHeader.addComponent(testingProgress);
        testingProgress.setWidth("150px");
        testHeader.setComponentAlignment(testingProgress, Alignment.MIDDLE_CENTER);

        testHeader.addComponent(testingProgressLabel);
        testHeader.setComponentAlignment(testingProgressLabel, Alignment.MIDDLE_CENTER);

        testHeader.addComponent(startTestingCancelButton);
        testHeader.setComponentAlignment(startTestingCancelButton, Alignment.MIDDLE_CENTER);

        testHeader.addComponent(startTestingButton);
        testHeader.setComponentAlignment(startTestingButton, Alignment.MIDDLE_CENTER);

        //resultsTable.setSizeFull();
//        resultsTable.addContainerProperty("S", String.class, null);
//        resultsTable.addContainerProperty("Test", Label.class, null);
//        resultsTable.addContainerProperty("Errors", AbstractComponent.class, null);
//        resultsTable.addContainerProperty("Prevalence", String.class, null);
//        resultsTable.setColumnCollapsingAllowed(true);
//        resultsTable.setSelectable(true);
//        resultsTable.setVisible(false);
//        this.addComponent(resultsTable);

        testHeader.addComponent(resultsButton);


        // Clicking the button creates and runs a work thread
        startTestingButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                startTestingButton.setEnabled(false);
                UI.getCurrent().push();
                TestExecutionView.this.execute();
            }
        });

        startTestingCancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                TestExecutionView.this.setMessage("Sending cancel signal, waiting for currect test to execute", false);
                UI.getCurrent().push();
                RDFUnitDemoSession.getTestExecutor().cancel();
            }
        });

        resultsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Window window = new Window("Window");
                window.setWidth("90%");
                window.setModal(true);
                final FormLayout content = new FormLayout();
                window.setContent(content);


                UI.getCurrent().addWindow(window);
            }
        });


    }

        //progressMonitor =

    @Override
    public void setMessage(String message, boolean isError) {
        this.isReady = !isError;
        WorkflowUtils.setMessage(messageLabel, message, isError);
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    @Override
    public void setPreviousItem(WorkflowItem item) {
        this.previous = item;
    }

    @Override
    public void setNextItem(WorkflowItem item) {
        this.next = item;
    }

    @Override
    public WorkflowItem getPreviousItem() {
        return previous;
    }

    @Override
    public WorkflowItem getNextItem() {
        return next;
    }

    @Override
    public boolean execute() {


        class TestExecutorThread extends Thread {
            @Override
            public void run() {

                    modelMonitor = new SimpleTestExecutorMonitor(false);
                    TestExecutor testExecutor = RDFUnitDemoSession.getTestExecutor();
                    testExecutor.clearTestExecutorMonitor();
                    testExecutor.addTestExecutorMonitor(progressMonitor);
                    testExecutor.addTestExecutorMonitor(modelMonitor);

                    Source dataset = RDFUnitDemoSession.getRDFUnitConfiguration().getTestSource();

                    String resultsFile = RDFUnitDemoSession.getBaseDir() + "results/" + dataset.getPrefix() + ".results.ttl";
                    //TODO refactor this, do not use cache here
                    File f = new File(resultsFile);
                    try {
                        f.delete();
                    } catch (Exception e) {
                        // catch
                    }
                    RDFUnitDemoSession.getTestExecutor().execute(dataset, RDFUnitDemoSession.getTestSuite(), 3);

            }
        }

        if (RDFUnitDemoSession.getRDFUnitConfiguration() == null)
            return false;



        final TestExecutorThread thread = new TestExecutorThread();
        thread.start();
        return true;
    }

    private TestExecutorMonitor createProgressMonitor() {
        return new TestExecutorMonitor() {
            private long count = 0;
            private long totalErrors = 0;
            private long failTest = 0;
            private long sucessTests = 0;
            private long timeoutTests = 0;
            private long total = 0;

            @Override
            public void testingStarted(final Source source, final TestSuite testSuite) {

                count = totalErrors = failTest = sucessTests = timeoutTests = 0;
                total = testSuite.size();

                startTestingCancelButton.setEnabled(true);
                testingProgress.setEnabled(true);
                testingProgress.setValue(0.0f);
                testingProgressLabel.setValue("0/" + total);
                UI.getCurrent().push();
            }

            @Override
            public void singleTestStarted(final TestCase test) {

            }

            @Override
            public void singleTestExecuted(final TestCase test, final TestCaseResultStatus status, final java.util.Collection<TestCaseResult> results) {

                long errors = 0;
                TestCaseResult result = RDFUnitUtils.getFirstItemInCollection(results);
                if (result != null && result instanceof AggregatedTestCaseResult) {
                    errors = ((AggregatedTestCaseResult) result).getErrorCount();
                }
                count++;
                totalErrors += (errors > 0 ? errors : 0);
                if (errors == -1)
                    timeoutTests++;
                else {
                    if (errors == 0)
                        sucessTests++;
                    else
                        failTest++;
                }

                testingProgress.setValue((float) count / total);
                testingProgressLabel.setValue(count + "/" + total + " (S: " + sucessTests + " / F: " + failTest + " / T: " + timeoutTests + " / E : " + totalErrors + ")");

                if ((timeoutTests == 10 || timeoutTests == 30) && sucessTests == 0 && failTest == 0) {
                    //Too many timeouts maybe banned
                    Notification.show("Too many timeouts",
                            "Maybe the endpoint banned this IP. Try a different endpoint of try the tool from a different IP.",
                            Notification.Type.WARNING_MESSAGE);
                }

                UI.getCurrent().push();
            }

            @Override
            public void testingFinished() {

                testingProgress.setValue(1.0f);
                setMessage("Completed! (S: " + sucessTests + " / F:" + failTest + " / T: " + timeoutTests + " / E : " + totalErrors + ")", false);
                startTestingCancelButton.setEnabled(true);
                UI.getCurrent().push();
            }
        };


    }




     /*
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
    */

}
