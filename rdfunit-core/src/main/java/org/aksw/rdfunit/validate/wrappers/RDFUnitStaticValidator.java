package org.aksw.rdfunit.validate.wrappers;

import lombok.NonNull;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.apache.jena.rdf.model.Model;

import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * RDFUnit Wrapper for a single ontology + manual test cases
 *
 * @author Dimitris Kontokostas
 * @since 6/17/14 8:30 PM
 * @version $Id: $Id
 */
public final class RDFUnitStaticValidator {

    private static RDFUnitTestSuiteGenerator testSuiteGenerator = null;

    private RDFUnitStaticValidator() {
    }

    public static void initWrapper(@NonNull RDFUnitTestSuiteGenerator testSuiteGenerator) {
        RDFUnitStaticValidator.testSuiteGenerator = testSuiteGenerator;
    }

    public static TestSuite getTestSuite() {
        return testSuiteGenerator.getTestSuite();
    }



    public static TestExecution validate(final Model inputModel) {
        return validate(inputModel, TestCaseExecutionType.rlogTestCaseResult);
    }


    public static TestExecution validate(final Model inputModel, final TestCaseExecutionType executionType) {
        return validate(inputModel, executionType, "custom");
    }


    public static TestExecution validate(final Model inputModel, final String inputURI) {
        return validate(inputModel, TestCaseExecutionType.rlogTestCaseResult, inputURI);
    }

    public static TestExecution validate(final Model inputModel, final TestCaseExecutionType executionType, final String inputURI) {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        return validate(inputModel, executionType, inputURI, overviewResults);
    }


    public static TestExecution validate(final Model inputModel, final TestCaseExecutionType executionType, final String inputURI, DatasetOverviewResults overviewResults) {

        final boolean enableRDFUnitLogging = false;
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);
        testExecutorMonitor.setExecutionType(executionType);

        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(executionType);
        checkNotNull(testExecutor, "TestExecutor should not be null");
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        final TestSource modelSource = new TestSourceBuilder()
                .setPrefixUri("custom", inputURI)
                .setImMemDataset()
                .setInMemReader(new RdfModelReader(inputModel))
                .setReferenceSchemata(testSuiteGenerator.getSchemas())
                .build();


        testExecutor.execute(modelSource, testSuiteGenerator.getTestSuite());
        overviewResults.set(testExecutorMonitor.getOverviewResults());

        return testExecutorMonitor.getTestExecution();
    }

    public static TestExecution validate(final TestCaseExecutionType testCaseExecutionType, final Model model, final TestSuite testSuite) {


        final TestSource testSource = new TestSourceBuilder()
                .setPrefixUri("custom", "http://example.com")
                .setImMemDataset()
                .setInMemReader(new RdfModelReader(model))
                .setReferenceSchemata(Collections.emptyList())
                .build();


        return validate(testCaseExecutionType, testSource, testSuite);
    }


    public static TestExecution validate(final TestCaseExecutionType testCaseExecutionType, final TestSource testSource, final TestSuite testSuite) {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        return validate(testCaseExecutionType, testSource, testSuite, overviewResults);
    }

    /**
     * Static method that validates a Source. In this case the Source and TestSuite are provided as argument along with a RDFUnitConfiguration object
     * This function can also serve as standalone
     */
    public static TestExecution validate(final TestCaseExecutionType testCaseExecutionType, final TestSource testSource, final TestSuite testSuite, DatasetOverviewResults overviewResults) {
        return validate(testCaseExecutionType, testSource, testSuite, "http://localhost", overviewResults);
    }

    /**
     * Static method that validates a Source. In this case the Source and TestSuite are provided as argument along with a RDFUnitConfiguration object
     * This function can also serve as standalone
     */
    public static TestExecution validate(final TestCaseExecutionType testCaseExecutionType, final TestSource testSource, final TestSuite testSuite, final String agentID, DatasetOverviewResults overviewResults) {

        checkNotNull(testCaseExecutionType, "Test Execution Type must not be null");
        checkNotNull(testSource, "Test Source must not be null");
        checkNotNull(testSuite, "Test Suite must not be null");
        checkNotNull(agentID, "Agent must not be null");
        checkNotNull(overviewResults, "Overview Results must not be null");

        final boolean enableRDFUnitLogging = false;
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);
        testExecutorMonitor.setUserID(agentID);
        testExecutorMonitor.setExecutionType(testCaseExecutionType);

        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(testCaseExecutionType);
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);
        testExecutor.execute(testSource, testSuite);
        overviewResults.set(testExecutorMonitor.getOverviewResults());

        return testExecutorMonitor.getTestExecution();
    }

}
