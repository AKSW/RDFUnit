package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.resources.Resources;
import org.aksw.rdfunit.utils.CacheUtils;
import org.aksw.rdfunit.utils.TestUtils;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.fail;

public class ManualTestsIntegrationTest {

    @Before
    public void setUp() throws Exception {

        String emptyResource = "/org/aksw/rdfunit/validate/data/empty.ttl";

        // Load test ontology from resource (empty in this case)
        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder().addLocalResource("custom", emptyResource).build());

        RDFUnit rdfunit = new RDFUnit();
        try {
            rdfunit.init();
        } catch (RDFReaderException e) {
            fail("Cannot read patterns and/or pattern generators");
        }

    }

    @Test
    public void testManualTestCases() throws Exception {

        for (Map.Entry<String, String> entry : Resources.getInstance().entrySet()) {
            String prefix = entry.getKey();
            String uri = entry.getValue();
            String resource = "/org/aksw/rdfunit/tests/" + "Manual/" + CacheUtils.getCacheFolderForURI(uri) + prefix + "." + "tests" + "." + "Manual" + ".ttl";
            try {
                TestUtils.instantiateTestsFromModel(RDFReaderFactory.createResourceReader(resource).read(), true);
            } catch (RDFReaderException e) {
                fail("Cannot read resource: " + resource + " (" + prefix + " - " + uri + ")");
            }

        }
    }
}