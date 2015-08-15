package org.aksw.rdfunit.statistics;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.junit.Before;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public abstract class DatasetStatisticsTest {

    protected QueryExecutionFactory qef;

    @Before
    public void setUp() throws Exception {
        RDFReader reader = RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/data/statistics.sample.ttl");
        qef = new QueryExecutionFactoryModel(reader.read());
    }

    protected abstract int getExteptedItems();

    protected abstract DatasetStatistics getStatisticsObject();

    protected Map<String, Integer> executeBasicTest() {
        Map<String, Integer> stats = getStatisticsObject().getStatisticsMap(qef);
        assertEquals(getExteptedItems(), stats.size());

        return stats;
    }

    /*
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
        Collection<String> ns = datasetStatistics.getAllNamespacesOntology();
        Collection<String> nsCount = datasetStatisticsCounts.getAllNamespacesOntology();

        assertEquals("NS with counts should be equal without counts", ns.size(), nsCount.size());
        assertEquals("Should be 3", ns.size(), nsInPatterns);

    }

    @Test
    public void testgetIdentifiedSchemata() throws Exception {
        //Collection<SchemaSource> schemata =  datasetStatistics.getIdentifiedSchemataOntology();
        //Collection<SchemaSource> schemataCount = datasetStatisticsCounts.getIdentifiedSchemataOntology();

        // TODO: init SchemaService to get data
        //assertEquals("NS with counts should be equal without counts", schemata.size(), schemataCount.size());
        //assertEquals("Should be 3", schemata.size(), nsInPatterns);

    }  */


}