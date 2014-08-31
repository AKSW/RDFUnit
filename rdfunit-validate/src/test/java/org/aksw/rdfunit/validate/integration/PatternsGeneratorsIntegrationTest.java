package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.RDFMultipleReader;
import org.aksw.rdfunit.io.RDFReader;
import org.aksw.rdfunit.io.RDFReaderFactory;
import org.aksw.rdfunit.sources.DumpTestSource;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
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

    private static final Map<String, Integer> testsWithErrors = new HashMap<>();
    private static final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";
    private static final RDFReader ontologyReader = new RDFMultipleReader(Arrays.asList(
                                                    RDFReaderFactory.createResourceReader(resourcePrefix + "owl/ontology.ttl" ),
                                                    RDFReaderFactory.createResourceReader(resourcePrefix + "dsp/dsp_constraints.ttl" ),
                                                    RDFReaderFactory.createResourceReader(resourcePrefix + "rs/rs_constraints.ttl" )
                                            ));
    private final SchemaSource ontologySource = new SchemaSource("tests", "http://rdfunit.aksw.org",ontologyReader);
    @Before
    public void setUp() throws Exception {

        // OWL
        testsWithErrors.put("owl/OWLDISJC_Correct.ttl", 0);
        testsWithErrors.put("owl/OWLDISJC_Wrong.ttl", 6);
        testsWithErrors.put("owl/RDFSRANGE_Correct.ttl", 0);
        testsWithErrors.put("owl/RDFSRANGE_Wrong.ttl", 1);
        testsWithErrors.put("owl/RDFSRANGE-MISS_Wrong.ttl", 1);
        testsWithErrors.put("owl/RDFSRANGED_Correct.ttl", 0);
        testsWithErrors.put("owl/RDFSRANGED_Wrong.ttl", 2);
        testsWithErrors.put("owl/INVFUNC_Correct.ttl", 0);
        testsWithErrors.put("owl/INVFUNC_Wrong.ttl", 2);
        testsWithErrors.put("owl/OWLCARDT_Correct.ttl", 0);
        testsWithErrors.put("owl/OWLCARDT_Wrong_Exact.ttl", 6);
        testsWithErrors.put("owl/OWLCARDT_Wrong_Min.ttl", 2);
        testsWithErrors.put("owl/OWLCARDT_Wrong_Max.ttl", 2);
        testsWithErrors.put("owl/RDFLANGSTRING_Correct.ttl", 0);
        testsWithErrors.put("owl/RDFLANGSTRING_Wrong.ttl", 2);

        // DSP
        testsWithErrors.put("dsp/standalone_class_Correct.ttl", 0);
        testsWithErrors.put("dsp/standalone_class_Wrong.ttl", 1);
        testsWithErrors.put("dsp/property_cardinality_Correct.ttl", 0);
        testsWithErrors.put("dsp/property_cardinality_Wrong.ttl", 5);
        testsWithErrors.put("dsp/valueClass_Correct.ttl", 0);
        testsWithErrors.put("dsp/valueClass_Wrong.ttl", 1);
        testsWithErrors.put("dsp/valueClass-miss_Wrong.ttl", 1);

        // Resource Shapes
        testsWithErrors.put("rs/valueType_Correct.ttl", 0);
        testsWithErrors.put("rs/valueType_Wrong.ttl", 11);
        testsWithErrors.put("rs/occurs_Correct.ttl", 0);
        testsWithErrors.put("rs/occurs_Wrong.ttl", 4);
        testsWithErrors.put("rs/range_Correct.ttl", 0);
        testsWithErrors.put("rs/range_Wrong.ttl", 2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPatterns() throws Exception {

        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();

        TestSuite testSuite = new TestSuite(
                TestUtils.instantiateTestsFromAG(rdfUnit.getAutoGenerators(), ontologySource));

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        for (Map.Entry<String, Integer> entry : testsWithErrors.entrySet()) {
            String resource = entry.getKey();
            int errors = testsWithErrors.get(resource);

            // Test all execution types
            long failedTestCases = -1;
            for (TestCaseExecutionType executionType : TestCaseExecutionType.values()) {

                // create new dataset for current entry
                final Source modelSource = new DumpTestSource(
                        "test", // prefix
                        resource,
                        RDFReaderFactory.createResourceReader(resourcePrefix + resource),
                        Arrays.asList( ontologySource)
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