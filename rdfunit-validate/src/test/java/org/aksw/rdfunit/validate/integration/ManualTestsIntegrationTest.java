package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.io.RDFReader;
import org.aksw.rdfunit.io.RDFReaderFactory;
import org.aksw.rdfunit.resources.Resources;
import org.aksw.rdfunit.tests.results.DatasetOverviewResults;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class ManualTestsIntegrationTest {

    private static final Map<String, Integer> testsWithErrors = new HashMap<>();
    private static final String emptyResource = "/org/aksw/rdfunit/validate/data/empty.ttl";



    @Before
    public void setUp() throws Exception {

        // Load test ontology from resource (empty in this case)
        RDFUnitStaticWrapper.initWrapper("", emptyResource);

        RDFReader patternReader = RDFUnitUtils.getPatternsFromResource();
        RDFReader testGeneratorReader = RDFUnitUtils.getAutoGeneratorsFromResource();
        RDFUnit rdfunit = new RDFUnit();
        try {
            rdfunit.initPatternsAndGenerators(patternReader, testGeneratorReader);
        } catch (TripleReaderException e) {
            fail("Cannot read patterns and/or pattern generators");
        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testManualTestCases() throws Exception {

        DatasetOverviewResults overviewResults = new DatasetOverviewResults();

        for (Map.Entry<String,String> entry : Resources.getInstance().entrySet() ) {
            String prefix = entry.getKey();
            String uri = entry.getValue();
            String resource = "/org/aksw/rdfunit/tests/" + "Manual/" + CacheUtils.getCacheFolderForURI(uri) + prefix + "." + "tests" + "." + "Manual" + ".ttl";
            try {
                TestUtils.instantiateTestsFromModel(RDFReaderFactory.createResourceReader(resource).read());
            } catch (TripleReaderException e) {
                fail("Cannot read resource: " + resource + " (" + prefix + " - " + uri + ")");
            }

        }
    }
}