package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.SimpleAnnotation;
import org.aksw.rdfunit.model.interfaces.results.ExtendedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


@Deprecated
public class ExtendedTestCaseResultImpl extends RLOGTestCaseResultImpl implements ExtendedTestCaseResult {

    private final ImmutableSet<SimpleAnnotation> resultAnnotations;

    public ExtendedTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource, Set<SimpleAnnotation> resultAnnotations) {
        super(testCaseUri, severity, message, resource);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    public ExtendedTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, String resource, Set<SimpleAnnotation> resultAnnotations) {
        super(element, testCaseUri, severity, message, timestamp, resource);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    private ExtendedTestCaseResultImpl(Builder builder) {
        this(builder.rlogTestCaseResult.getTestCaseUri(),
                builder.rlogTestCaseResult.getSeverity(),
                builder.rlogTestCaseResult.getMessage(),
                builder.rlogTestCaseResult.getFailingResource(),
                builder.resultAnnotations);
    }

    public Set<SimpleAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }

    public static class Builder {
        private RLOGTestCaseResult rlogTestCaseResult;
        private Set<SimpleAnnotation> resultAnnotations;

        public Builder(String testCaseUri, RLOGLevel severity, String message, String resource) {
            rlogTestCaseResult = new RLOGTestCaseResultImpl(testCaseUri, severity, message, resource);
        }

        public Builder setResultAnnotations(Set<SimpleAnnotation> annotations) {
            this.resultAnnotations = annotations;
            return this;
        }

        public ExtendedTestCaseResultImpl build() {
            return new ExtendedTestCaseResultImpl(this);
        }
    }
}
