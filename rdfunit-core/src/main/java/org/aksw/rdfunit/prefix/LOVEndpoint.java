package org.aksw.rdfunit.prefix;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
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

    /**
     * <p>Constructor for LOVEndpoint.</p>
     */
    public LOVEndpoint(){}


    /**
     * <p>getAllLOVEntries.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<SchemaEntry> getAllLOVEntries() {

        List<SchemaEntry> lovEntries = new LinkedList<>();
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(lovEndpointURI, Collections.singletonList(lovGraph));


        try (QueryExecution qe = qef.createQueryExecution(lovSparqlQuery)) {

            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution row = rs.next();

                String prefix = row.get("vocabPrefix").asLiteral().getLexicalForm();
                String vocab = row.get("vocabURI").asResource().getURI();
                String ns = row.get("vocabNamespace").asLiteral().getLexicalForm();
                String definedBy = ns; // default
                if (ns == null || ns.isEmpty()) {
                    ns = vocab;
                }

                if (row.get("definedBy") != null) {
                    definedBy = row.get("definedBy").asResource().getURI();
                }
                lovEntries.add(new SchemaEntry(prefix, vocab, ns, definedBy));
            }
        } catch (Exception e) {
            log.error("Encountered error when reading schema information from LOV, schema prefixes & auto schema discovery might not work as expected", e);
        }

        return lovEntries;
    }

    /**
     * <p>writeAllLOVEntriesToFile.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public void writeAllLOVEntriesToFile(String filename)  {

        List<SchemaEntry> lovEntries = getAllLOVEntries();
        Collections.sort(lovEntries);
        String header =
                "#This file is auto-generated from the (amazing) LOV service" +
                "# if you don't want to load this use the available CLI / code options " +
                "# To override some of it's entries use the schemaDecl.csv";


        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("outfilename"), "UTF-8")) ){

            out.write(header);
            out.write("\n");

            for (SchemaEntry entry: lovEntries) {
                out.write(entry.getPrefix());
                out.write(',');
                out.write(entry.getVocabularyURI());
                if (!entry.getVocabularyDefinedBy().isEmpty()) {
                    out.write(',');
                    out.write(entry.getVocabularyDefinedBy());
                }
                out.write('\n');
            }
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }


    }
}
