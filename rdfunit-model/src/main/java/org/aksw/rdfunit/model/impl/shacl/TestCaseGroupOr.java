package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseGroupResult;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseGroup;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the logical constraint sh:or
 */
@EqualsAndHashCode(exclude={"resource"})
public class TestCaseGroupOr implements TestCaseGroup {

    private final ShapeTarget target;
    private final Shape shape;
    private final Resource resource;
    private final ImmutableList<TargetBasedTestCase> testCases;

    public TestCaseGroupOr(@NonNull List<? extends TargetBasedTestCase> testCases, Shape shape) {
        assert(! testCases.isEmpty());
        this.shape = shape;
        target = testCases.iterator().next().getTarget();
        assert(testCases.stream().map(TargetBasedTestCase::getTarget).noneMatch(x -> x != target));
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
        this.testCases = ImmutableList.copyOf(testCases);
    }

    @Override
    public List<TargetBasedTestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.or;
    }

    @Override
    public Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults) {
        ImmutableList.Builder<TestCaseResult> res = ImmutableList.builder();
        TestCaseGroup.groupInternalResults(internalResults).forEach((focusNode, valueMap) -> {
            valueMap.forEach((value, results) ->{
                if(results.size() == this.testCases.size()) {
                    PropertyValuePairSet pvs = TestCaseGroup.convertResultAnnotations(getTestCaseAnnotation(), focusNode);
                    res.add(new ShaclTestCaseGroupResult(
                            this.resource,
                            this.getLogLevel(),
                            "All test case failed inside a sh:or constraint.",
                            focusNode,
                            results,
                            pvs.getAnnotations()
                    ));
                }
                //else we ignore all internal errors, since at least one was successful
            });
        });
        return res.build();
    }

    @Override
    public Shape getPertainingShape() {
        return this.shape;
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
