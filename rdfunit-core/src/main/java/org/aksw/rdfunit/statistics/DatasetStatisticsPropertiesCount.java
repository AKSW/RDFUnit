package org.aksw.rdfunit.statistics;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:02 PM
 * @version $Id: $Id
 */
public class DatasetStatisticsPropertiesCount extends DatasetStatistics {

    /*
    * SPARQL query to get all property occurrences with counts
    * */
    private static final String propertyStatsSPARQLwithCounts = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT ?stats (count( DISTINCT ?stats) AS ?count) WHERE {" +
            "     ?s ?stats ?o . } " +
            " GROUP BY ?stats ";

    /** {@inheritDoc} */
    @Override
    public String getStatisticsQuery() {
        return propertyStatsSPARQLwithCounts;
    }


}
