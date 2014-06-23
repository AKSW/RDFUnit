package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

/**
 * The type RLOG test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM
 */
public class RLOGTestCaseResult extends TestCaseResult {

    private final String resource;
    private final String message;
    private final RLOGLevel logLevel;

    /**
     * Instantiates a new RLOG test case result.
     *
     * @param testCase the test case
     * @param resource the resource
     * @param message the message
     * @param logLevel the log level
     */
    public RLOGTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase);

        this.resource = resource;
        this.message = message;
        this.logLevel = logLevel;
    }

    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rut") + "RLOGTestCaseResult"))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rlog") + "Entry"))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "resource"), model.createResource(getResource()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "message"), getMessage())
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "level"), model.createResource(getLogLevel().getUri()))
                ;
    }

    /**
     * Gets the resource.
     *
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the log level.
     *
     * @return the log level
     */
    public RLOGLevel getLogLevel() {
        return logLevel;
    }
}
