package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

import java.util.Calendar;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 */
public abstract class TestCaseResult {
    private final TestCase testCase;
    private final XSDDateTime timestamp;

    /**
     * Constructor
     *
     * @param testCase the test case this result is erlated with
     */
    protected TestCaseResult(TestCase testCase) {
        this.testCase = testCase;
        this.timestamp = new XSDDateTime(Calendar.getInstance());
    }

    /**
     * Serializes a TestCaseResult in a model. Subclasses should call super.serialize() and add all additional triples
     *
     * @param model            The @Model we want to write to
     * @param testExecutionURI The test execution URI for the current execution
     * @return a @Resource with all the triples for this @TestCaseResult
     */
    public Resource serialize(Model model, String testExecutionURI) {
        return model.createResource(testExecutionURI + "/" + JenaUUID.generate().asString())
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:TestCaseResult")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:wasGeneratedBy")), model.createResource(testExecutionURI))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testCase")), model.createResource(getTestCase().getTestURI()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("dcterms:date")), model.createTypedLiteral(this.getTimestamp(), XSDDatatype.XSDdateTime))
                ;
    }

    /**
     * Gets the associated test case.
     *
     * @return the test case
     */
    public TestCase getTestCase() {
        return testCase;
    }

    /**
     * Gets the result timestamp.
     *
     * @return the timestamp
     */
    public XSDDateTime getTimestamp() {
        return timestamp;
    }
}
