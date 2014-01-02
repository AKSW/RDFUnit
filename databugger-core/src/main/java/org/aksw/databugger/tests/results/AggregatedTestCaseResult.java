package org.aksw.databugger.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.services.PrefixService;
import org.aksw.databugger.tests.TestCase;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/2/14 3:44 PM
 */
public class AggregatedTestCaseResult extends TestCaseResult {
    private final long errorCount;
    private final long prevalenceCount;

    public AggregatedTestCaseResult(TestCase testCase, long errorCount, long prevalenceCount) {
        super(testCase);
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
    public void writeToModel(Model model, String sourceURI) {
        model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "Result"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "resultCount"), "" + errorCount)
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "resultPrevalence"), "" + prevalenceCount)
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(sourceURI))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "testCase"), model.createResource(getTestCase().getTestURI()));
    }

    @Override
    public String toString() {
        return errorCount + " Errors with " + prevalenceCount + " Prevalence. Test: " + getTestCase().getTestURI();
    }
}
