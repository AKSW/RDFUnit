package org.aksw.rdfunit.statistics;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Generates property and class statistics for a QEF
 *
 * @author Dimitris Kontokostas
 * @since 6 /16/14 1:27 PM
 */
public final class DatasetStatistics {

    private static final Logger log = LoggerFactory.getLogger(DatasetStatistics.class);

    private final QueryExecutionFactory qef;

    /**
     * switch to activate aggregate stats
     */
    private final boolean doGetCounts;

    /*
    * Simple SPARQL query to get all property occurrences
    * */
    private static final String propertyStatsSPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT DISTINCT ?stats WHERE {" +
            "     ?s ?stats ?o . } ";

    /*
    * SPARQL query to get all property occurrences with counts
    * */
    private static final String propertyStatsSPARQLwithCounts = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT ?stats (count( DISTINCT ?stats) AS ?count) WHERE {" +
            "     ?s ?stats ?o . } " +
            " GROUP BY ?stats ";

    /*
    * Simple SPARQL query to get all class occurrences
    * */
    private static final String classStatsSPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT DISTINCT ?stats WHERE {" +
            "     ?s a ?stats . } ";

    /*
    * SPARQL query to get all class occurrences with counts
    * */
    private static final String classStatsSPARQLwithCounts = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT ?stats (count( DISTINCT ?stats ) AS ?count) WHERE {" +
            "     ?s a ?stats . } " +
            " GROUP BY ?stats ";

    /**
     * Instantiates a new Dataset statistics.
     *
     * @param qef         the qef
     * @param doGetCounts the do get counts
     */
    public DatasetStatistics(QueryExecutionFactory qef, boolean doGetCounts) {
        this.qef = qef;
        this.doGetCounts = doGetCounts;
    }

    /**
     * Returns property statistics.
     *
     * @return a Map, if doGetCounts is false, the number defaults to 0
     */
    public Map<String, Integer> getPropertyStats() {
        if (doGetCounts) {
            return getStats(propertyStatsSPARQLwithCounts);
        } else {
            return getStats(propertyStatsSPARQL);
        }
    }


    /**
     * Returns property statistics.
     *
     * @return a Map, if doGetCounts is false, the number defaults to 0
     */
    public Map<String, Integer> getClassStats() {
        if (doGetCounts) {
            return getStats(classStatsSPARQLwithCounts);
        } else {
            return getStats(classStatsSPARQL);
        }
    }

    /**
     * Gets all namespaces that exists in a QEF
     *
     * @return returns a string Set inside a Collection
     */
    public Collection<String> getAllNamespaces() {
        Set<String> namespaces = new HashSet<>();

        // property stats
        for (String n : getPropertyStats().keySet()) {
            namespaces.add(getNamespaceFromURI(n));
        }

        // class stats
        for (String n : getClassStats().keySet()) {
            namespaces.add(getNamespaceFromURI(n));
        }

        return namespaces;
    }

    /**
     * Uses the getAllNamespaces() function and tries to match them to SchemaSource's
     *
     * @return a Collection of SchemaSource's for all identified namespaces
     */
    public Collection<SchemaSource> getIdentifiedSchemata() {
        Collection<SchemaSource> sources = new ArrayList<>();

        for (String namespace : getAllNamespaces()) {

            //Get the prefix for this namespace
            String schemaPrefix = PrefixNSService.getPrefixFromNS(namespace);
            SchemaSource source = null;

            // If not null, get it from SchemaService
            if (schemaPrefix != null && !schemaPrefix.isEmpty()) {
                source = SchemaService.getSource(schemaPrefix);
            }

            if (source != null) {
                sources.add(source);
            }
            else {
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
        String namespace = uri.substring(0, pos + 1);

        return namespace;
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
    protected Map<String, Integer> getStats(String sparqlQuery) {
        Map<String, Integer> stats = new HashMap<>();

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlQuery);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();

                String s = qs.get("stats").toString();
                int c = 0;
                if (doGetCounts) {
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
