package org.aksw.rdfunit.examples;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 1/9/15 2:59 PM
 * @version $Id: $Id
 */
public class GetAllVocabLinksFromLOV {

    private GetAllVocabLinksFromLOV(){}

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        RDFUnitUtils.fillSchemaServiceFromLOV();
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, ModelFactory.createDefaultModel());
        for (SchemaSource schema : SchemaService.getSourceListAll(false,null)){
            QueryExecutionFactory qef = new QueryExecutionFactoryModel(schema.getModel());

            String queryString = PrefixNSService.getSparqlPrefixDecl() +
                    " SELECT DISTINCT ?s ?p ?o WHERE { " +
                    " ?s ?p ?o ." +
                    " FILTER (?p IN (owl:sameAs, owl:equivalentProperty, owl:equivalentClass))" +
                   // " FILTER (strStarts(?s, 'http://dbpedia.org') || strStarts(?o, 'http://dbpedia.org')))" +
                    "}";

            try (QueryExecution qe = qef.createQueryExecution(queryString)) {
                qe.execSelect().forEachRemaining(qs -> {

                    Resource s = qs.get("s").asResource();
                    Resource p = qs.get("p").asResource();
                    RDFNode o = qs.get("o");

                    model.add(s, ResourceFactory.createProperty(p.getURI()), o);

                    // save the data in a file to read later
                });
            }
        }

        try (OutputStream fos = new FileOutputStream("output.ttl")) {

            model.write(fos, "TURTLE");

        } catch (Exception e) {
            throw new UnsupportedOperationException("Error writing file: " + e.getMessage(), e);
        }

    }
}
