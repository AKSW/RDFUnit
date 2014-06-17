package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/6/14 3:26 PM
 */
public class StatusTestCaseResult extends TestCaseResult {
    private final TestCaseResultStatus status;

    public StatusTestCaseResult(TestCase testCase, TestCaseResultStatus status) {
        super(testCase);
        this.status = status;
    }

    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rut") + "StatusTestCaseResult"))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "resultStatus"), model.createResource(getStatus().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("dcterms"), "description"), getTestCase().getResultMessage())
                ;
    }

    public TestCaseResultStatus getStatus() {
        return status;
    }
}
