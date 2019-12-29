package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ShaclTestCaseResultImpl extends ShaclLiteTestCaseResultImpl implements ShaclTestCaseResult {

    private final ImmutableSet<PropertyValuePair> resultAnnotations;
    private final ImmutableSet<TestCaseResult> details;


    public ShaclTestCaseResultImpl(
            Resource testCaseUri,
            RLOGLevel severity,
            String message,
            RDFNode focusNode,
            Set<PropertyValuePair> resultAnnotations,
            Collection<TestCaseResult> details
    ) {
        super(testCaseUri, severity, message, focusNode);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
        this.details = ImmutableSet.copyOf(checkNotNull(details));
    }

    public ShaclTestCaseResultImpl(
            Resource testCaseUri,
            RLOGLevel severity,
            String message,
            RDFNode focusNode,
            Set<PropertyValuePair> resultAnnotations
    ) {
        super(testCaseUri, severity, message, focusNode);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
        this.details = ImmutableSet.of();
    }

    public ShaclTestCaseResultImpl(
            Resource element,
            Resource testCaseUri,
            RLOGLevel severity,
            String message,
            XSDDateTime timestamp,
            RDFNode focusNode,
            Set<PropertyValuePair> resultAnnotations
    ) {
        super(element, testCaseUri, severity, message, timestamp, focusNode);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
        this.details = ImmutableSet.of();
    }

    public ShaclTestCaseResultImpl(
            Resource element,
            Resource testCaseUri,
            RLOGLevel severity,
            String message,
            XSDDateTime timestamp,
            RDFNode focusNode,
            Set<PropertyValuePair> resultAnnotations,
            Collection<TestCaseResult> details
    ) {
        super(element, testCaseUri, severity, message, timestamp, focusNode);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
        this.details = ImmutableSet.copyOf(checkNotNull(details));
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
        if(resultAnnotations == null)
            return ImmutableSet.of();
        else
            return ImmutableSet.copyOf(this.resultAnnotations);
    }

    @Override
    public Set<TestCaseResult> getDetails() {
        return details;
    }

    public static class Builder {
        private ShaclLiteTestCaseResult shaclLiteTestCaseResult;
        private Set<PropertyValuePair> resultAnnotations;

        public Builder(Resource testCaseUri, RLOGLevel severity, String message, RDFNode focusNode) {
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


    private static ImmutableSet<org.apache.jena.rdf.model.Property> hashEqualsProperties = ImmutableSet.of(
            SHACL.sourceConstraintComponent, SHACL.focusNode, SHACL.sourceShape, SHACL.message, SHACL.value);

    /**
     * Similar to equals, this function equates two instances of [[ShaclTestCaseResultImpl]] and determines
     * if the results express the same result (disregarding the origin test case), dependent on the properties defined above.
     * This might happen, for example, if two test-cases were defined which basically look for the same violation.
     * This function helps to remove such duplicate results.
     */
    public boolean resembles(TestCaseResult tcr) {
        if(tcr instanceof ShaclTestCaseResultImpl) {
            val propertyPairs = getResultAnnotations().stream().filter(p -> hashEqualsProperties.contains(p.getProperty())).collect(Collectors.toSet());
            val hps = ((ShaclTestCaseResultImpl)tcr).getResultAnnotations().stream()
                    .filter(p -> hashEqualsProperties.contains(p.getProperty()))
                    .collect(Collectors.toMap(PropertyValuePair::getProperty, PropertyValuePair::getValues));
            if(hps.size() != propertyPairs.size())
                return false;
            return propertyPairs.stream().allMatch(x -> hps.get(x.getProperty()).containsAll(x.getValues()));
        }
        else
            return false;
    }
}
