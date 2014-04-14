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
 * Created: 2/2/14 3:57 PM
 */
public class ExtendedTestCaseResult extends RLOGTestCaseResult {

    private final java.util.Collection <ResultAnnotation> resultAnnotations;

    public ExtendedTestCaseResult(TestCase testCase, String resource, String message, String logLevel) {
        super(testCase, resource, message, logLevel);
        this.resultAnnotations = testCase.getResultAnnotations();
    }

    public ExtendedTestCaseResult(TestCase testCase, RLOGTestCaseResult rlogResult) {
        super(testCase, rlogResult.getResource(), rlogResult.getMessage(), rlogResult.getLogLevel());
        this.resultAnnotations = testCase.getResultAnnotations();
    }

    @Override
    public Resource serialize(Model model, String sourceURI) {
        Resource resource = super.serialize(model, sourceURI)
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("rut") + "ExtendedTestCaseResult"));

        for (ResultAnnotation annotation : resultAnnotations) {
            resource.addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "resultAnnotation"), annotation.serializeAsTestCase(model));
        }

        return resource;
    }

}
