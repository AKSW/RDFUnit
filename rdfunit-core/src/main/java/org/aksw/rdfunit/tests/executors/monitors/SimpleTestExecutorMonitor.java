package org.aksw.rdfunit.tests.executors.monitors;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * User: Dimitris Kontokostas
 * A simple test executor monitor. This is used in the CLI version
 * Created: 5/6/14 2:49 PM
 */
public class SimpleTestExecutorMonitor implements TestExecutorMonitor {

    private static final Logger log = LoggerFactory.getLogger(SimpleTestExecutorMonitor.class);
    private final boolean loggingEnabled;

    final private Model model;
    final String executionUUID;

    private Source testedDataset;
    private TestSuite testSuite;

    private long counter = 0;
    private long totalTests = 0;
    private long success = 0;
    private long fail = 0;
    private long timeout = 0;
    private long error = 0;
    private long totalErrors = 0;

    XSDDateTime startTime;
    XSDDateTime endTime;

    public SimpleTestExecutorMonitor() {
        this(ModelFactory.createDefaultModel(), true);
    }

    public SimpleTestExecutorMonitor(boolean loggingEnabled) {
        this(ModelFactory.createDefaultModel(), loggingEnabled);
    }

    public SimpleTestExecutorMonitor(Model model) {
        this(model, true);
    }

    public SimpleTestExecutorMonitor(Model model, boolean loggingEnabled) {
        this.model = model;
        this.loggingEnabled = loggingEnabled;
        model.setNsPrefixes(PrefixService.getPrefixMap());
        executionUUID = JenaUUID.generate().asString();
    }


    @Override
    public void testingStarted(Source dataset, TestSuite testSuite) {
        testedDataset = dataset;
        this.testSuite = testSuite;
        totalTests = testSuite.size();

        // init counters
        counter = success = fail = timeout = error = totalErrors = 0;

        if (loggingEnabled) {
            log.info("Testing " + testedDataset.getUri());
        }
    }

    @Override
    public void singleTestStarted(TestCase test) {
        counter++;
        startTime = new XSDDateTime(Calendar.getInstance());
    }

    @Override
    public void singleTestExecuted(TestCase test, TestCaseResultStatus status, java.util.Collection<TestCaseResult> results) {

        if (status.equals(TestCaseResultStatus.Error))
            error++;
        if (status.equals(TestCaseResultStatus.Timeout))
            timeout++;
        if (status.equals(TestCaseResultStatus.Success))
            success++;
        if (status.equals(TestCaseResultStatus.Fail))
            fail++;

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
                    log.info("Test " + counter + "/" + totalTests + " returned " + result.toString());
                }


                if (result instanceof AggregatedTestCaseResult) {
                    long errorCount = ((AggregatedTestCaseResult) result).getErrorCount();
                    if (errorCount > 0)
                        totalErrors += ((AggregatedTestCaseResult) result).getErrorCount();
                }
            }
        }

        if (!statusResult) {
            // TODO RLOG+ results
            totalErrors += results.size();

        }
    }

    @Override
    public void testingFinished() {
        endTime = new XSDDateTime(Calendar.getInstance());

        Resource testSuiteResource = testSuite.serialize(getModel());

        getModel().createResource(executionUUID)
                .addProperty(RDF.type, getModel().createResource(PrefixService.getPrefix("rut") + "TestExecution"))
                .addProperty(RDF.type, getModel().createResource(PrefixService.getPrefix("prov") + "Activity"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "used"), testSuiteResource)
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "startedAtTime"),
                        ResourceFactory.createTypedLiteral("" + startTime, XSDDatatype.XSDdateTime))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "endedAtTime"),
                        ResourceFactory.createTypedLiteral("" + endTime, XSDDatatype.XSDdateTime))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "source"),
                        getModel().createResource(testedDataset.getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsRun"),
                        ResourceFactory.createTypedLiteral("" + totalTests, XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsSuceedded"),
                        ResourceFactory.createTypedLiteral("" + success, XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsFailed"),
                        ResourceFactory.createTypedLiteral("" + fail, XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsTimeout"),
                        ResourceFactory.createTypedLiteral("" + timeout, XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsError"),
                        ResourceFactory.createTypedLiteral("" + error, XSDDatatype.XSDnonNegativeInteger))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "totalIndividualErrors"),
                        ResourceFactory.createTypedLiteral("" + totalErrors, XSDDatatype.XSDnonNegativeInteger));

        if (loggingEnabled) {
            log.info("Tests run: " + totalTests + ", Failed: " + fail + ", Timeout: " + timeout + ", Error: " + error + ". Individual Errors: " + totalErrors);
        }
    }

    public Model getModel() {
        return model;
    }
}
