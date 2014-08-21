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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class PatternIntegrationTest {

    private final Map<String, Integer> testsWithErrors = new HashMap<>();
    private final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";



    @Before
    public void setUp() throws Exception {
        testsWithErrors.put("OWLDISJC_Correct.ttl", 0);
        testsWithErrors.put("OWLDISJC_Wrong.ttl", 6);

        // Load test ontology from resource
        RDFUnitStaticWrapper.initWrapper("", resourcePrefix + "ontology.ttl");

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPatterns() throws Exception {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        for (String resource : testsWithErrors.keySet()) {
            int errors = testsWithErrors.get(resource);

            RDFReader reader = RDFReaderFactory.createResourceReader(resourcePrefix + resource);
            RDFUnitStaticWrapper.validate(reader.read(), TestCaseExecutionType.rlogTestCaseResult, resourcePrefix + resource, overviewResults);

            assertEquals("Errors not as expected for " + resource, errors, overviewResults.getIndividualErrors() );

        }
    }
}