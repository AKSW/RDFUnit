package org.aksw.rdfunit.statistics;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:02 PM
 * @version $Id: $Id
 */
public class DatasetStatisticsProperties extends DatasetStatistics {

    /*
    * Simple SPARQL query to get all property occurrences
    * */
    private static final String propertyStatsSPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT DISTINCT ?stats WHERE {" +
            "     ?s ?stats ?o . } ";

    /** {@inheritDoc} */
    @Override
    public String getStatisticsQuery() {
        return propertyStatsSPARQL;
    }


}
