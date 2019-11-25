package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseGroupResult;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseGroup;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements the logical constraint sh:and
 */
public class TestCaseGroupAnd implements TestCaseGroup {

    private final ShapeTarget target;
    private final Shape shape;
    private final Resource resource;
    private final ImmutableSet<TargetBasedTestCase> testCases;

    public TestCaseGroupAnd(@NonNull Set<? extends TargetBasedTestCase> testCases, Shape shape) {
        assert(! testCases.isEmpty());
        target = testCases.iterator().next().getTarget();
        assert(testCases.stream().map(TargetBasedTestCase::getTarget).noneMatch(x -> x != target));

        this.shape = shape;
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
        this.testCases = ImmutableSet.copyOf(testCases);
    }

    public boolean isAtomic(){ return testCases.size() == 1; }

    @Override
    public Set<TargetBasedTestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.and;
    }

    @Override
    public Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults) {
        ImmutableSet.Builder<TestCaseResult> res = ImmutableSet.builder();
        TestCaseGroup.groupInternalResults(internalResults).forEach((focusNode, valueMap) -> {
            valueMap.forEach((value, results) ->{
                addSummaryResult(res, focusNode, results);
            });
        });
        return res.build();
    }

    @Override
    public Shape getPertainingShape() {
        return this.shape;
    }

    void addSummaryResult(ImmutableSet.Builder<TestCaseResult> builder, RDFNode focusNode, List<TestCaseResult> results){
        PropertyValuePairSet pvs = TestCaseGroup.convertResultAnnotations(getTestCaseAnnotation(), focusNode);
        builder.add(new ShaclTestCaseGroupResult(
                this.resource,
                this.getLogLevel(),
                "At least one test case failed inside a sh:and constraint.",
                focusNode,
                results,
                pvs.getAnnotations()
        ));
    }

    private TestCaseAnnotation testCaseAnno;
    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        if(testCaseAnno == null)
            testCaseAnno = TestCaseGroup.getTestCaseAnnotation(this.resource, this.shape, getLogicalOperator());

        return testCaseAnno;
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return testCases.stream().flatMap(t -> t.getPrefixDeclarations().stream()).collect(Collectors.toSet());
    }

    @Override
    public Resource getElement() {
        return this.resource;
    }

    @Override
    public ShapeTarget getTarget() {
        return target;
    }
}
