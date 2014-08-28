package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

/**
 * The Status test case result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /6/14 3:26 PM
 */
public class StatusTestCaseResult extends TestCaseResult {
    private final TestCaseResultStatus status;

    /**
     * Instantiates a new Status test case result.
     *
     * @param testCase the test case
     * @param status   the status
     */
    public StatusTestCaseResult(TestCase testCase, TestCaseResultStatus status) {
        super(testCase);
        this.status = status;
    }

    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:StatusTestCaseResult")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:resultStatus")), model.createResource(getStatus().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("dcterms:description")), getTestCase().getResultMessage())
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testCaseLogLevel")), model.createResource(getTestCase().getLogLevel().getUri()))
                ;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public TestCaseResultStatus getStatus() {
        return status;
    }
}
