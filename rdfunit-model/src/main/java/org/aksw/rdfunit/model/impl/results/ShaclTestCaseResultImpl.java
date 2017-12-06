package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
@EqualsAndHashCode(exclude = "element", callSuper = false)
public class ShaclTestCaseResultImpl extends ShaclLiteTestCaseResultImpl implements ShaclTestCaseResult {

    private final ImmutableSet<PropertyValuePair> resultAnnotations;


    public ShaclTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, RDFNode focusNode, Set<PropertyValuePair> resultAnnotations) {
        super(testCaseUri, severity, message, focusNode);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    public ShaclTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, RDFNode focusNode, Set<PropertyValuePair> resultAnnotations) {
        super(element, testCaseUri, severity, message, timestamp, focusNode);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    private ShaclTestCaseResultImpl(Builder builder) {
        this(builder.shaclLiteTestCaseResult.getTestCaseUri(),
                builder.shaclLiteTestCaseResult.getSeverity(),
                builder.shaclLiteTestCaseResult.getMessage(),
                builder.shaclLiteTestCaseResult.getFailingNode(),
                builder.resultAnnotations);
    }

    @Override
    public Set<PropertyValuePair> getResultAnnotations() {
        return resultAnnotations;
    }

    public static class Builder {
        private ShaclLiteTestCaseResult shaclLiteTestCaseResult;
        private Set<PropertyValuePair> resultAnnotations;

        public Builder(String testCaseUri, RLOGLevel severity, String message, RDFNode focusNode) {
            shaclLiteTestCaseResult = new ShaclLiteTestCaseResultImpl(testCaseUri, severity, message, focusNode);
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