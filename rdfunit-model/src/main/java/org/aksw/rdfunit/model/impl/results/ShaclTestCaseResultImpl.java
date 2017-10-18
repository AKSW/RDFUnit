package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
@EqualsAndHashCode(exclude = "element", callSuper = false)
public class ShaclTestCaseResultImpl extends ShaclLiteTestCaseResultImpl implements ShaclTestCaseResult {

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
        this(builder.shaclLiteTestCaseResult.getTestCaseUri(),
                builder.shaclLiteTestCaseResult.getSeverity(),
                builder.shaclLiteTestCaseResult.getMessage(),
                builder.shaclLiteTestCaseResult.getFailingResource(),
                builder.resultAnnotations);
    }

    @Override
    public Set<PropertyValuePair> getResultAnnotations() {
        return resultAnnotations;
    }

    public static class Builder {
        private ShaclLiteTestCaseResult shaclLiteTestCaseResult;
        private Set<PropertyValuePair> resultAnnotations;

        public Builder(String testCaseUri, RLOGLevel severity, String message, String resource) {
            shaclLiteTestCaseResult = new ShaclLiteTestCaseResultImpl(testCaseUri, severity, message, resource);
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