package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 2/2/14 3:28 PM
 */
public class RLOGTestCaseResult extends TestCaseResult {

    private final String resource;
    private final String message;
    private final RLOGLevel logLevel;

    public RLOGTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase);

        this.resource = resource;
        this.message = message;
        this.logLevel = logLevel;
    }

    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rut") + "RLOGTestCaseResult"))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rlog") + "Entry"))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "resource"), model.createResource(getResource()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "message"), getMessage())
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "level"), model.createResource(getLogLevel().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rlog"), "date"), model.createTypedLiteral(this.getTimestamp(), XSDDatatype.XSDdateTime))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("prov"), "wasGeneratedBy"), model.createResource(testExecutionURI))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "testCase"), model.createResource(getTestCase().getTestURI()))
                ;
    }

    public String getResource() {
        return resource;
    }

    public String getMessage() {
        return message;
    }

    public RLOGLevel getLogLevel() {
        return logLevel;
    }
}
