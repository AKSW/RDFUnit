package org.aksw.rdfunit.tests.executors.monitors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.impl.results.TestExecutionImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple test executor monitor. This is used in the CLI version
 *
 * @author Dimitris Kontokostas
 * @since 5/6/14 2:49 PM

 */
@Slf4j
public class SimpleTestExecutorMonitor implements TestExecutorMonitor {

    private final boolean loggingEnabled;
    @Getter private TestExecution testExecution;

    private final DatasetOverviewResults overviewResults = new DatasetOverviewResults();

    private final String executionUUID;

    private TestSource testedDataset;
    private TestSuite testSuite;
    private TestCaseExecutionType executionType;

    private String userID = "http://localhost/";

    private long counter = 0;

    private Collection<TestCaseResult> results = new ArrayList<>();


    public SimpleTestExecutorMonitor() {
        this(true);
    }

    public SimpleTestExecutorMonitor(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        executionUUID = JenaUtils.getUniqueIri();
    }



    @Override
    public void testingStarted(TestSource testSource, TestSuite testSuite) {
        testedDataset = testSource;
        this.testSuite = testSuite;

        // init counters
        counter = 0;
        overviewResults.reset();
        results.clear();

        // Set testing start time
        overviewResults.setStartTime();
        overviewResults.setTotalTests(testSuite.size());

        if (loggingEnabled) {
            log.info("Testing {}", testedDataset.getUri());
        }
    }


    @Override
    public void singleTestStarted(TestCase test) {
        counter++;
    }


    @Override
    public void singleTestExecuted(TestCase test, TestCaseResultStatus status, Collection<TestCaseResult> results) {

        this.results.addAll(results);

        if (status.equals(TestCaseResultStatus.Error)) {
            overviewResults.increaseErrorTests();
        }
        if (status.equals(TestCaseResultStatus.Timeout)) {
            overviewResults.increaseTimeoutTests();
        }
        if (status.equals(TestCaseResultStatus.Success)) {
            overviewResults.increaseSuccessfulTests();
        }
        if (status.equals(TestCaseResultStatus.Fail)) {
            overviewResults.increaseFailedTests();
        }

        // in case we have 1 result but is not status
        boolean statusResult = false;

        if (results.size() == 1) {

            //Get item
            TestCaseResult result = RDFUnitUtils.getFirstItemInCollection(results);
            checkNotNull(result);

            if (result instanceof StatusTestCaseResult) {
                statusResult = true;

                if (loggingEnabled) {
                    log.info("Test " + counter + "/" + overviewResults.getTotalTests() + " returned " + result);
                }


                if (result instanceof AggregatedTestCaseResult) {
                    long errorCount = ((AggregatedTestCaseResult) result).getErrorCount();
                    if (errorCount > 0) {
                        long individualErrors = ((AggregatedTestCaseResult) result).getErrorCount();
                        overviewResults.increaseIndividualErrors(individualErrors);
                    }

                }
            }
        }

        if (!statusResult) {
            // TODO RLOG+ results
            long individualErrors = results.size();
            overviewResults.increaseIndividualErrors(individualErrors);

            if (loggingEnabled) {
                log.info("Test " + counter + "/" + overviewResults.getTotalTests() + " returned " + results.size() + " violation instances / TC: " + test.getAbrTestURI());
            }

        }
    }


    @Override
    public void testingFinished() {
        // Set testing end time
        overviewResults.setEndTime();

        List<String> schemata = testedDataset.getReferencesSchemata().stream()
                .map(SchemaSource::getUri).collect(Collectors.toList());

        Set<String> testCaseUris = testSuite.getTestCases().stream()
                .map(TestCase::getTestURI).collect(Collectors.toSet());

        testExecution = new TestExecutionImpl.Builder()
                .setElement(ResourceFactory.createResource(executionUUID))
                .setDatasetOverviewResults(overviewResults)
                .setStartedByAgent(userID)
                .setTestCaseExecutionType(executionType)
                .setTestedDatasetUri(testedDataset.getUri())
                //.setTestSuite(testSuite)
                .setSchemata(schemata)
                .setTestCaseUris(testCaseUris)
                .setResults(results)
                .build();

        if (loggingEnabled) {
            log.info("Tests run: " + overviewResults.getTotalTests() +
                    ", Failed: " + overviewResults.getFailedTests() +
                    ", Timeout: " + overviewResults.getTimeoutTests() +
                    ", Error: " + overviewResults.getErrorTests() +
                    ". Individual Errors: " + overviewResults.getIndividualErrors());
        }
    }

    /**
     * Gets overview results.
     *
     * @return the overview results
     */
    public DatasetOverviewResults getOverviewResults() {
        return overviewResults;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setExecutionType(TestCaseExecutionType executionType) {
        this.executionType = executionType;
    }
}
