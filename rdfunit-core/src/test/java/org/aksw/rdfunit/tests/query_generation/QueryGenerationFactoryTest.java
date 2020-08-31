package org.aksw.rdfunit.tests.query_generation;


import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;
import org.mockito.Mockito;

public final class QueryGenerationFactoryTest {

  private static final String basicSparqlSelect = " SELECT ?this WHERE ";
  private static final String basicSparqlQuery = "{ ?this ?p ?o }";

  private static final TestCase testCaseWithBasicSparqlQuery = ManualTestCaseImpl.builder()
      .element(ResourceFactory.createResource("http://example.com"))
      .testCaseAnnotation(Mockito.mock(TestCaseAnnotation.class))  //Mock class
      .sparqlWhere(basicSparqlQuery)
      .sparqlPrevalence("")
      .build();


  @Test
  public void checkSelectFactory() {

    QueryGenerationFactory queryGenerationSelectFactory = new QueryGenerationSelectFactory();

    Query query1 = QueryFactory
        .create(PrefixNSService.getSparqlPrefixDecl() + basicSparqlSelect + basicSparqlQuery);
    Query query2 = queryGenerationSelectFactory.getSparqlQuery(testCaseWithBasicSparqlQuery);

    assertThat(query1)
        .isEqualTo(query2);
  }

  @Test
  public void checkCacheFactory() {

    QueryGenerationFactory queryGenerationSelectFactory = new QueryGenerationSelectFactory();
    QueryGenerationFactory queryGenerationCacheFactory = new QueryGenerationFactoryCache(
        queryGenerationSelectFactory);

    assertThat(queryGenerationSelectFactory.getSparqlQuery(testCaseWithBasicSparqlQuery))
        .isEqualTo(queryGenerationCacheFactory.getSparqlQuery(testCaseWithBasicSparqlQuery));

    assertThat(queryGenerationSelectFactory.getSparqlQueryAsString(testCaseWithBasicSparqlQuery))
        .isEqualTo(
            queryGenerationCacheFactory.getSparqlQueryAsString(testCaseWithBasicSparqlQuery));
  }
}