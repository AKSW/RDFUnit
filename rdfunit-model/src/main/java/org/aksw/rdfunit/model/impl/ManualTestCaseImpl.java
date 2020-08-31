package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.utils.CommonNames;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * ManualTestCase Implementation
 *
 * @author Dimitris Kontokostas
 * @since 1/3/14 3:57 PM
 */
@Builder
@ToString(exclude = "element")
@EqualsAndHashCode(exclude = "element")
public class ManualTestCaseImpl implements TestCase {

  @Getter
  @NonNull
  private final Resource element;
  @Getter
  @NonNull
  private final TestCaseAnnotation testCaseAnnotation;
  @Getter
  @NonNull
  @Singular
  private final ImmutableSet<PrefixDeclaration> prefixDeclarations;

  @Getter
  @NonNull
  private final String sparqlWhere;
  @Getter
  @NonNull
  private final String sparqlPrevalence;

  @Override
  public RDFNode getFocusNode(QuerySolution solution) {
    String focusVar = getVariableAnnotations().stream()
        .filter(ra -> ra.getAnnotationProperty().equals(SHACL.focusNode))
        .map(ResultAnnotation::getAnnotationVarName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElse(CommonNames.This);
    return solution.get(focusVar);
  }
}
