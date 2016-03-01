package org.aksw.rdfunit.statistics;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 4:16 PM
 */
public class NamespaceStatisticsTest {

    protected RdfReader reader;
    protected QueryExecutionFactory qef;

    @Before
    public void setUp() throws RdfReaderException {
        RdfReader reader = RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/data/statistics.sample.ttl");
        qef = new QueryExecutionFactoryModel(reader.read());
    }


    @Test
    public void testGetNamespaces() {

        assertEquals(13, NamespaceStatistics.createCompleteNSStatisticsAll().getNamespaces(qef).size());
        assertEquals(0, NamespaceStatistics.createCompleteNSStatisticsKnown().getNamespaces(qef).size());

        assertEquals(8, NamespaceStatistics.createOntologyNSStatisticsAll().getNamespaces(qef).size());
        assertEquals(0, NamespaceStatistics.createOntologyNSStatisticsKnown().getNamespaces(qef).size());

    }

    @Test
    public void testGetNamespaceFromURI() {
        Map<String, String> examples = new HashMap<>();
        examples.put("http://example.com/property", "http://example.com/");
        examples.put("http://example.com#property", "http://example.com#");
        examples.put("http://www.w3.org/2004/02/skos/core#broader", "http://www.w3.org/2004/02/skos/core#");

        NamespaceStatistics namespaceStatistics = NamespaceStatistics.createCompleteNSStatisticsAll();
        for (Map.Entry<String, String> entry : examples.entrySet()) {
            String namespace = entry.getValue();
            assertEquals("All prefixed should be initialized", namespace, namespaceStatistics.getNamespaceFromURI(entry.getKey()));
        }
    }
}