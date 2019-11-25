package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
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
import java.util.stream.Stream;

/**
 * Implements the logical constraint sh:xone
 */
public class TestCaseGroupXone implements TestCaseGroup {

    private final Shape shape;
    private final Resource resource;
    private final ShapeTarget target;
    private final ImmutableSet<TargetBasedTestCase> testCases;
    private final AlwaysFailingTestCase alwaysFailingTest;

    public TestCaseGroupXone(@NonNull Set<? extends TargetBasedTestCase> testCases, Shape shape) {
        assert(! testCases.isEmpty());
        this.shape = shape;
        target = testCases.iterator().next().getTarget();
        assert(testCases.stream().map(TargetBasedTestCase::getTarget).noneMatch(x -> x != target));
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
        this.alwaysFailingTest = new AlwaysFailingTestCase(this.target);
        this.testCases = ImmutableSet.copyOf(Stream.concat(testCases.stream(), Stream.of(alwaysFailingTest)).collect(Collectors.toSet())); // adding always failing test
    }

    @Override
    public Set<TargetBasedTestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.xone;
    }

    @Override
    public Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults) {
        ImmutableSet.Builder<TestCaseResult> res = ImmutableSet.builder();
        TestCaseGroup.groupInternalResults(internalResults).forEach((focusNode, valueMap) -> {
            valueMap.forEach((value, results) ->{
                if(testCases.size() - results.size() != 1) {    // expecting exactly one correct test
                    PropertyValuePairSet pvs = TestCaseGroup.convertResultAnnotations(getTestCaseAnnotation(), focusNode);
                    res.add(new ShaclTestCaseGroupResult(
                            this.resource,
                            this.getLogLevel(),
                            "More than one or all test case failed inside a sh:xone constraint.",
                            focusNode,
                            // filter out the AlwaysFailingTestCase
                            results.stream().filter(r -> ! r.getTestCaseUri().equals(alwaysFailingTest.getElement())).collect(Collectors.toList()),
                            pvs.getAnnotations()
                    ));
                }
                //else we ignore all internal errors, since exactly one was successful
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
