package org.aksw.rdfunit.statistics;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:02 PM
 * @version $Id: $Id
 */
public final class DatasetStatisticsAllIris extends DatasetStatistics {


    /*
    * SPARQL query to get all IRIs
    * */
    private static final String iriListSPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " select distinct (?iri AS ?stats) {\n" +
            "  {  \n" +
            "    select (?s1 AS ?iri) where {?s1 ?p1 ?o1} \n" +
            "  } UNION {\n" +
            "    select (?p2 AS ?iri) where {?s2 ?p2 ?o2} \n" +
            "  } UNION {\n" +
            "    select (?o3 AS ?iri) where {?s3 ?p3 ?o3. FILTER (isIRI(?o3))} \n" +
            "  }\n" +
            "} ";

    /** {@inheritDoc} */
    @Override
    public String getStatisticsQuery() {
        return iriListSPARQL;
    }


}
