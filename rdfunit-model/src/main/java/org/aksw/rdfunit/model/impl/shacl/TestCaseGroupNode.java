package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.val;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseGroupResult;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseGroup;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents all child shapes of an sh:NodeShape, which are logically like an sh:and but returns a different result message
 */
public class TestCaseGroupNode extends TestCaseGroupAnd {
    public TestCaseGroupNode(List<? extends TargetBasedTestCase> testCases, Shape shape) {
        super(testCases, shape);
    }

    @Override
    void addSummaryResult(ImmutableSet.Builder<TestCaseResult> builder, RDFNode focusNode, List<TestCaseResult> results) {
        val shNode = this.getPertainingShape().getPropertyValuePairSets().getPropertyValues(SHACL.node);
        if(! shNode.isEmpty()) {  // test if sh:node is present or not, if so, we summarize all results
            PropertyValuePairSet pvs = TestCaseGroup.convertResultAnnotations(getTestCaseAnnotation(), focusNode);
            builder.add(new ShaclTestCaseGroupResult(
                    this.getElement(),
                    this.getLogLevel(),
                    "At least one test case failed inside a sh:node shape.",
                    focusNode,
                    results,
                    pvs.getAnnotations()
            ));
        }
        else{
            builder.addAll(results);    // we just pass through internal results
        }
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.node;
    }
}
