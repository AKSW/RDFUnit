package org.aksw.rdfunit.Utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.EndpointTestSource;
import org.aksw.rdfunit.sources.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/24/13 11:25 AM
 */
public final class RDFUnitUtils {
    private static final Logger log = LoggerFactory.getLogger(RDFUnitUtils.class);

    private RDFUnitUtils() {
    }

    public static void fillSchemaServiceFromFile(String additionalCSV) {

        try (InputStream inputStream = new FileInputStream(additionalCSV)) {
            fillSchemaServiceFromFile(inputStream);
        } catch (FileNotFoundException e) {
            log.error("File " + additionalCSV + " not fount!", e);
        } catch (IOException e) {
            log.error("File " + additionalCSV + " not fount!", e);
        }
    }

    public static void fillSchemaServiceFromFile(InputStream additionalCSV) {

        int count = 0;

        if (additionalCSV != null) {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(additionalCSV, "UTF-8"))) {

                String line = null;

                while ((line = in.readLine()) != null) {
                    // skip comments & empty lines
                    if (line.startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }

                    count++;

                    String[] parts = line.split(",");
                    switch (parts.length) {
                        case 2:
                            SchemaService.addSchemaDecl(parts[0], parts[1]);
                            break;
                        case 3:
                            SchemaService.addSchemaDecl(parts[0], parts[1], parts[2]);
                            break;
                        default:
                            log.error("Invalid schema declaration in " + additionalCSV + ". Line: " + line);
                            count--;
                    }
                }

            } catch (UnsupportedEncodingException e) {
                log.debug("UnsupportedEncodingException: ", e);
                return;
            } catch (IOException e) {
                log.debug("IOException: ", e);
            }

            log.info("Loaded " + count + " schema declarations from: " + additionalCSV);
        }

        if (additionalCSV != null) {
            try {
                additionalCSV.close();
            } catch (IOException e) {
                log.debug("IOException: ", e);
            }
        }
    }

    public static void fillSchemaServiceFromLOV() {

        Source lov = new EndpointTestSource("lov", "http://lov.okfn.org", "http://lov.okfn.org/endpoint/lov", new ArrayList<String>(), null);

        QueryExecution qe = null;
        int count = 0;


        try {
            // Example from http://lov.okfn.org/endpoint/lov
            qe = lov.getExecutionFactory().createQueryExecution(
                    "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                            "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
                            "PREFIX dcterms:<http://purl.org/dc/terms/>\n" +
                            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
                            "PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
                            "PREFIX skos:<http://www.w3.org/2004/02/skos/core#>\n" +
                            "PREFIX foaf:<http://xmlns.com/foaf/0.1/>\n" +
                            "PREFIX void:<http://rdfs.org/ns/void#>\n" +
                            "PREFIX bibo:<http://purl.org/ontology/bibo/>\n" +
                            "PREFIX vann:<http://purl.org/vocab/vann/>\n" +
                            "PREFIX voaf:<http://purl.org/vocommons/voaf#>\n" +
                            "PREFIX frbr:<http://purl.org/vocab/frbr/core#>\n" +
                            "PREFIX lov:<http://lov.okfn.org/dataset/lov/lov#>\n" +
                            "\n" +
                            "SELECT ?vocabURI ?vocabPrefix ?vocabNamespace ?definedBy\n" +
                            "WHERE{\n" +
                            "\t?vocabURI a voaf:Vocabulary.\n" +
                            "\t?vocabURI vann:preferredNamespacePrefix ?vocabPrefix.\n" +
                            "\t?vocabURI vann:preferredNamespaceUri ?vocabNamespace.\n" +
                            "\tOPTIONAL {?vocabURI rdfs:isDefinedBy ?definedBy.}\n" +
                            "} \n" +
                            "ORDER BY ?vocabPrefix ");


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
                SchemaService.addSchemaDecl(prefix, ns, definedBy);
                count++;
            }
        } catch (Exception e) {
            //TODO log error about lov
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        log.info("Loaded " + count + " schema declarations from LOV SPARQL Endpoint");
    }

    public static boolean fileExists(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static <T> T getFirstItemInCollection(Collection<T> collection) {
        //noinspection LoopStatementThatDoesntLoop
        for (T item : collection) {
            return item;
        }
        return null;
    }
}
