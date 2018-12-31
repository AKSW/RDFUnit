package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseGroupResult;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;
import java.util.Set;

public class TestCaseGroupAtomic extends TestCaseGroupAnd {

    public TestCaseGroupAtomic(@NonNull Set<? extends GenericTestCase> testCases) {
        super(testCases);
        assert(testCases.size() == 1);
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.atomic;
    }

    @Override
    void addSummaryResult(ImmutableSet.Builder<TestCaseResult> builder, RDFNode focusNode, List<TestCaseResult> results) {
        builder.add(new ShaclTestCaseGroupResult(
                this.getElement(),
                this.getLogLevel(),
                "Atomic Test Group failed.",    //FIXME this test result is redundant
                focusNode,
                results));
    }
}
