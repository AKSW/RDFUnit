package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;


/**
 * The type Extended test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:57 PM
 */
public class ExtendedTestCaseResult extends RLOGTestCaseResult {

    private final java.util.Collection<ResultAnnotation> resultAnnotations;

    /**
     * Instantiates a new Extended test case result.
     *
     * @param testCase the test case
     * @param resource the resource
     * @param message the message
     * @param logLevel the log level
     */
    public ExtendedTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase, resource, message, logLevel);
        this.resultAnnotations = testCase.getResultAnnotations();
    }

    /**
     * Instantiates a new Extended test case result.
     *
     * @param testCase the test case
     * @param rlogResult the rlog result
     */
    public ExtendedTestCaseResult(TestCase testCase, RLOGTestCaseResult rlogResult) {
        super(testCase, rlogResult.getResource(), rlogResult.getMessage(), rlogResult.getLogLevel());
        this.resultAnnotations = testCase.getResultAnnotations();
    }

    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        Resource resource = super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rut") + "ExtendedTestCaseResult"));

        for (ResultAnnotation annotation : resultAnnotations) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "resultAnnotation"), annotation.serializeAsTestCase(model));
        }

        return resource;
    }

}
