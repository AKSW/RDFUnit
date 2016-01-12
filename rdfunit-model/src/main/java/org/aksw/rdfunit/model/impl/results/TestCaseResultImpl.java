package org.aksw.rdfunit.model.impl.results;

import com.google.common.base.Optional;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.Calendar;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 * @version $Id: $Id
 */
public abstract class TestCaseResultImpl implements TestCaseResult {
    private final String testCaseUri;
    private final TestCase testCase;
    private final XSDDateTime timestamp;

    /**
     * Constructor
     *
     * @param testCase the test case this result is erlated with
     */
    protected TestCaseResultImpl(TestCase testCase) {
        this.testCaseUri = testCase.getTestURI();
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
                .addProperty(RDF.type, RDFUNITv.TestCaseResult)
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:wasGeneratedBy")), model.createResource(testExecutionURI))
                .addProperty(RDFUNITv.testCase, model.createResource(getTestCaseUri()))
                .addProperty(DCTerms.date, model.createTypedLiteral(this.getTimestamp(), XSDDatatype.XSDdateTime))
                ;
    }

    public String getTestCaseUri() {
        return testCaseUri;
    }

    public Optional<TestCase> getTestCase() {
        return Optional.fromNullable(testCase);
    }

    public XSDDateTime getTimestamp() {
        return timestamp;
    }
}
