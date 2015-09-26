package org.aksw.rdfunit.prefix;

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
 * @version $Id: $Id
 */
public final class LOVEndpoint {

    private static final Logger log = LoggerFactory.getLogger(LOVEndpoint.class);

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

    public LOVEndpoint(){}


    /**
     * <p>getAllLOVEntries.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<SchemaEntry> getAllLOVEntries() {

        List<SchemaEntry> lovEntries = new LinkedList<>();
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(lovEndpointURI, Arrays.asList(lovGraph));


        try (QueryExecution qe = qef.createQueryExecution(lovSparqlQuery)) {

            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution row = rs.next();

                String prefix = row.get("vocabPrefix").asLiteral().getLexicalForm();
                String vocab = row.get("vocabURI").asLiteral().getLexicalForm();
                String ns = row.get("vocabNamespace").asLiteral().getLexicalForm();
                String definedBy = ns; // default
                if (ns == null || ns.isEmpty()) {
                    ns = vocab;
                }

                if (row.get("definedBy") != null) {
                    definedBy = row.get("definedBy").asLiteral().getLexicalForm();
                }
                lovEntries.add(new SchemaEntry(prefix, vocab, ns, definedBy));
            }
        } catch (Exception e) {
            log.error("Encountered error when reading schema information from LOV, schema prefixes & auto schema discovery might not work as expected", e);
        }

        return lovEntries;
    }
}
