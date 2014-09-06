package org.aksw.rdfunit.webdemo.view;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.writer.RDFStreamWriter;
import org.aksw.rdfunit.io.writer.RDFWriterException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.WorkflowUtils;

import java.io.ByteArrayOutputStream;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/20/13 5:20 PM
 */
class TestExecutionView extends VerticalLayout implements WorkflowItem {

    private final Button startTestingButton = new Button("Run tests");
    private final Button startTestingCancelButton = new Button("Cancel");
    private final Button resultsButton = new Button("Display Results");

    private final NativeSelect execTypeSelect = new NativeSelect();
    private final NativeSelect resultsFormatsSelect = new NativeSelect();

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

        initData();
        initLayout();

    }

    private void initData() {

        messageLabel.setValue("Please select a report type and press 'Run Tests'");

        execTypeSelect.addItem(TestCaseExecutionType.statusTestCaseResult);
        execTypeSelect.setItemCaption(TestCaseExecutionType.statusTestCaseResult, "Status");
        execTypeSelect.addItem(TestCaseExecutionType.aggregatedTestCaseResult);
        execTypeSelect.setItemCaption(TestCaseExecutionType.aggregatedTestCaseResult, "Counts");
        execTypeSelect.addItem(TestCaseExecutionType.rlogTestCaseResult);
        execTypeSelect.setItemCaption(TestCaseExecutionType.rlogTestCaseResult, "Resources");
        execTypeSelect.addItem(TestCaseExecutionType.extendedTestCaseResult);
        execTypeSelect.setItemCaption(TestCaseExecutionType.extendedTestCaseResult, "Annotated Res.");

        // Select turtle
        execTypeSelect.setNullSelectionAllowed(false);
        execTypeSelect.setValue(TestCaseExecutionType.aggregatedTestCaseResult);


        resultsFormatsSelect.addItem("html");
        resultsFormatsSelect.setItemCaption("html", "HTML");
        resultsFormatsSelect.addItem("turtle");
        resultsFormatsSelect.setItemCaption("turtle", "Turtle");
        resultsFormatsSelect.addItem("ntriples");
        resultsFormatsSelect.setItemCaption("ntriples", "N-Triples");
        resultsFormatsSelect.addItem("n3");
        resultsFormatsSelect.setItemCaption("n3", "N3");
        resultsFormatsSelect.addItem("jsonld");
        resultsFormatsSelect.setItemCaption("jsonld", "JSON-LD");
        resultsFormatsSelect.addItem("rdfjson");
        resultsFormatsSelect.setItemCaption("rdfjson", "RDF/JSON");
        resultsFormatsSelect.addItem("rdfxml");
        resultsFormatsSelect.setItemCaption("rdfxml", "RDF/XML");

        resultsFormatsSelect.setNullSelectionAllowed(false);
        resultsFormatsSelect.setValue("html");
    }


//    public void clearTableRowsAndHide() {
//        resultsTable.removeAllItems();
//        resultsTable.setVisible(false);
//    }

    private void initLayout() {
        this.setWidth("100%");
        this.setSpacing(true);

        HorizontalLayout testHeader = new HorizontalLayout();
        testHeader.setSpacing(true);
        testHeader.setWidth("100%");
        this.addComponent(testHeader);

        testHeader.addComponent(messageLabel);
        testHeader.setExpandRatio(messageLabel, 1.0f);
        testHeader.setComponentAlignment(messageLabel, Alignment.MIDDLE_RIGHT);

        Label selLabel = new Label("Select Report Type:");
        testHeader.addComponent(selLabel);
        selLabel.setWidth("170px");
        testHeader.setComponentAlignment(selLabel, Alignment.MIDDLE_RIGHT);

        testHeader.addComponent(execTypeSelect);
        testHeader.setComponentAlignment(execTypeSelect, Alignment.MIDDLE_CENTER);

        testHeader.addComponent(startTestingCancelButton);
        testHeader.setComponentAlignment(startTestingCancelButton, Alignment.MIDDLE_CENTER);

        testHeader.addComponent(startTestingButton);
        testHeader.setComponentAlignment(startTestingButton, Alignment.MIDDLE_CENTER);


        this.addComponent(new Label("<br/>", ContentMode.HTML));


        HorizontalLayout resultsHeader = new HorizontalLayout();
        resultsHeader.setSpacing(true);
        resultsHeader.setWidth("100%");
        this.addComponent(resultsHeader);


        resultsHeader.addComponent(testingProgress);
        resultsHeader.setExpandRatio(testingProgress, 1.0f);
        testingProgress.setWidth("100%");
        resultsHeader.setComponentAlignment(testingProgress, Alignment.MIDDLE_RIGHT);

        resultsHeader.addComponent(testingProgressLabel);
        testingProgressLabel.setWidth("320px");
        resultsHeader.setComponentAlignment(testingProgressLabel, Alignment.MIDDLE_CENTER);

        Label resLabel = new Label("Select Results Format:");
        resultsHeader.addComponent(resLabel);
        resLabel.setWidth("170px");
        resultsHeader.setComponentAlignment(resLabel, Alignment.MIDDLE_RIGHT);

        resultsHeader.addComponent(resultsFormatsSelect);
        resultsHeader.setComponentAlignment(resultsFormatsSelect, Alignment.MIDDLE_CENTER);


        resultsHeader.addComponent(resultsButton);
        resultsHeader.setComponentAlignment(resultsButton, Alignment.MIDDLE_CENTER);


        // Clicking the button creates and runs a work thread
        startTestingButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                startTestingButton.setEnabled(false);
                UI.getCurrent().push();

                if (inProgress) {
                    setMessage("Test Execution already in progress", true);
                    UI.getCurrent().push();
                    return;
                }
                if (!WorkflowUtils.checkIfPreviousItemIsReady(TestExecutionView.this)) {
                    setMessage("Please Complete previous step correctly", true);
                    UI.getCurrent().push();
                    return;
                }


                TestExecutionView.this.execute();
            }
        });

        startTestingCancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (!inProgress) {
                    Notification.show("Test Execution Not in progress", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                TestExecutionView.this.setMessage("Sending cancel signal, waiting for currect test to execute", false);
                UI.getCurrent().push();
                RDFUnitDemoSession.getTestExecutor().cancel();
            }
        });

        resultsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                if (!isReady) {
                    Notification.show("Test Execution Not completed", Notification.Type.WARNING_MESSAGE);
                    return;
                }

                String resultFormat = FormatService.getOutputFormat(resultsFormatsSelect.getValue().toString()).getName();

                VerticalLayout inner = new VerticalLayout();
                inner.setSpacing(true);
                inner.setWidth("100%");
                inner.setHeight("100%");
                if (resultFormat.equals("html")) {
                    inner.addComponent(new TextArea("html"));
                } else {


                    //OutputStream os;
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        new RDFStreamWriter(os, resultFormat).write(modelMonitor.getModel());
                    } catch (RDFWriterException e) {
                        Notification.show("Error Occurred in Serialization", Notification.Type.ERROR_MESSAGE);
                        // TODO log
                    }

                    TextArea textArea = new TextArea("", os.toString());
                    textArea.setWidth("100%");
                    textArea.setHeight("100%");
                    inner.addComponent(textArea);

                }

                final Window window = new Window("Results");
                window.setWidth("90%");
                window.setHeight("90%");
                window.setModal(true);
                final FormLayout content = new FormLayout();
                window.setContent(inner);


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


                TestCaseExecutionType executionType = (TestCaseExecutionType) execTypeSelect.getValue();
                TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(executionType);
                RDFUnitDemoSession.setTestExecutor(testExecutor);

                TestExecutionView.this.modelMonitor = new SimpleTestExecutorMonitor(false);


                testExecutor.clearTestExecutorMonitor();
                testExecutor.addTestExecutorMonitor(progressMonitor);
                testExecutor.addTestExecutorMonitor(modelMonitor);


                Source dataset = RDFUnitDemoSession.getRDFUnitConfiguration().getTestSource();

                RDFUnitDemoSession.getTestExecutor().execute(dataset, RDFUnitDemoSession.getTestSuite(), 0);

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
                testingProgressLabel.setValue(count + "/" + total + getStatusStr());

//                if ((timeoutTests == 10 || timeoutTests == 30) && sucessTests == 0 && failTest == 0) {
//                    //Too many timeouts maybe banned
//                    Notification.show("Too many timeouts",
//                            "Maybe the endpoint banned this IP. Try a different endpoint of try the tool from a different IP.",
//                            Notification.Type.WARNING_MESSAGE);
//                }

                UI.getCurrent().push();
            }

            @Override
            public void testingFinished() {

                testingProgress.setValue(1.0f);
                setMessage("Completed! " + getStatusStr() + ". See the results or rerun with a different 'Report Type'", false);
                startTestingButton.setEnabled(true);
                isReady = true;
                UI.getCurrent().push();
            }

            private String getStatusStr() {
                return " (S: " + sucessTests + " / F: " + failTest + " / E: " + timeoutTests + " / T : " + totalErrors + ")";
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
