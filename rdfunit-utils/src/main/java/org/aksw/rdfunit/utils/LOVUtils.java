package org.aksw.rdfunit.utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * LOV (lov.okfn.org) is a vocabulary repository that we re-use to do prefix dereferencing
 * and locate the exact definition IRI in case TCN is not properly configured
 *
 * @author Dimitris Kontokostas
 * @since 3 /31/15 4:15 PM
 */
public final class LOVUtils {

    private static final Logger log = LoggerFactory.getLogger(LOVUtils.class);

    private static final String lovEndpointURI = "http://lov.okfn.org/dataset/lov/sparql";
    private static final String lovGraph = "http://lov.okfn.org/dataset/lov";
    private static final String lovSparqlQuery =
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX vann:<http://purl.org/vocab/vann/>\n" +
            "PREFIX voaf:<http://purl.org/vocommons/voaf#>\n" +
            "SELECT ?vocabURI ?vocabPrefix ?vocabNamespace ?definedBy\n" +
            "WHERE{\n" +
            "\t?vocabURI a voaf:Vocabulary.\n" +
            "\t?vocabURI vann:preferredNamespacePrefix ?vocabPrefix.\n" +
            "\t?vocabURI vann:preferredNamespaceUri ?vocabNamespace.\n" +
            "\tOPTIONAL {?vocabURI rdfs:isDefinedBy ?definedBy.}\n" +
            "} \n" +
            "ORDER BY ?vocabPrefix ";

    private LOVUtils(){}


    public static List<LOVEntry> getAllLOVEntries() {

        List<LOVEntry> lovEntries = new LinkedList<>();
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(getLovEndpointURI(), Arrays.asList(LOVUtils.getLovGraph()));


        try (QueryExecution qe = qef.createQueryExecution(getLOVSparqlQuery());) {

            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution row = rs.next();

                String prefix = row.get("vocabPrefix").toString();
                String vocab = row.get("vocabURI").toString();
                String ns = row.get("vocabNamespace").toString();
                String definedBy = ns; // default
                if (ns == null || ns.isEmpty()) {
                    ns = vocab;
                }

                if (row.get("definedBy") != null) {
                    definedBy = row.get("definedBy").toString();
                }
                lovEntries.add(new LOVEntry(prefix, vocab, ns, definedBy));
            }
        } catch (Exception e) {
            log.error("Encountered error when reading schema information from LOV, schema prefixes & auto schema discovery might not work as expected", e);
        }

        return lovEntries;
    }


    /**
     * Gets the lOV sparql query.
     *
     * @return the lOV sparql query
     */
    public static String getLOVSparqlQuery() {
        return lovSparqlQuery;
    }

    /**
     * Gets lov endpoint uRI.
     *
     * @return the lov endpoint uRI
     */
    public static String getLovEndpointURI() {
        return lovEndpointURI;
    }

    /**
     * Gets lov graph.
     *
     * @return the lov graph
     */
    public static String getLovGraph() {
        return lovGraph;
    }


}
