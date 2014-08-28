package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.RDFReader;
import org.aksw.rdfunit.io.RDFReaderFactory;
import org.aksw.rdfunit.tests.results.DatasetOverviewResults;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class PatternIntegrationTest {

    private static final Map<String, Integer> testsWithErrors = new HashMap<>();
    private static final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";



    @Before
    public void setUp() throws Exception {
        // /*
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

        /*
        testsWithErrors.put("dsp/standalone_class_Correct.ttl", 0);
        testsWithErrors.put("dsp/standalone_class_Wrong.ttl", 1);
        testsWithErrors.put("dsp/property_cardinality_Correct.ttl", 0);
        testsWithErrors.put("dsp/property_cardinality_Wrong.ttl", 5);
        testsWithErrors.put("dsp/valueClass_Correct.ttl", 0);
        testsWithErrors.put("dsp/valueClass_Wrong.ttl", 1);
        testsWithErrors.put("dsp/valueClass-miss_Wrong.ttl", 1);

        */
        // Load test ontology from resource
        RDFUnitStaticWrapper.initWrapper("", resourcePrefix + "owl/ontology.ttl");
        //RDFUnitStaticWrapper.initWrapper("", resourcePrefix + "dsp/dsp_constraints.ttl");

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPatterns() throws Exception {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        for (Map.Entry<String, Integer> entry : testsWithErrors.entrySet()) {
            String resource = entry.getKey();
            int errors = testsWithErrors.get(resource);

            // Test all execution types
            long failedTestCases = -1;
            for (TestCaseExecutionType executionType : TestCaseExecutionType.values()) {

                RDFReader reader = RDFReaderFactory.createResourceReader(resourcePrefix + resource);
                RDFUnitStaticWrapper.validate(reader.read(), executionType, resourcePrefix + resource, overviewResults);

                // For status results we don't get violation instances
                if (!executionType.equals(TestCaseExecutionType.statusTestCaseResult)) {
                    assertEquals(executionType + ": Errors not as expected for " + resource, errors, overviewResults.getIndividualErrors());
                }

                if (failedTestCases == -1) {
                    failedTestCases = overviewResults.getFailedTests();
                }
                else {
                    assertEquals(executionType + ": Failed test cases not as expected for " + resource, failedTestCases, overviewResults.getFailedTests());
                }
            }

        }
    }
}