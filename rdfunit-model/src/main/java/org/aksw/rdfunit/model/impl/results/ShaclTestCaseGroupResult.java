package org.aksw.rdfunit.model.impl.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ShaclTestCaseGroupResult extends ShaclTestCaseResultImpl {

    public ShaclTestCaseGroupResult(
            Resource testCaseUri,
            RLOGLevel severity,
            String message,
            RDFNode focusNode,
            List<TestCaseResult> internalResults,
            Set<PropertyValuePair> resultAnnotations
    ) {
        super(testCaseUri, severity, message, focusNode, resultAnnotations, internalResults);
    }

    public ShaclTestCaseGroupResult(
            Resource testCaseUri,
            RLOGLevel severity,
            String message,
            RDFNode focusNode,
            Set<ResultAnnotation> resultAnnotations,
            List<TestCaseResult> internalResults
    ) {
        this(
            testCaseUri, severity, message, focusNode, internalResults,
            resultAnnotations.stream()
                    .map(PropertyValuePair::fromAnnotation)
                    .map(o -> o.orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet())
        );
    }

    public Set<TestCaseResult> getInternalResults() {
        return getDetails();
    }
}
