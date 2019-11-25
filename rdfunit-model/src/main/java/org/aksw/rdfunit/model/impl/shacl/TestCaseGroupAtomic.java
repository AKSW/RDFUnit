package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;
import java.util.Set;

/**
 * The default TestCaseGroup wrapping a single test case, having the same behaviour as an sh:and with a single test
 * Will not add a validation result
 */
public class TestCaseGroupAtomic extends TestCaseGroupAnd {

    public TestCaseGroupAtomic(@NonNull Set<? extends TargetBasedTestCase> testCases, Shape shape) {
        super(testCases, shape);
        assert(testCases.size() == 1);
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.atomic;
    }

    @Override
    void addSummaryResult(ImmutableSet.Builder<TestCaseResult> builder, RDFNode focusNode, List<TestCaseResult> results) {
        builder.addAll(results);    // we just pass through internal results
    }
}
