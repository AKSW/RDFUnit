package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.rdfunit.tests.TestCase;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 2/2/14 3:28 PM
 */
public class RLOGTestCaseResult extends TestCaseResult {

    private final String resource;
    private final String message;
    private final String logLevel;

    public RLOGTestCaseResult(TestCase testCase, String resource, String message, String logLevel) {
        super(testCase);

        this.resource = resource;
        this.message = message;
        this.logLevel = logLevel;
    }

    @Override
    public Resource serialize(Model model, String sourceURI) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "RLOGTestCaseResult"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rlog"), "resource"), model.createResource(getResource()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rlog"), "message"), getMessage())
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rlog"), "level"), model.createResource(getLogLevel()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "wasGeneratedBy"), model.createResource(sourceURI))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "testCase"), model.createResource(getTestCase().getTestURI()));
    }

    public String getResource() {
        return resource;
    }

    public String getMessage() {
        return message;
    }

    public String getLogLevel() {
        return logLevel;
    }
}
