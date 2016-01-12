package org.aksw.rdfunit.model.interfaces.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.impl.results.TestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;

/**
 * The Simplefied SHACL result that contains only message, severity and focusNode
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM
 * @version $Id: $Id
 */
public class SimpleShaclTestCaseResult extends TestCaseResultImpl {

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
    public SimpleShaclTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase);

        this.resource = resource;
        this.message = message;
        this.logLevel = logLevel;
    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, SHACL.ValidationResult)
                .addProperty(SHACL.focusNode, model.createResource(getResource()))    //TODO double check later, might not always be the current resource
                .addProperty(SHACL.message, getMessage())
                .addProperty(SHACL.severity, model.createResource(getLogLevel().getUri()))
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
