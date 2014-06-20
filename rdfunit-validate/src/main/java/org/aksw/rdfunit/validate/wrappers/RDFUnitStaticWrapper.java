package org.aksw.rdfunit.validate.wrappers;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.io.*;
import org.aksw.rdfunit.sources.DumpSource;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * User: Dimitris Kontokostas
 * RDFUnit Wrapper for a single ontology + manual test cases
 * Created: 6/17/14 8:30 PM
 */
public class RDFUnitStaticWrapper {

    private static DataReader ontologyReader = null;
    private static TestSuite testSuite = null;

    /**
     * The following variables must be initialized with initWrapper before any validation
     */
    private static String ontologyURI = null;
    private static String ontologyResourceURI = null;

    private RDFUnitStaticWrapper() {
    }

    /**
     * This must be called first in order to initialize the Wrapper. Otherwise it will not work
     *
     * @param _ontologyURI         The ontology URI/IRI (for dereferencing)
     */
    public static void initWrapper(String _ontologyURI) {
        initWrapper(_ontologyURI, null);
    }

    /**
     * This must be called first in order to initialize the Wrapper. Otherwise it will not work
     *
     * @param _ontologyURI         The ontology URI/IRI (for dereferencing)
     * @param _ontologyResourceURI The resource URI of the ontology (if the ontology is stored in resources) give null if not applicable
     */
    public static void initWrapper(String _ontologyURI, String _ontologyResourceURI) {
        ontologyURI = _ontologyURI;
        ontologyResourceURI = _ontologyResourceURI;
    }

    /**
     * This functions return the ontology reader as @DataReader
     *
     * @return a @DataReader
     */
    private static DataReader getOntologyReader() {

        // No locking here => possible deadock with getTestSuite()
        // even if it's called twice, there is no harm & the overhead is negligible
        if (ontologyReader == null) {

            // Means initWrapper was not called
            if (ontologyURI == null && ontologyResourceURI == null) {
                throw new IllegalArgumentException("RDFUnitStaticWrapper was not initialized properly. Call initWrapper() once before any validation. ");
            }

            // Reader the ontology either from a resource or, if it fails, dereference it from the URI
            Collection<DataReader> nifReaderList = new ArrayList<>();
            if (ontologyResourceURI != null) {
                nifReaderList.add(new RDFStreamReader(RDFUnitStaticWrapper.class.getResourceAsStream(ontologyResourceURI)));
            }
            nifReaderList.add(DataReaderFactory.createDereferenceReader(ontologyURI));

            ontologyReader = new DataFirstSuccessReader(nifReaderList);
        }
        return ontologyReader;
    }

    public static TestSuite getTestSuite() {
        if (testSuite == null) {
            synchronized (RDFUnitStaticWrapper.class) {
                if (testSuite == null) {


                    // Initialize the nif Source
                    Source nifSchema = new SchemaSource("custom", ontologyURI, getOntologyReader());

                    // Set up the manual nif test cases (from resource)
                    DataReader manualTestCaseReader = new RDFStreamReader(
                            RDFUnitStaticWrapper.class.getResourceAsStream(
                                    CacheUtils.getSourceManualTestFile("/org/aksw/rdfunit/tests/", nifSchema)));

                    // Instantiate manual test cases
                    Collection<TestCase> manualTestCases;
                    try {
                        manualTestCases = TestUtils.instantiateTestsFromModel(manualTestCaseReader.read());
                    } catch (TripleReaderException e) {
                        // Create an empty collection
                        manualTestCases = new ArrayList<TestCase>();
                    }

                    // Generate test cases from ontology (do this every time in case ontology changes)
                    RDFUnit rdfunit = new RDFUnit();
                    try {
                        rdfunit.initPatternsAndGenerators(
                                RDFUnitUtils.getPatternsFromResource(),
                                RDFUnitUtils.getAutoGeneratorsFromResource());
                    } catch (TripleReaderException e) {
                        // fatal error / send only manual test cases
                        testSuite = new TestSuite(manualTestCases);
                        return testSuite; // do not execute further
                    }

                    Collection<TestCase> autoTestCases = TestUtils.instantiateTestsFromAG(rdfunit.getAutoGenerators(), nifSchema);

                    Collection<TestCase> allTestCases = new ArrayList<TestCase>();
                    allTestCases.addAll(autoTestCases);
                    allTestCases.addAll(manualTestCases);

                    testSuite = new TestSuite(allTestCases);
                }
            }
        }

        return testSuite;
    }

    public static Model validate(final Model input) {
        return validate(input, TestCaseExecutionType.rlogTestCaseResult);
    }


    public static Model validate(final Model input, final TestCaseExecutionType executionType) {
        return validate(input, executionType, "custom");
    }

    public static Model validate(final Model input, final String inputURI) {
        return validate(input, TestCaseExecutionType.rlogTestCaseResult, inputURI);
    }

    /**
     * Static method that validates an input model. You MUST call initWrapper once before calling this function
     *
     * @param input         the Model we want to validate
     * @param executionType What type of results we want
     * @param inputURI      A URI/IRI that defines the input source (for reporting purpose only)
     * @return a new Model that contains the validation results. The results are according to executionType
     */
    public static Model validate(final Model input, final TestCaseExecutionType executionType, final String inputURI) {

        final boolean enableRDFUnitLogging = false;
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);
        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(executionType);
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        final Source modelSource = new DumpSource(
                "custom", // prefix
                inputURI,
                new DataModelReader(input), // the input model as a DataReader
                Arrays.asList(  // List of associated ontologies (these will be loaded in the testing model)
                        new SchemaSource("custom", ontologyURI, getOntologyReader()))
        );

        testExecutor.execute(modelSource, getTestSuite(), 0);

        return testExecutorMonitor.getModel();
    }

    /**
     * Static method that validates a Source. In this case the Source & TestSuite are provided as argument along with a RDFUnitCOnfiguration object
     * This function can also serve as standalone
     *
     * @param testCaseExecutionType execution type
     * @param dataset the dataset source we want to test
     * @param testSuite the list of test cases we want to test our Source against
     * @return  a new Model that contains the validation results. The results are according to executionType
     */
    public static Model validate(final TestCaseExecutionType testCaseExecutionType, final Source dataset, final TestSuite testSuite) {

        final boolean enableRDFUnitLogging = false;
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);

        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(testCaseExecutionType);
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);
        testExecutor.execute(dataset, testSuite, 0);

        return testExecutorMonitor.getModel();
    }

}
