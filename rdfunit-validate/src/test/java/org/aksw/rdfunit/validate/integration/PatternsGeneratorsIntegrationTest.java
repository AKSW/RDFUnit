package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.sources.DumpTestSource;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.DatasetOverviewResults;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class PatternsGeneratorsIntegrationTest {

    private static final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";
    private final RDFUnit rdfUnit = new RDFUnit();

    @Before
    public void setUp() throws Exception {
        // Init RDFUnit
        rdfUnit.init();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOWL() throws Exception {
        Map<String, Integer> testsWithErrorsOWL = new HashMap<>();

        testsWithErrorsOWL.put("owl/OWLDISJC_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/OWLDISJC_Wrong.ttl", 6);
        testsWithErrorsOWL.put("owl/RDFSRANGE_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFSRANGE_Wrong.ttl", 1);
        testsWithErrorsOWL.put("owl/RDFSRANGE-MISS_Wrong.ttl", 1);
        testsWithErrorsOWL.put("owl/RDFSRANGED_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFSRANGED_Wrong.ttl", 2);
        testsWithErrorsOWL.put("owl/INVFUNC_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/INVFUNC_Wrong.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLCARDT_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/OWLCARDT_Wrong_Exact.ttl", 6);
        testsWithErrorsOWL.put("owl/OWLCARDT_Wrong_Min.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLCARDT_Wrong_Max.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Wrong_Exact.ttl", 6);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Wrong_Min.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Wrong_Max.ttl", 2);
        testsWithErrorsOWL.put("owl/RDFLANGSTRING_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFLANGSTRING_Wrong.ttl", 2);

        RDFReader ontologyOWLReader = RDFReaderFactory.createResourceReader(resourcePrefix + "owl/ontology.ttl");
        SchemaSource ontologyOWLSource = new SchemaSource("tests", "http://rdfunit.aksw.org", ontologyOWLReader);

        TestSuite testSuite = new TestSuite(
                TestUtils.instantiateTestsFromAG(rdfUnit.getAutoGenerators(), ontologyOWLSource));

        testMap(testsWithErrorsOWL, testSuite, ontologyOWLSource);
    }

    @Test
    public void testRS() throws Exception {
        Map<String, Integer> testsWithErrorsRS = new HashMap<>();

        testsWithErrorsRS.put("rs/valueType_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/valueType_Wrong.ttl", 11);
        testsWithErrorsRS.put("rs/occurs_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/occurs_Wrong.ttl", 4);
        testsWithErrorsRS.put("rs/range_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/range_Wrong.ttl", 4);
        testsWithErrorsRS.put("rs/allowedValue_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/allowedValue_Wrong.ttl", 9);


        RDFReader ontologyRSReader = RDFReaderFactory.createResourceReader(resourcePrefix + "rs/rs_constraints.ttl");
        SchemaSource ontologyRSSource = new SchemaSource("tests", "http://rdfunit.aksw.org", ontologyRSReader);

        TestSuite testSuite = new TestSuite(
                TestUtils.instantiateTestsFromAG(rdfUnit.getAutoGenerators(), ontologyRSSource));

        testMap(testsWithErrorsRS, testSuite, ontologyRSSource);
    }

    @Test
    public void testDSP() throws Exception {

        Map<String, Integer> testsWithErrorsDSP = new HashMap<>();

        testsWithErrorsDSP.put("dsp/standalone_class_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/standalone_class_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/property_cardinality_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/property_cardinality_Wrong.ttl", 5);
        testsWithErrorsDSP.put("dsp/valueClass_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/valueClass_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/valueClass-miss_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/languageOccurrence_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/languageOccurrence_Wrong.ttl", 2);
        testsWithErrorsDSP.put("dsp/language_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/language_Wrong.ttl", 3);
        testsWithErrorsDSP.put("dsp/isLiteral_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/literal_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/literal_Wrong.ttl", 4);

        RDFReader ontologyDSPReader = RDFReaderFactory.createResourceReader(resourcePrefix + "dsp/dsp_constraints.ttl");
        SchemaSource ontologyDSPSource = new SchemaSource("tests", "http://rdfunit.aksw.org", ontologyDSPReader);


        TestSuite testSuite = new TestSuite(
                TestUtils.instantiateTestsFromAG(rdfUnit.getAutoGenerators(), ontologyDSPSource));

        testMap(testsWithErrorsDSP, testSuite, ontologyDSPSource);
    }

    private void testMap(Map<String, Integer> datasets, TestSuite testSuite, SchemaSource ontologySource) {
        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        for (Map.Entry<String, Integer> entry : datasets.entrySet()) {
            String resource = entry.getKey();
            int errors = datasets.get(resource);

            // Test all execution types
            long failedTestCases = -1;
            for (TestCaseExecutionType executionType : TestCaseExecutionType.values()) {

                // create new dataset for current entry
                final TestSource modelSource = new DumpTestSource(
                        "test", // prefix
                        resource,
                        RDFReaderFactory.createResourceReader(resourcePrefix + resource),
                        Arrays.asList(ontologySource)
                );

                RDFUnitStaticWrapper.validate(executionType, modelSource, testSuite, overviewResults);

                // For status results we don't get violation instances
                if (!executionType.equals(TestCaseExecutionType.statusTestCaseResult)) {
                    assertEquals(executionType + ": Errors not as expected for " + resource, errors, overviewResults.getIndividualErrors());
                }

                if (failedTestCases == -1) {
                    failedTestCases = overviewResults.getFailedTests();
                } else {
                    assertEquals(executionType + ": Failed test cases not as expected for " + resource, failedTestCases, overviewResults.getFailedTests());
                }

                assertEquals(executionType + ": There should be no failed test cases for " + resource, 0, overviewResults.getErrorTests());
            }

        }
    }
}