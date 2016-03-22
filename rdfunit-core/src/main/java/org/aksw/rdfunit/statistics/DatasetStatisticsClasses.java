package org.aksw.rdfunit.statistics;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:02 PM
 * @version $Id: $Id
 */
public class DatasetStatisticsClasses extends DatasetStatistics {


    /*
    * Simple SPARQL query to get all class occurrences
    * */
    private static final String CLASS_STATS_SPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT DISTINCT ?stats WHERE {" +
            "     ?s a ?stats . } ";

    /** {@inheritDoc} */
    @Override
    public String getStatisticsQuery() {
        return CLASS_STATS_SPARQL;
    }


}
