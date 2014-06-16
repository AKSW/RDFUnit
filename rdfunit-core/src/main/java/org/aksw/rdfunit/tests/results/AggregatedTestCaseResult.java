package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.rdfunit.tests.TestCase;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/2/14 3:44 PM
 */
public class AggregatedTestCaseResult extends StatusTestCaseResult {
    private final long errorCount;
    private final long prevalenceCount;

    public AggregatedTestCaseResult(TestCase testCase, long errorCount, long prevalenceCount) {
        super(testCase, TestCaseResultStatus.resolve(errorCount));
        this.errorCount = errorCount;
        this.prevalenceCount = prevalenceCount;
    }

    public AggregatedTestCaseResult(TestCase testCase, TestCaseResultStatus status, long errorCount, long prevalenceCount) {
        super(testCase, status);
        this.errorCount = errorCount;
        this.prevalenceCount = prevalenceCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public long getPrevalenceCount() {
        return prevalenceCount;
    }

    @Override
    public Resource serialize(Model model, String sourceURI) {
        return super.serialize(model, sourceURI)
                .addProperty(RDF.type, model.createResource(PrefixService.getNSFromPrefix("rut") + "AggregatedTestResult"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getNSFromPrefix("rut"), "resultCount"),
                        ResourceFactory.createTypedLiteral("" + errorCount, XSDDatatype.XSDinteger))
                .addProperty(ResourceFactory.createProperty(PrefixService.getNSFromPrefix("rut"), "resultPrevalence"),
                        ResourceFactory.createTypedLiteral("" + prevalenceCount, XSDDatatype.XSDinteger));
    }

    @Override
    public String toString() {
        return "Errors: " + errorCount + " / Prevalence: " + prevalenceCount + ". Test: " + getTestCase().getTestURI();
    }
}
