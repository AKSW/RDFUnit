package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentConstraint;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.SparqlConstraint;
import org.aksw.rdfunit.model.readers.shacl.SparqlValidatorReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

@Slf4j
public final class ConstraintTestCaseFactory {

  private ConstraintTestCaseFactory() {
  }

  public static Set<TestCaseWithTarget> createFromComponentAndShape(Component component,
      Shape shape, Set<ShapeTarget> shapeTargets) {
    ImmutableSet.Builder<TestCaseWithTarget> testCaseBuilder = ImmutableSet.builder();

    ConstraintFactory.createFromShapeAndComponent(shape, component)
        .forEach(c -> testCaseBuilder
            .addAll(ConstraintTestCaseFactory.createFromConstraintAndTargets(c, shapeTargets)));

    return testCaseBuilder.build();
  }

  public static Set<TestCaseWithTarget> createFromConstraintAndTargets(
      ComponentConstraint constraint, Set<ShapeTarget> targets) {
    ImmutableSet.Builder<TestCaseWithTarget> testCaseBuilder = ImmutableSet.builder();

    targets.stream()
        .map(target -> TestCaseWithTarget.builder()
            .target(target)
            .filterSparql("")
            .testCase(constraint.getTestCase())
            .build())
        .forEach(testCaseBuilder::add);

    return testCaseBuilder.build();
  }


  public static Set<TestCaseWithTarget> createFromSparqlContraintInShape(Shape shape,
      Set<ShapeTarget> shapeTargets) {
    ImmutableSet.Builder<TestCaseWithTarget> testCaseBuilder = ImmutableSet.builder();

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
              .message(validator.getDefaultMessage()
                  .orElse(ResourceFactory.createStringLiteral("Error")))
              .build();

          shapeTargets.forEach(target -> {
            TestCaseWithTarget tc = TestCaseWithTarget.builder()
                .target(target)
                .filterSparql("")
                .testCase(sc.getTestCase())
                .build();
            testCaseBuilder.add(tc);
          });
        });

    return testCaseBuilder.build();
  }
}
