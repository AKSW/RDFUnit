package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;
import java.util.Set;

/**
 * Represents all child shapes of an sh:NodeShape, which are logically like an sh:and but returns a different result message
 */
public class TestCaseGroupNode extends TestCaseGroupAnd {
    public TestCaseGroupNode(Set<? extends TargetBasedTestCase> testCases, Shape shape) {
        super(testCases, shape);
    }

    @Override
    void addSummaryResult(ImmutableSet.Builder<TestCaseResult> builder, RDFNode focusNode, List<TestCaseResult> results) {
        builder.addAll(results);
    }
}
