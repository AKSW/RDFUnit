package org.aksw.rdfunit.statistics;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.utils.PrefixNSService;
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
     * Uses the getAllNamespacesOntology() function and tries to match them to SchemaSource's
     *
     * @return a Collection of SchemaSource's for all identified namespaces
     */
    public Collection<SchemaSource> getIdentifiedSchemataOntology() {
        return getIdentifiedSchemata(getAllNamespacesOntology());
    }

    /**
     * Uses the getAllNamespacesOntology() function and tries to match them to SchemaSource's
     *
     * @return a Collection of SchemaSource's for all identified namespaces
     */
    public Collection<SchemaSource> getIdentifiedSchemataAll() {
        return getIdentifiedSchemata(getAllNamespacesComplete());
    }


    private Collection<SchemaSource> getIdentifiedSchemata(Collection<String> namespaces) {
        Collection<SchemaSource> sources = new ArrayList<>();

        for (String namespace : namespaces) {

            SchemaSource source = SchemaService.getSourceFromUri(namespace);

            // If not null, get it from SchemaService
            if (source != null) {

                // Skip some schemas that don't add anything
                if (excludePrefixes.contains(source.getPrefix())) {
                    continue;
                }
                sources.add(source);
            } else {
                log.warn("Undefined namespace in LOV or schemaDecl.csv: " + namespace);
            }
        }

        return sources;
    }



    /**
     * Gets namespace from uRI.
     *
     * @param uri the uri
     * @return the namespace from uRI
     */
    protected String getNamespaceFromURI(String uri) {
        String breakChar = "/";
        if (uri.contains("#")) {
            breakChar = "#";
        }

        int pos = Math.min(uri.lastIndexOf(breakChar), uri.length());
        return uri.substring(0, pos + 1);
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
