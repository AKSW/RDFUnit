package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

import java.util.Calendar;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/2/14 3:44 PM
 */
public abstract class TestCaseResult {
    private final TestCase testCase;
    private final XSDDateTime timestamp;

    protected TestCaseResult(TestCase testCase) {
        this.testCase = testCase;
        this.timestamp = new XSDDateTime(Calendar.getInstance());
    }

    /**
     * Serializes a @TestCaseResult in a model. Subclasses should call super.serialize() and add all additional triples
     *
     * @param model The @Model we want to write to
     * @param testExecutionURI The test execution URI for the current execution
     * @return a @Resource with all the triples for this @TestCaseResult
     */
    public Resource serialize(Model model, String testExecutionURI) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixNSService.getNSFromPrefix("rut") + "TestCaseResult"))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("prov"), "wasGeneratedBy"), model.createResource(testExecutionURI))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "testCase"), model.createResource(getTestCase().getTestURI()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("dcterms"), "date"), model.createTypedLiteral(this.getTimestamp(), XSDDatatype.XSDdateTime))
                ;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public XSDDateTime getTimestamp() {
        return timestamp;
    }
}
