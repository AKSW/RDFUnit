package org.aksw.rdfunit.statistics;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates property and class statistics for a QEF
 *
 * @author Dimitris Kontokostas
 * @version $Id : $Id
 * @since 6 /16/14 1:27 PM
 */
public abstract class DatasetStatistics {

    /**
     * Gets statistics query.
     *
     * @return the statistics query
     */
    protected abstract String getStatisticsQuery();


    /**
     * Returns a map with statistics according to the current execution query.
     *
     * @param qef the qef
     * @return a Map, if doGetCounts is false, the number defaults to 0
     */
    public Map<String, Long> getStatisticsMap(QueryExecutionFactory qef) {

        return getStats(getStatisticsQuery(), qef);

    }


    private Map<String, Long> getStats(String sparqlQuery, QueryExecutionFactory qef) {
        Map<String, Long> stats = new HashMap<>();


        try (QueryExecution qe =  qef.createQueryExecution(sparqlQuery))
        {
            qe.execSelect().forEachRemaining( qs -> {

                String s = qs.get("stats").toString();
                int c = 0;
                if (qs.contains("count")) {
                    c = qs.get("count").asLiteral().getInt();
                }
                stats.put(s, (long) c);
            });
        }

        return stats;
    }
}
