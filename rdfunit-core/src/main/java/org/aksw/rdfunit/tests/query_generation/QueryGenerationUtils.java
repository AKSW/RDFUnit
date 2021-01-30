package org.aksw.rdfunit.tests.query_generation;

import java.util.stream.Collectors;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;

/**
 * @author Dimitris Kontokostas
 * @since 7/27/17
 */
final class QueryGenerationUtils {

  private QueryGenerationUtils() {
  }

  public static String getPrefixDeclarations(TestCase testCase) {
    String standardPrefixes = PrefixNSService.getSparqlPrefixDecl() + "\n";
    String givenPrefixes = testCase.getPrefixDeclarations().stream()
        .map(p -> "PREFIX " + p.getPrefix() + ": <" + p.getNamespace() + ">")
        .collect(Collectors.joining("\n"));
    return standardPrefixes + givenPrefixes + "\n";

  }
}
