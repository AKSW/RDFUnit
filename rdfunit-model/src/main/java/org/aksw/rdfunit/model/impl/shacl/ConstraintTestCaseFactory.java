package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.Constraint;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;

import java.util.Set;

@Slf4j
public final class ConstraintTestCaseFactory {

    private ConstraintTestCaseFactory() {}

    public static Set<TestCase> createFromComponentAndShape(Component component, Shape shape, Set<ShapeTarget> shapeTargets) {
        ImmutableSet.Builder<TestCase> testCaseBuilder = ImmutableSet.builder();

        ConstraintFactory.createFromShapeAndComponent(shape, component)
                .ifPresent(c -> testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromConstraintAndTargets(c, shapeTargets)));

        return testCaseBuilder.build();
    }

    public static Set<TestCase> createFromConstraintAndTargets(Constraint constraint, Set<ShapeTarget> targets) {
        ImmutableSet.Builder<TestCase> testCaseBuilder = ImmutableSet.builder();

        targets.stream()
                .map(target -> TestCaseWithTarget.builder()
                        .target(target)
                        .filterSpqrql("")
                        .testCase(constraint.getTestCase())
                        .build())
                .forEach(testCaseBuilder::add);

        return testCaseBuilder.build();

    }

}
