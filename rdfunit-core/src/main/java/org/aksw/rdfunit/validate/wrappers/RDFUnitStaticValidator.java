package org.aksw.rdfunit.validate.wrappers;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.apache.jena.rdf.model.Model;

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

    /**
     * <p>initWrapper.</p>
     *
     * @param testSuiteGenerator a {@link org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator} object.
     */
    public static void initWrapper(RDFUnitTestSuiteGenerator testSuiteGenerator) {
        checkNotNull(testSuiteGenerator);
        RDFUnitStaticValidator.testSuiteGenerator = testSuiteGenerator;
    }

    /**
     * <p>getTestSuite.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.interfaces.TestSuite} object.
     */
    public static TestSuite getTestSuite() {
        return testSuiteGenerator.getTestSuite();
    }



    /**
     * <p>validate.</p>
     *
     * @param inputModel a {@link org.apache.jena.rdf.model.Model} object.
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public static Model validate(final Model inputModel) {
        return validate(inputModel, TestCaseExecutionType.rlogTestCaseResult);
    }


    /**
     * <p>validate.</p>
     *
     * @param inputModel a {@link org.apache.jena.rdf.model.Model} object.
     * @param executionType a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public static Model validate(final Model inputModel, final TestCaseExecutionType executionType) {
        return validate(inputModel, executionType, "custom");
    }


    /**
     * <p>validate.</p>
     *
     * @param inputModel a {@link org.apache.jena.rdf.model.Model} object.
     * @param inputURI a {@link java.lang.String} object.
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public static Model validate(final Model inputModel, final String inputURI) {
        return validate(inputModel, TestCaseExecutionType.rlogTestCaseResult, inputURI);
    }


    /**
     * <p>validate.</p>
     *
     * @param inputModel a {@link org.apache.jena.rdf.model.Model} object.
     * @param executionType a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @param inputURI a {@link java.lang.String} object.
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public static Model validate(final Model inputModel, final TestCaseExecutionType executionType, final String inputURI) {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        return validate(inputModel, executionType, inputURI, overviewResults);
    }


    /**
     * <p>validate.</p>
     *
     * @param inputModel a {@link org.apache.jena.rdf.model.Model} object.
     * @param executionType a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @param inputURI a {@link java.lang.String} object.
     * @param overviewResults a {@link DatasetOverviewResults} object.
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public static Model validate(final Model inputModel, final TestCaseExecutionType executionType, final String inputURI, DatasetOverviewResults overviewResults) {

        final boolean enableRDFUnitLogging = false;
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);

        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(executionType);
        checkNotNull(testExecutor, "TestExecutor should not be null");
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        final TestSource modelSource = new TestSourceBuilder()
                .setPrefixUri("custom", inputURI)
                .setImMemDataset()
                .setInMemReader(new RDFModelReader(inputModel))
                .setReferenceSchemata(testSuiteGenerator.getSchemas())
                .build();


        testExecutor.execute(modelSource, testSuiteGenerator.getTestSuite());
        overviewResults.set(testExecutorMonitor.getOverviewResults());

        return testExecutorMonitor.getModel();
    }


    /**
     * <p>validate.</p>
     *
     * @param testCaseExecutionType a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @param testSource a {@link org.aksw.rdfunit.sources.TestSource} object.
     * @param testSuite a {@link org.aksw.rdfunit.model.interfaces.TestSuite} object.
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public static Model validate(final TestCaseExecutionType testCaseExecutionType, final TestSource testSource, final TestSuite testSuite) {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        return validate(testCaseExecutionType, testSource, testSuite, overviewResults);
    }

    /**
     * Static method that validates a Source. In this case the Source and TestSuite are provided as argument along with a RDFUnitConfiguration object
     * This function can also serve as standalone
     *
     * @param testCaseExecutionType execution type
     * @param testSource               the dataset source we want to test
     * @param testSuite             the list of test cases we want to test our Source against
     * @param overviewResults This is a way to get validation statistics
     * @return a new Model that contains the validation results. The results are according to executionType
     */
    public static Model validate(final TestCaseExecutionType testCaseExecutionType, final TestSource testSource, final TestSuite testSuite, DatasetOverviewResults overviewResults) {
        return validate(testCaseExecutionType, testSource, testSuite, "http://localhost", overviewResults);
    }

    /**
     * Static method that validates a Source. In this case the Source and TestSuite are provided as argument along with a RDFUnitConfiguration object
     * This function can also serve as standalone
     *
     * @param testCaseExecutionType execution type
     * @param testSource               the dataset source we want to test
     * @param testSuite             the list of test cases we want to test our Source against
     * @param agentID             an identifier that will be set in the execution provenance data as prov:wasStartedBy
     * @param overviewResults This is a way to get validation statistics
     * @return a new Model that contains the validation results. The results are according to executionType
     */
    public static Model validate(final TestCaseExecutionType testCaseExecutionType, final TestSource testSource, final TestSuite testSuite, final String agentID, DatasetOverviewResults overviewResults) {

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

        return testExecutorMonitor.getModel();
    }

}
