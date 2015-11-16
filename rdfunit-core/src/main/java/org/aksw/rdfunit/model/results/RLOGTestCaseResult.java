package org.aksw.rdfunit.model.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.RLOG;

/**
 * The type RLOG test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM
 * @version $Id: $Id
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
     * @param message  the message
     * @param logLevel the log level
     */
    public RLOGTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase);

        this.resource = resource;
        this.message = message;
        this.logLevel = logLevel;
    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, RDFUNITv.RLOGTestCaseResult)
                .addProperty(RDF.type, RLOG.Entry)
                .addProperty(RLOG.resource, model.createResource(getResource()))
                .addProperty(RLOG.message, getMessage())
                .addProperty(RLOG.level, model.createResource(getLogLevel().getUri()))
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
