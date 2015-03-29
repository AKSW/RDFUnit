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

import java.util.*;

/**
 * Generates property and class statistics for a QEF
 *
 * @author Dimitris Kontokostas
 * @since 6 /16/14 1:27 PM
 * @version $Id: $Id
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

    /*
    * SPARQL query to get all IRIs
    * */
    private static final String iriListSPARQL = PrefixNSService.getSparqlPrefixDecl() +
            " select distinct ?iri {\n" +
            "  {  \n" +
            "    select (?s1 AS ?iri) where {?s1 ?p1 ?o1} \n" +
            "  } UNION {\n" +
            "    select (?p2 AS ?iri) where {?s2 ?p2 ?o2} \n" +
            "  } UNION {\n" +
            "    select (?o3 AS ?iri) where {?s3 ?p3 ?o3. FILTER (isIRI(?o3))} \n" +
            "  }\n" +
            "} ";

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
    public Collection<String> getAllNamespacesOntology() {
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

    public Collection<String> getAllNamespacesComplete() {
        Set<String> namespaces = new HashSet<>();

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(iriListSPARQL);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();

                String iri = qs.get("iri").toString();
                namespaces.add(getNamespaceFromURI(iri));
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return namespaces;
    }

    private final Collection<String> excludePrefixes = Arrays.asList("rdf", "rdfs", "owl", "rdfa");

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
