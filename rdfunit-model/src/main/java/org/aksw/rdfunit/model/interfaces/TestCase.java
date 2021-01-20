package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

/**
 * @author Dimitris Kontokostas
 * @since 9/23/13 6:31 AM
 */
public interface TestCase extends GenericTestCase {

  String getSparqlWhere();

  String getSparqlPrevalence();

  RDFNode getFocusNode(QuerySolution solution);

  default Query getSparqlPrevalenceQuery() {
    if (getSparqlPrevalence().trim().isEmpty()) {
      return null;
    }
    return QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getSparqlPrevalence());
  }
}
