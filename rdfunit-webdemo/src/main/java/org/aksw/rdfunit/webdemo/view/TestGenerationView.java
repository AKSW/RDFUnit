package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.sources.CacheUtils;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.aksw.rdfunit.tests.generators.monitors.TestGeneratorExecutorMonitor;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.CommonAccessUtils;
import org.aksw.rdfunit.webdemo.utils.WorkflowUtils;

import java.io.File;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/20/13 5:20 PM
 */
final class TestGenerationView extends VerticalLayout implements TestGeneratorExecutorMonitor, WorkflowItem {

    private final Button generateBtn = new Button("Generate tests");
    private final Button cancelBtn = new Button("Cancel");
    private final ProgressBar generateTestsProgress = new ProgressBar();
    private final Label progressLabel = new Label("0/0");
    private final Label messageLabel = new Label();

    private final Table resultsTable = new Table("Test Results");
    private TestGeneratorExecutorMonitor progressMonitor;

    private WorkflowItem previous;
    private WorkflowItem next;

    private volatile boolean isReady = false;
    private volatile boolean inProgress = false;


    /**
     * <p>Constructor for TestGenerationView.</p>
     */
    public TestGenerationView() {
        initLayout();

    }

    private void initLayout() {
        this.setWidth("100%");


        resultsTable.setHeight("250px");
        resultsTable.setWidth("100%");
        resultsTable.addContainerProperty("Type", String.class, null);
        resultsTable.addContainerProperty("URI", Link.class, null);
        resultsTable.addContainerProperty("Automatic", AbstractComponent.class, null);
        resultsTable.addContainerProperty("Manual", AbstractComponent.class, null);
        resultsTable.setColumnCollapsingAllowed(true);
        resultsTable.setSelectable(true);
        resultsTable.setVisible(false);

        this.addComponent(resultsTable);

        messageLabel.setValue("Press Generate to start generating test cases");
        messageLabel.setContentMode(ContentMode.HTML);


        HorizontalLayout genHeader = new HorizontalLayout();
        genHeader.setSpacing(true);
        genHeader.setWidth("100%");
        this.addComponent(genHeader);

        genHeader.addComponent(messageLabel);
        genHeader.setExpandRatio(messageLabel, 1.0f);
        genHeader.setComponentAlignment(messageLabel, Alignment.MIDDLE_RIGHT);

        genHeader.addComponent(generateTestsProgress);
        generateTestsProgress.setWidth("80px");
        genHeader.setComponentAlignment(generateTestsProgress, Alignment.MIDDLE_RIGHT);

        genHeader.addComponent(progressLabel);
        progressLabel.setWidth("40px");
        genHeader.setComponentAlignment(progressLabel, Alignment.MIDDLE_RIGHT);

        genHeader.addComponent(cancelBtn);
        genHeader.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
        genHeader.addComponent(generateBtn);
        genHeader.setComponentAlignment(generateBtn, Alignment.MIDDLE_RIGHT);

        initInteractions();
    }

//    public void clearTableRowsAndHide() {
//        resultsTable.removeAllItems();
//        resultsTable.setVisible(false);
//    }

    /** {@inheritDoc} */
    @Override
    public void generationStarted(final Source source, final long numberOfSources) {
        UI.getCurrent().access(() -> {
            resultsTable.setVisible(true);
            resultsTable.setPageLength((int) Math.min(7, numberOfSources));
            resultsTable.removeAllItems();
            CommonAccessUtils.pushToClient();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void sourceGenerationStarted(final Source source, TestGenerationType generationType) {
        UI.getCurrent().access(() -> {
            Link uriLink = new Link(source.getUri(), new ExternalResource(source.getUri()));
            uriLink.setTargetName("_blank");
            resultsTable.addItem(new Object[]{
                    source.getClass().getSimpleName(), uriLink, new Label("-"), new Label("-")}, source);
            resultsTable.setCurrentPageFirstItemIndex(resultsTable.getCurrentPageFirstItemIndex() + 1);
            CommonAccessUtils.pushToClient();
        });


    }

    /** {@inheritDoc} */
    @Override
    public void sourceGenerationExecuted(final Source source, final TestGenerationType generationType, final long testsCreated) {
        UI.getCurrent().access(() -> {
            Item item = resultsTable.getItem(source);
            if (testsCreated == 0 || item == null)
                return;

            String column = (generationType.equals(TestGenerationType.AutoGenerated) ? "Automatic" : "Manual");
            Property<Link> statusProperty = item.getItemProperty(column);
            String fileName;
            if (generationType.equals(TestGenerationType.AutoGenerated)) {
                fileName = CacheUtils.getSourceAutoTestFile(RDFUnitDemoSession.getBaseDir() + "tests/", source);
                statusProperty.setValue(new Link("" + testsCreated, new FileResource(new File(fileName))));
            } else {
                fileName = CacheUtils.getSourceManualTestFile("/org/aksw/rdfunit/tests/", source);
                statusProperty.setValue(new Link("" + testsCreated, new ClassResource(fileName)));
            }
            CommonAccessUtils.pushToClient();
        });

    }

    /** {@inheritDoc} */
    @Override
    public void generationFinished() {
        UI.getCurrent().access(() -> {
            isReady = true;
            inProgress = false;
            generateBtn.setEnabled(true);
            CommonAccessUtils.pushToClient();
        });

    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(String message, boolean isError) {
        this.isReady = !isError;
        WorkflowUtils.setMessage(messageLabel, message, isError);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReady() {
        return isReady;
    }

    /** {@inheritDoc} */
    @Override
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    /** {@inheritDoc} */
    @Override
    public void setPreviousItem(WorkflowItem item) {
        this.previous = item;
    }

    /** {@inheritDoc} */
    @Override
    public void setNextItem(WorkflowItem item) {
        this.next = item;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowItem getPreviousItem() {
        return previous;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowItem getNextItem() {
        return next;
    }

    /** {@inheritDoc} */
    @Override
    public boolean execute() {
        if (RDFUnitDemoSession.getRDFUnitConfiguration() == null) {
            setMessage("Something went wrong, please retry", true);
            return false;
        }

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
        testGeneratorExecutor.addTestExecutorMonitor(TestGenerationView.this);
        testGeneratorExecutor.addTestExecutorMonitor(TestGenerationView.this.progressMonitor);

        RDFUnitDemoSession.setTestGeneratorExecutor(testGeneratorExecutor);

        final TestGenerationThread thread = new TestGenerationThread();
        thread.start();
        return true;
    }

    private void initInteractions() {
        // Clicking the button creates and runs a work thread
        generateBtn.addClickListener((Button.ClickListener) event -> UI.getCurrent().access(() -> {
            if (!WorkflowUtils.checkIfPreviousItemIsReady(TestGenerationView.this)) {
                setMessage("Please Complete previous step correctly", true);
                return;
            }

            isReady = false;
            inProgress = true;
            TestGenerationView.this.generateBtn.setEnabled(false);
            TestGenerationView.this.setMessage("Generating tests... (note that big ontologies may take a while)", false);
            CommonAccessUtils.pushToClient();

            TestGenerationView.this.execute();

        }));

        cancelBtn.addClickListener((Button.ClickListener) clickEvent -> UI.getCurrent().access(() -> {
            if (inProgress) {
                RDFUnitDemoSession.getTestGeneratorExecutor().cancel();
            } else {
                Notification.show("Nothing to cancel, generation not in progress",
                        Notification.Type.WARNING_MESSAGE);
            }
        }));

        progressMonitor = new TestGeneratorExecutorMonitor() {
            private long count = 0;
            private long total = 0;
            private long tests = 0;

            @Override
            public void generationStarted(final Source source, final long numberOfSources) {
                total = numberOfSources * 2 + 1;
                count = 0;
                tests = 0;
                UI.getCurrent().access(() -> {
                    generateTestsProgress.setValue(0.0f);
                    progressLabel.setValue("0/" + numberOfSources);
                    CommonAccessUtils.pushToClient();
                });


            }

            @Override
            public void sourceGenerationStarted(Source source, TestGenerationType generationType) {
            }

            @Override
            public void sourceGenerationExecuted(final Source source, final TestGenerationType generationType, final long testsCreated) {
                count++;
                tests += testsCreated;
                generateTestsProgress.setValue((float) count / total);
                UI.getCurrent().access(() -> {

                    progressLabel.setValue(count + "/" + total);
                    CommonAccessUtils.pushToClient();
                });

            }

            @Override
            public void generationFinished() {
                UI.getCurrent().access(() -> {
                    generateTestsProgress.setValue(1.0f);
                    WorkflowUtils.setMessage(messageLabel, "Completed! Generated " + tests + " tests\"", false);
                    isReady = true;
                    inProgress = false;
                    CommonAccessUtils.pushToClient();
                });
            }
        };


    }

    private class TestGenerationThread extends Thread {

        @Override
        public void run() {

            RDFUnitDemoSession.setTestSuite(
                    RDFUnitDemoSession.getTestGeneratorExecutor().generateTestSuite(
                            RDFUnitDemoSession.getBaseDir() + "tests/",
                            RDFUnitDemoSession.getRDFUnitConfiguration().getTestSource(),
                            CommonAccessUtils.getRDFUnit().getAutoGenerators()));
        }

    }
}
