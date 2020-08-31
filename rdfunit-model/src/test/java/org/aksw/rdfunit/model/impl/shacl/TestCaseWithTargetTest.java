package org.aksw.rdfunit.model.impl.shacl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.assertj.core.api.Fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 1:22 μμ
 */
@RunWith(Parameterized.class)
public class TestCaseWithTargetTest {

  @Parameterized.Parameter
  public String sparqlQuery;
  @Parameterized.Parameter(value = 1)
  public ShapeTarget target;

  @Parameterized.Parameters(name = "{index}")
  public static Collection<Object[]> items() {

    List<String> sparqlSampleQueries = Arrays.asList(
        "select ?s where{?s ?p ?o }",
        "select ?s where { ?this ?p ?o ; ?p2 ?o}"
    );

    Resource p = ResourceFactory.createResource("http://example.com#ex");
    List<ShapeTarget> targets = Arrays.stream(ShapeTargetType.values())
        .filter(sct -> !sct.equals(ShapeTargetType.ValueShapeTarget))
        .map(scType -> ShapeTargetCore.create(scType, p))
        .collect(Collectors.toList());

    Collection<Object[]> parameters = new ArrayList<>();
    sparqlSampleQueries
        .forEach(sparql -> targets
            .forEach(target ->
                parameters.add(new Object[]{sparql, target})));
    return parameters;
  }

  @Test
  public void test() {

    TestCase innerTestCAse = Mockito.mock(TestCase.class);
    when(innerTestCAse.getSparqlWhere()).thenReturn(sparqlQuery);

    TestCase testCaseWithTarget = TestCaseWithTarget.builder()
        .testCase(innerTestCAse)
        .target(target)
        .filterSparql(" ?this <http://example.cpm/p> ?value .")
        .build();

    String finalSparql =
        PrefixNSService.getSparqlPrefixDecl() + testCaseWithTarget.getSparqlWhere();

    assertThat(finalSparql)
        .contains(target.getPattern());

    try {
      QueryFactory.create(finalSparql);
    } catch (Exception e) {
      Fail.fail("Failed sparql query:\n" + finalSparql, e);
    }

  }
}