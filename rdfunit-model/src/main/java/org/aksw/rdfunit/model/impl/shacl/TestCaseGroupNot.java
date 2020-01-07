package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
 * Implements the logical constraint sh:not
 */
@EqualsAndHashCode(exclude={"resource"})
public class TestCaseGroupNot implements TestCaseGroup {

    private final ShapeTarget target;
    private final Shape shape;
    private final Resource resource;
    private final ImmutableList<TargetBasedTestCase> testCases;
    private final AlwaysFailingTestCase alwaysFailingTest;

    public TestCaseGroupNot(@NonNull List<? extends TargetBasedTestCase> testCases, Shape shape) {
        assert(testCases.size() == 1);  //TODO this is wrong, rather sh:not needs to be implemented as a negated sh:and
        this.shape = shape;
        this.target = testCases.iterator().next().getTarget();
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
        this.alwaysFailingTest = new AlwaysFailingTestCase(this.target);
        this.testCases = ImmutableList.of(testCases.iterator().next(), alwaysFailingTest); // adding always failing test
    }

    @Override
    public List<TargetBasedTestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.not;
    }

    @Override
    public Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults) {
        ImmutableSet.Builder<TestCaseResult> res = ImmutableSet.builder();
        TestCaseGroup.groupInternalResults(internalResults).forEach((focusNode, valueMap) -> {
            valueMap.forEach((value, results) ->{
                if(results.size() == 1 && results.get(0).getTestCaseUri().equals(this.alwaysFailingTest.getElement())){   // only the "always failing test" failed -> not constraint failed
                    PropertyValuePairSet pvs = TestCaseGroup.convertResultAnnotations(getTestCaseAnnotation(), focusNode);
                    res.add(new ShaclTestCaseGroupResult(
                            this.resource,
                            this.getLogLevel(),
                            "A sh:not constraint did not hold, since it did not encounter any failure.",
                            focusNode,
                            Collections.emptyList(),    // no we wont pass on the result of the AlwaysFailingTestCase
                            pvs.getAnnotations()
                            ));
                }
                else if(results.isEmpty()){
                    throw new RuntimeException("An unexpected result set for sh:not logical constraint was returned.");
                }
                // else the the expected number of failures were encountered (2) which we will omit from the result set.
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
