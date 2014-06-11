package org.aksw.rdfunit.examples;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnit;
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
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * User: Dimitris Kontokostas
 * Wraps RDFUnit for NIF
 * We use only the nif test cases and they are initiated on first run
 * Created: 5/6/14 5:35 PM
 */
public class RDFUnitWrapperForNIF {

    private static String nifOntologyURI = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";

    // Keep these static and initialize only once on startup / first run
    private static TestSuite testSuite = null;
    private static DataReader nifOntologyReader = null;

    private RDFUnitWrapperForNIF() {
    }

    private static DataReader getNifOntologyReader() {

        // No locking here => possible deadock with getTestSuite()
        // even if it's called twice, there is no harm & the overhead is negligible
        if (nifOntologyReader == null) {

            // Reader the nif ontology either from a resource or, if it fails, dereference it from the URI
            Collection<DataReader> nifReaderList = new ArrayList<>();
            nifReaderList.add(new RDFStreamReader(RDFUnitWrapperForNIF.class.getResourceAsStream("org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl")));
            nifReaderList.add(new RDFDereferenceReader(nifOntologyURI));

            nifOntologyReader = new DataFirstSuccessReader(nifReaderList);
        }
        return nifOntologyReader;
    }

    private static TestSuite getTestSuite() {
        if (testSuite == null) {
            synchronized (RDFUnitWrapperForNIF.class) {
                if (testSuite == null) {


                    // Initialize the nif Source
                    Source nifSchema = new SchemaSource("nif", nifOntologyURI, getNifOntologyReader());

                    // Set up the manual nif test cases (from resource)
                    DataReader manualTestCaseReader = new RDFStreamReader(
                            RDFUnitWrapperForNIF.class.getResourceAsStream(
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

        final boolean enableRDFUnitLogging = false;
        SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);
        TestExecutor testExecutor = TestExecutor.initExecutorFactory(TestCaseExecutionType.rlogTestCaseResult);
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        Source modelSource = new DumpSource(
                "prefix", // prefix
                "uri",    //uri
                new DataModelReader(input), // the input model as a DataReader
                Arrays.asList(  // List of associated ontologies (these will be loaded in the testing model)
                        new SchemaSource("nif", nifOntologyURI, getNifOntologyReader()))
        );

        testExecutor.execute(modelSource, getTestSuite(), 0);

        return testExecutorMonitor.getModel();
    }
}