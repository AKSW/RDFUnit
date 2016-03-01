package org.aksw.rdfunit.tests.query_generation;


import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class QueryGenerationSelectFactoryTest {

    private static final String sparqlSelect = " SELECT DISTINCT ?this WHERE ";

    private static final String goodSparqlQuery = "{ ?this ?p ?o }";

    private QueryGenerationSelectFactory queryGenerationSelectFactory;

    @Before
    public void init() throws TestCaseInstantiationException {

        queryGenerationSelectFactory = new QueryGenerationSelectFactory();

    }

    @Test
    public void checkQuery() {

        TestCase testCase = ManualTestCaseImpl.builder()
                .element(ResourceFactory.createResource("http://example.com"))
                .testCaseAnnotation(Mockito.mock(TestCaseAnnotation.class))  //Mock class
                .sparqlWhere(goodSparqlQuery)
                .sparqlPrevalence("")
                .build();

        Query query1 = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + sparqlSelect + goodSparqlQuery);
        Query query2 = queryGenerationSelectFactory.getSparqlQuery(testCase);


        assertEquals(query1, query2);
    }

}