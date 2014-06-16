package org.aksw.rdfunit.statistics;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * User: Dimitris Kontokostas
 * Generates property and class statistics for a source
 * Created: 6/16/14 1:27 PM
 */
public class DatasetStatistics {
    private final QueryExecutionFactory qef;
    private final boolean doGetCounts;

    public DatasetStatistics(QueryExecutionFactory qef, boolean doGetCounts) {
        this.qef = qef;
        this.doGetCounts = doGetCounts;
    }




}
