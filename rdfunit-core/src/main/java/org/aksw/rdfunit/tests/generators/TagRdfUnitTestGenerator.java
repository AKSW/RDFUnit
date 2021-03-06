package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.TestCaseValidator;
import org.aksw.rdfunit.utils.TestUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */

@Slf4j
public class TagRdfUnitTestGenerator implements RdfUnitTestGenerator {

  private final ImmutableList<TestGenerator> testGenerators;

  public TagRdfUnitTestGenerator(Collection<TestGenerator> testGenerators) {
    this.testGenerators = ImmutableList.copyOf(testGenerators);
  }

  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    return ImmutableList.of();
  }

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {

    Model m = source.getModel();
    try (QueryExecutionFactoryModel qef = new QueryExecutionFactoryModel(m)) {
      Set<TestCase> tests = testGenerators.stream()
          .parallel()
          .flatMap(tg -> generate(qef, source, tg).stream())
          .collect(Collectors.toSet());

      log.info("{} generated {} tests using {} TAGs", source.getUri(), tests.size(),
          testGenerators.size());
      return tests;
    }
  }

  private Set<TestCase> generate(QueryExecutionFactoryModel qef, SchemaSource source,
      TestGenerator testGenerator) {
    Set<TestCase> tests = new HashSet<>();

    Pattern tgPattern = testGenerator.getPattern();

    Query q = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + testGenerator.getQuery());
    try (QueryExecution qe = qef.createQueryExecution(q)) {
      qe.execSelect().forEachRemaining(result -> {

        Optional<TestCase> tc = generateTestFromResult(testGenerator, tgPattern, result, source);
        tc.ifPresent(tests::add);

      });
    }
    return tests;
  }

  private Optional<TestCase> generateTestFromResult(TestGenerator tg, Pattern tgPattern,
      QuerySolution row, SchemaSource schemaSource) {
    Set<String> references = new HashSet<>();
    String description;

    Collection<Binding> bindings = new ArrayList<>();
    for (PatternParameter p : tgPattern.getParameters()) {
      if (row.contains(p.getId())) {
        RDFNode n = row.get(p.getId());
        Binding b;
        try {
          b = new Binding(p, n);
        } catch (NullPointerException | IllegalArgumentException e) {
          log.error("Non valid binding for parameter {} in AutoGenerator: {}", p.getId(),
              tg.getUri(), e);
          continue;
        }
        bindings.add(b);
        if (n.isResource() && !"loglevel".equalsIgnoreCase(p.getId())) {
          references.add(n.toString().trim().replace(" ", ""));
        }
      } else {
        log.error("Not bindings for parameter {} in AutoGenerator: {}", p.getId(), tg.getUri());
        break;
      }
    }
    if (bindings.size() != tg.getPattern().getParameters().size()) {
      log.error("Bindings for pattern {} do not match in AutoGenerator: {}", tgPattern.getId(),
          tg.getUri());
      return Optional.empty();
    }

    if (row.get("DESCRIPTION") != null) {
      description = row.get("DESCRIPTION").toString();
    } else {
      log.error("No ?DESCRIPTION variable found in AutoGenerator: {}", tg.getUri());
      return Optional.empty();
    }

    Set<ResultAnnotation> patternBindedAnnotations = tgPattern.getBindedAnnotations(bindings);
    patternBindedAnnotations.addAll(tg.getAnnotations());
    Resource tcResource = ResourceFactory.createResource(
        TestUtils.generateTestURI(schemaSource.getPrefix(), tgPattern, bindings, tg.getUri()));
    PatternBasedTestCaseImpl tc = new PatternBasedTestCaseImpl(
        tcResource,
        new TestCaseAnnotation(
            tcResource,
            TestGenerationType.AutoGenerated,
            tg.getUri(),
            schemaSource.getSourceType(),
            schemaSource.getUri(),
            references,
            description,
            null,
            patternBindedAnnotations),
        tgPattern,
        bindings
    );
    new TestCaseValidator(tc).validate();
    return Optional.of(tc);
  }
}
