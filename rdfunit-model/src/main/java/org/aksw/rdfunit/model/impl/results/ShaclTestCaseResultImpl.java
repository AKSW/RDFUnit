package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


public class ShaclTestCaseResultImpl extends SimpleShaclTestCaseResultImpl implements ShaclTestCaseResult{

    private final ImmutableSet<PropertyValuePair> resultAnnotations;


    public ShaclTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource, Set<PropertyValuePair> resultAnnotations) {
        super(testCaseUri, severity, message, resource);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    public ShaclTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, String resource, Set<PropertyValuePair> resultAnnotations) {
        super(element, testCaseUri, severity, message, timestamp, resource);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    private ShaclTestCaseResultImpl(Builder builder) {
        this(builder.rlogTestCaseResult.getTestCaseUri(),
                builder.rlogTestCaseResult.getSeverity(),
                builder.rlogTestCaseResult.getMessage(),
                builder.rlogTestCaseResult.getFailingResource(),
                builder.resultAnnotations);
    }

    public Set<PropertyValuePair> getResultAnnotations() {
        return resultAnnotations;
    }

    public static class Builder {
        private RLOGTestCaseResult rlogTestCaseResult;
        private Set<PropertyValuePair> resultAnnotations;

        public Builder(String testCaseUri, RLOGLevel severity, String message, String resource) {
            rlogTestCaseResult = new RLOGTestCaseResultImpl(testCaseUri, severity, message, resource);
        }

        public Builder setResultAnnotations(Set<PropertyValuePair> annotations) {
            this.resultAnnotations = annotations;
            return this;
        }

        public ShaclTestCaseResultImpl build() {
            return new ShaclTestCaseResultImpl(this);
        }
    }
}

    /*
    public Resource serialize(Model model, String testExecutionURI) {
        Resource resource = super.serialize(model, testExecutionURI)
            .addProperty(SHACL.subject, model.createResource(getElement()));


        for (Map.Entry<ResultAnnotation, Set<RDFNode>> vaEntry : variableAnnotationsMap.entrySet()) {
            for (RDFNode rdfNode : vaEntry.getValue()) {
                resource.addProperty(vaEntry.getKey().getAnnotationProperty(), rdfNode);
            }
        }

        return resource;
    }
      */

