package org.aksw.rdfunit.examples;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.utils.PrefixNSService;
import org.aksw.rdfunit.utils.RDFUnitUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 1/9/15 2:59 PM
 */
public class GetAllVocabLinksFromLOV {

    public static void main(String[] args) throws Exception {
        RDFUnitUtils.fillSchemaServiceFromLOV();
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, ModelFactory.createDefaultModel());
        for (SchemaSource schema : SchemaService.getSourceListAll(false,null)){
            QueryExecutionFactory qef = schema.getExecutionFactory();

            String queryString = PrefixNSService.getSparqlPrefixDecl() +
                    " SELECT DISTINCT ?s ?p ?o WHERE { " +
                    " ?s ?p ?o ." +
                    " FILTER (?p IN (owl:sameAs, owl:equivalentProperty, owl:equivalentClass))" +
                   // " FILTER (strStarts(?s, 'http://dbpedia.org') || strStarts(?o, 'http://dbpedia.org')))" +
                    "}";

            QueryExecution qe = null;
            try {
                qe = qef.createQueryExecution(queryString);
                ResultSet results = qe.execSelect();

                while (results.hasNext()) {
                    QuerySolution qs = results.next();

                    Resource s = qs.get("s").asResource();
                    Resource p = qs.get("p").asResource();
                    RDFNode o = qs.get("o");

                    model.add(s, ResourceFactory.createProperty(p.getURI()) ,o);

                    // save the data in a file to read later
                }
            } finally {
                if (qe != null) {
                    qe.close();
                }
            }
        }

        try (OutputStream fos = new FileOutputStream("output.ttl")) {

            model.write(fos, "TURTLE");

        } catch (Exception e) {
            throw new RuntimeException("Error writing file: " + e.getMessage(), e);
        }

    }
}
