package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.aksw.rdfunit.model.readers.shacl.SparqlValidatorReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

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

    public static Set<TestCase> createFromConstraintAndTargets(ComponentConstraint constraint, Set<ShapeTarget> targets) {
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


    public static Set<TestCase> createFromSparqlContraintInShape(Shape shape, Set<ShapeTarget> shapeTargets) {
        ImmutableSet.Builder<TestCase> testCaseBuilder = ImmutableSet.builder();

        shape.getElement().listProperties(SHACL.sparql).toSet().stream()
                .map(Statement::getObject)
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(r -> SparqlValidatorReader.create().read(r))
                .forEach(validator -> {
                    SparqlConstraint sc = SparqlConstraintImpl.builder()
                            .shape(shape)
                            .validator(validator)
                            .severity(shape.getSeverity())
                            .message(validator.getDefaultMessage().orElse(ResourceFactory.createStringLiteral("Error")))
                            .build();

                    shapeTargets.forEach(target -> {
                        TestCase tc = TestCaseWithTarget.builder()
                                .target(target)
                                .filterSpqrql("")
                                .testCase(sc.getTestCase())
                                //.prefixDeclarations(validator.getPrefixDeclarations())
                                .build();
                        testCaseBuilder.add(tc);
                    });
                });

        return testCaseBuilder.build();
    }

}
