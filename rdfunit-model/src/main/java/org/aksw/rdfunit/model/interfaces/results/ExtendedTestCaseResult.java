package org.aksw.rdfunit.model.interfaces.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * The type Extended test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:57 PM
 * @version $Id: $Id
 */
public class ExtendedTestCaseResult extends RLOGTestCaseResult {

    private final Map<ResultAnnotation, Set<RDFNode>> variableAnnotationsMap;

    /**
     * Instantiates a new Extended test case result.
     *
     * @param testCase   the test case
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
     * @param message  the message
     * @param logLevel the log level
     */
    public ExtendedTestCaseResult(TestCase testCase, String resource, String message, RLOGLevel logLevel) {
        super(testCase, resource, message, logLevel);
        checkArgument(getTestCase().isPresent(), "Cannot instantiate an ExtendedTestCaseResult without a TestCase object");
        this.variableAnnotationsMap = createMap();
    }


    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        Resource resource = super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, RDFUNITv.ExtendedTestCaseResult)
                .addProperty(RDF.type, SHACL.ValidationResult)
                .addProperty(SHACL.focusNode, model.createResource(getResource()));

        for (Map.Entry<ResultAnnotation, Set<RDFNode>> vaEntry : variableAnnotationsMap.entrySet()) {
            for (RDFNode rdfNode : vaEntry.getValue()) {
                resource.addProperty(vaEntry.getKey().getAnnotationProperty(), rdfNode);
            }
        }

        return resource;
    }

    /**
     * <p>Getter for the field <code>variableAnnotationsMap</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<ResultAnnotation, Set<RDFNode>> getVariableAnnotationsMap() {
        return variableAnnotationsMap;
    }

    private Map<ResultAnnotation, Set<RDFNode>> createMap() {
        Map<ResultAnnotation, Set<RDFNode>> vaMap = new HashMap<>();

        Collection<ResultAnnotation> variableAnnotations = getTestCase().get().getVariableAnnotations();
        for (ResultAnnotation annotation : variableAnnotations) {
            vaMap.put(annotation, new HashSet<RDFNode>());
        }

        return Collections.unmodifiableMap(vaMap);
    }
}
