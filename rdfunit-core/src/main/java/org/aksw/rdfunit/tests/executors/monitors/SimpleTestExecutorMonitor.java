package org.aksw.rdfunit.tests.executors.monitors;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.DatasetOverviewResults;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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

    private Source testedDataset;
    private TestSuite testSuite;
    private TestCaseExecutionType executionType;

    private String userID = "http://localhost/";

    private long counter = 0;

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
    public void testingStarted(Source dataset, TestSuite testSuite) {
        testedDataset = dataset;
        this.testSuite = testSuite;

        // init counters
        counter = 0;
        overviewResults.reset();

        // Set testing start time
        overviewResults.setStartTime();
        overviewResults.setTotalTests(testSuite.size());

        if (loggingEnabled) {
            log.info("Testing " + testedDataset.getUri());
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
            assert (result != null);

            if (result instanceof StatusTestCaseResult) {
                statusResult = true;

                if (loggingEnabled) {
                    log.info("Test " + counter + "/" + overviewResults.getTotalTests() + " returned " + result.toString());
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

        Resource testSuiteResource = testSuite.serialize(getModel());

        Resource execution = getModel().createResource(executionUUID);

        execution
                .addProperty(RDF.type, getModel().createResource(PrefixNSService.getURIFromAbbrev("rut:TestExecution")))
                .addProperty(RDF.type, getModel().createResource(PrefixNSService.getURIFromAbbrev("prov:Activity")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:used")), testSuiteResource)
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:startedAtTime")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getStartTime(), XSDDatatype.XSDdateTime))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:endedAtTime")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getEndTime(), XSDDatatype.XSDdateTime))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:source")),
                        getModel().createResource(testedDataset.getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testsRun")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getTotalTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testsSuceedded")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getSuccessfullTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testsFailed")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getFailedTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testsTimeout")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getTimeoutTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testsError")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getErrorTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:totalIndividualErrors")),
                        ResourceFactory.createTypedLiteral("" + overviewResults.getIndividualErrors(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:wasStartedBy")),
                        getModel().createResource(userID));


        // Associate the constraints to the execution
        for (SchemaSource src : testedDataset.getReferencesSchemata()) {
            execution.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:wasAssociatedWith")),
                    getModel().createResource(src.getUri()));
        }

        if (executionType != null) {
            execution.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:executionType")),
                    executionType.name());
        }

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
