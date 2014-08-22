package org.aksw.rdfunit.statistics;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.io.RDFReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DatasetStatisticsTest {

    private DatasetStatistics datasetStatistics;
    private DatasetStatistics datasetStatisticsCounts;

    /* number of defined NS in patterns.ttl*/
    private static final int nsInPatterns = 3;
    private static final Integer zero = new Integer(0);


    @Before
    public void setUp() throws Exception {
        RDFReader patternReader = RDFUnitUtils.getPatternsFromResource();
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(patternReader.read());
        datasetStatistics = new DatasetStatistics(qef, false);
        datasetStatisticsCounts = new DatasetStatistics(qef, true);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetPropertyStats() throws Exception {
        Map<String, Integer> pStats = datasetStatistics.getPropertyStats();
        Map<String, Integer> pStatsCounts = datasetStatisticsCounts.getPropertyStats();

        assertEquals("Stats with counts should be equal without counts", pStats.size(), pStatsCounts.size());

        for (Map.Entry<String, Integer> entry : pStats.entrySet()) {
            Integer count = entry.getValue();
            assertNotNull(count);
            assertEquals("No count stats must have 0 as count", count, zero);
        }

        for (Map.Entry<String, Integer> entry : pStatsCounts.entrySet()) {
            Integer count = entry.getValue();
            assertNotNull(count);
            assertNotEquals("No count stats must have <>0 as count", count, zero);
        }

    }

    @Test
    public void testGetClassStats() throws Exception {

        Map<String, Integer> cStats = datasetStatistics.getClassStats();
        Map<String, Integer> cStatsCounts = datasetStatisticsCounts.getClassStats();

        assertEquals("Stats with counts should be equal without counts", cStats.size(), cStatsCounts.size());


        for (Map.Entry<String, Integer> entry : cStats.entrySet()) {
            Integer count = entry.getValue();
            assertNotNull(count);
            assertEquals("No count stats must have 0 as count", count, zero);
        }

        for (Map.Entry<String, Integer> entry : cStatsCounts.entrySet()) {
            Integer count = entry.getValue();
            assertNotNull(count);
            assertNotEquals("No count stats must have <>0 as count", count, zero);
        }
    }

    @Test
    public void testGetAllNamespaces() throws Exception {
        Collection<String> ns = datasetStatistics.getAllNamespaces();
        Collection<String> nsCount = datasetStatisticsCounts.getAllNamespaces();

        assertEquals("NS with counts should be equal without counts", ns.size(), nsCount.size());
        assertEquals("Should be 3", ns.size(), nsInPatterns);

    }

    @Test
    public void testgetIdentifiedSchemata() throws Exception {
        /*Collection<SchemaSource> schemata = */ datasetStatistics.getIdentifiedSchemata();
        //Collection<SchemaSource> schemataCount = datasetStatisticsCounts.getIdentifiedSchemata();

        // TODO: init SchemaService to get data
        //assertEquals("NS with counts should be equal without counts", schemata.size(), schemataCount.size());
        //assertEquals("Should be 3", schemata.size(), nsInPatterns);

    }

    @Test
    public void testGetNamespaceFromURI() throws Exception {
        Map<String, String> examples = new HashMap<>();
        examples.put("http://example.com/property", "http://example.com/");
        examples.put("http://example.com#property", "http://example.com#");
        examples.put("http://www.w3.org/2004/02/skos/core#broader", "http://www.w3.org/2004/02/skos/core#");

        for (Map.Entry<String, String> entry : examples.entrySet()) {
            String namespace = entry.getValue();
            assertEquals("All prefixed should be initialized", namespace, datasetStatistics.getNamespaceFromURI(entry.getKey()));
            // test both in case there is a conflict
            assertEquals("All prefixed should be initialized", namespace, datasetStatisticsCounts.getNamespaceFromURI(entry.getKey()));
        }
    }

    @Test
    public void testGetStats() throws Exception {

    }
}