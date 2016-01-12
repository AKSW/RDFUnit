package org.aksw.rdfunit.tests.executors.monitors;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.impl.results.TestExecutionImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.vocabulary.PROV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple test executor monitor. This is used in the CLI version
 *
 * @author Dimitris Kontokostas
 * @since 5/6/14 2:49 PM
 * @version $Id: $Id
 */
public class SimpleTestExecutorMonitor implements TestExecutorMonitor {

    private static final Logger log = LoggerFactory.getLogger(SimpleTestExecutorMonitor.class);
    private final boolean loggingEnabled;

    private final DatasetOverviewResults overviewResults = new DatasetOverviewResults();

    final private Model model;
    final private String executionUUID;

    private TestSource testedDataset;
    private TestSuite testSuite;
    private TestCaseExecutionType executionType;

    private String userID = "http://localhost/";

    private long counter = 0;

    Collection<TestCaseResult> results = new ArrayList<>();

    /**
     * Instantiates a new Simple test executor monitor.
     */
    public SimpleTestExecutorMonitor() {
        this(ModelFactory.createDefaultModel(), true);
    }

    /**
     * Instantiates a new Simple test executor monitor.
     *
     * @param loggingEnabled have logging enabled / disabled
     */
    public SimpleTestExecutorMonitor(boolean loggingEnabled) {
        this(ModelFactory.createDefaultModel(), loggingEnabled);
    }

    /**
     * Instantiates a new Simple test executor monitor using an external Model.
     *
     * @param model the external model
     */
    public SimpleTestExecutorMonitor(Model model) {
        this(model, true);
    }

    /**
     * Instantiates a new Simple test executor monitor.
     *
     * @param model          the model
     * @param loggingEnabled the logging enabled
     */
    public SimpleTestExecutorMonitor(Model model, boolean loggingEnabled) {
        this.model = model;
        this.loggingEnabled = loggingEnabled;
        PrefixNSService.setNSPrefixesInModel(model);
        executionUUID = PrefixNSService.getURIFromAbbrev("rutr:" + JenaUUID.generate().asString());
    }


    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void singleTestStarted(TestCase test) {
        counter++;
    }

    /** {@inheritDoc} */
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
            overviewResults.increaseSuccessfullTests();
        }
        if (status.equals(TestCaseResultStatus.Fail)) {
            overviewResults.increaseFailedTests();
        }

        for (TestCaseResult result : results) {
            result.serialize(getModel(), executionUUID);
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

    /** {@inheritDoc} */
    @Override
    public void testingFinished() {
        // Set testing end time
        overviewResults.setEndTime();

        //TODO leave for now
        Resource testSuiteResource = testSuite.serialize(getModel());

        List<String> schemata = new ArrayList<>();
        for (SchemaSource src: testedDataset.getReferencesSchemata()) {
            schemata.add(src.getUri());
        }

        TestExecution te = new TestExecutionImpl.Builder()
                .setElement(ResourceFactory.createResource(executionUUID))
                .setDatasetOverviewResults(overviewResults)
                .setStartedByAgent(userID)
                .setTestCaseExecutionType(executionType)
                .setTestedDatasetUri(testedDataset.getUri())
                .setTestSuiteUri(testSuiteResource.getURI())
                .setSchemata(schemata)
                .setResults(results)
                .build();

        TestExecutionWriter.create(te).write(getModel());

        if (loggingEnabled) {
            log.info("Tests run: " + overviewResults.getTotalTests() +
                    ", Failed: " + overviewResults.getFailedTests() +
                    ", Timeout: " + overviewResults.getTimeoutTests() +
                    ", Error: " + overviewResults.getErrorTests() +
                    ". Individual Errors: " + overviewResults.getIndividualErrors());
        }
    }

    /**
     * Gets model that contains the results
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Gets overview results.
     *
     * @return the overview results
     */
    public DatasetOverviewResults getOverviewResults() {
        return overviewResults;
    }

    /**
     * <p>Setter for the field <code>userID</code>.</p>
     *
     * @param userID a {@link java.lang.String} object.
     * @since 0.7.2
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * <p>Setter for the field <code>executionType</code>.</p>
     *
     * @param executionType a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @since 0.7.2
     */
    public void setExecutionType(TestCaseExecutionType executionType) {
        this.executionType = executionType;
    }
}
