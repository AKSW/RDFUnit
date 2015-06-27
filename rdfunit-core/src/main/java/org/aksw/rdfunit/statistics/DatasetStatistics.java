package org.aksw.rdfunit.statistics;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates property and class statistics for a QEF
 *
 * @author Dimitris Kontokostas
 * @version $Id: $Id
 * @since 6 /16/14 1:27 PM
 */
public abstract class DatasetStatistics {

    private static final Logger log = LoggerFactory.getLogger(DatasetStatistics.class);


    protected abstract String getStatisticsQuery();


    /**
     * Returns a map with statistics according to the current execution query.
     *
     * @return a Map, if doGetCounts is false, the number defaults to 0
     */
    public Map<String, Integer> getStatisticsMap(QueryExecutionFactory qef) {

        return getStats(getStatisticsQuery(), qef);

    }

    public DatasetStatistics() {
    }

    /**
     * Gets stats.
     *
     * @param sparqlQuery the sparql query
     * @return the stats
     */
/*
    * helper function to get any stats
    * */
    private Map<String, Integer> getStats(String sparqlQuery, QueryExecutionFactory qef) {
        Map<String, Integer> stats = new HashMap<>();

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlQuery);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();

                String s = qs.get("stats").toString();
                int c = 0;
                if (qs.contains("count")) {
                    c = qs.get("count").asLiteral().getInt();
                }
                stats.put(s, c);
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return stats;
    }
}
