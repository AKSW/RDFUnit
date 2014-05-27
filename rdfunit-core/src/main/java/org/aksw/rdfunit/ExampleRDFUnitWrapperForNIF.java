package org.aksw.rdfunit;

import com.hp.hpl.jena.rdf.model.Model;
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
 * Example use case of RDFUnit for NLP2RDF validator
 * Here you use only the nif test cases and they are initiated on first run
 * Created: 5/7/14 11:44 PM
 */
public class ExampleRDFUnitWrapperForNIF {

    private static String nifOntologyURI = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";

    // Keep these static and initialize only once on startup / first run
    private static TestSuite testSuite = null;
    private static DataReader nifOntologyReader = null;

    private ExampleRDFUnitWrapperForNIF() {

    }

    private static TestSuite getTestSuite() {
        if (testSuite == null) {
            synchronized (ExampleRDFUnitWrapperForNIF.class) {
                if (testSuite == null) {

                    // Reader the nif ontology either from a resource or, if it fails, dereference it from the URI
                    Collection<DataReader> nifReaderList = new ArrayList<>();
                    nifReaderList.add(new RDFStreamReader(ExampleRDFUnitWrapperForNIF.class.getResourceAsStream("org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl")));
                    nifReaderList.add(new RDFDereferenceReader(nifOntologyURI));

                    nifOntologyReader = new DataFirstSuccessReader(nifReaderList);
                    // Initialize the nif Source
                    Source nifSchema = new SchemaSource("nif", nifOntologyURI, nifOntologyReader);

                    // Set up the manual nif test cases (from resource)
                    DataReader manualTestCaseReader = new RDFStreamReader(
                            ExampleRDFUnitWrapperForNIF.class.getResourceAsStream(
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

        SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor();
        TestExecutor testExecutor = TestExecutor.initExecutorFactory(TestCaseExecutionType.rlogTestCaseResult);
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        Source modelSource = new DumpSource(
                "prefix", // prefix
                "uri",    //uri
                new DataModelReader(input), // the input model as a DataReader
                Arrays.asList(  // List of associated ontologies (these will be loaded in the testing model)
                        new SchemaSource("nif", nifOntologyURI, nifOntologyReader))
        );

        testExecutor.execute(modelSource, getTestSuite(), 0);

        return testExecutorMonitor.getModel();
    }
}

