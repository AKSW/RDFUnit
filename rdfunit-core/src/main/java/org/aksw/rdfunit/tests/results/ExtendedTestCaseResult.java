package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestCase;

import java.util.*;


/**
 * The type Extended test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:57 PM
 */
public class ExtendedTestCaseResult extends RLOGTestCaseResult {

    private final Map<ResultAnnotation, Set<RDFNode>> variableAnnotationsMap;

    /**
     * Instantiates a new Extended test case result.
     *
     * @param testCase the test case
     * @param rlogResult the rlog result
     */
    public ExtendedTestCaseResult(TestCase testCase, RLOGTestCaseResult rlogResult) {
        this(testCase, rlogResult.getResource(), rlogResult.getMessage(), rlogResult.getLogLevel());
    }

    /**
     * Instantiates a new Extended test case result.
     *
     * @param testCase the test case
     * @param resource the resource
     * @param message the message
     * @param logLevel the log level
     */
    public ExtendedTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase, resource, message, logLevel);
        this.variableAnnotationsMap = createMap();
    }


    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        Resource resource = super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:ExtendedTestCaseResult")))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("spin:ConstraintViolation")))
                .addProperty(model.createProperty(PrefixNSService.getURIFromAbbrev("spin:violationRoot")), model.createResource(getResource()));

        for (ResultAnnotation annotation : getTestCase().getResultAnnotations()) {
            annotation.serializeAsResult(resource, model);
        }

        for (Map.Entry<ResultAnnotation, Set<RDFNode>> vaEntry : variableAnnotationsMap.entrySet()) {
            for (RDFNode rdfNode: vaEntry.getValue()) {
                resource.addProperty(ResourceFactory.createProperty(vaEntry.getKey().getAnnotationProperty()), rdfNode);
            }
        }

        return resource;
    }

    public Map<ResultAnnotation, Set<RDFNode>> getVariableAnnotationsMap() {
        return variableAnnotationsMap;
    }

    private Map<ResultAnnotation, Set<RDFNode>> createMap() {
        Map<ResultAnnotation, Set<RDFNode>> vaMap = new HashMap<>();

        Collection<ResultAnnotation> variableAnnotations = getTestCase().getVariableAnnotations();
        for (ResultAnnotation annotation : variableAnnotations) {
            vaMap.put(annotation, new HashSet<RDFNode>());
        }

        return Collections.unmodifiableMap(vaMap);
    }
}
