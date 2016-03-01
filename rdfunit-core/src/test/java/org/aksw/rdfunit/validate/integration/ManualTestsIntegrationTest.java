package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.resources.ManualTestResources;
import org.aksw.rdfunit.utils.TestUtils;
import org.aksw.rdfunit.utils.UriToPathUtils;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.fail;

public class ManualTestsIntegrationTest {

    @Before
    public void setUp() {

        String emptyResource = "/org/aksw/rdfunit/validate/data/empty.ttl";

        // Load test ontology from resource (empty in this case)
        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder().addLocalResource("custom", emptyResource).build());

        RDFUnit rdfunit = new RDFUnit();
        try {
            rdfunit.init();
        } catch (RdfReaderException e) {
            fail("Cannot read patterns and/or pattern generators");
        }

    }

    @Test
    public void testManualTestCases() throws TestCaseInstantiationException {

        for (Map.Entry<String, String> entry : ManualTestResources.getInstance().entrySet()) {
            String prefix = entry.getKey();
            String uri = entry.getValue();
            String resource = "/org/aksw/rdfunit/tests/" + "Manual/" + UriToPathUtils.getCacheFolderForURI(uri) + prefix + "." + "tests" + "." + "Manual" + ".ttl";
            try {
                TestUtils.instantiateTestsFromModel(RdfReaderFactory.createResourceReader(resource).read(), true);
            } catch (RdfReaderException e) {
                fail("Cannot read resource: " + resource + " (" + prefix + " - " + uri + ")");
            }

        }
    }
}