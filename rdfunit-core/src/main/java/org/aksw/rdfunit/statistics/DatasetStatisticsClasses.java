package org.aksw.rdfunit.statistics;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:02 PM
 */
public class DatasetStatisticsClasses extends DatasetStatistics {


    /*
    * Simple SPARQL query to get all class occurrences
    * */
    private static final String classStatsSPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT DISTINCT ?stats WHERE {" +
            "     ?s a ?stats . } ";

    @Override
    public String getStatisticsQuery() {
        return classStatsSPARQL;
    }


}
