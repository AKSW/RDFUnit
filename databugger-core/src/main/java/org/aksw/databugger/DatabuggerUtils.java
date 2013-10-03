package org.aksw.databugger;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.sources.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/24/13 11:25 AM
 */
public class DatabuggerUtils {
    public static String getAllPrefixes() {
        return  " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                " PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                " PREFIX dcterms: <http://purl.org/dc/terms/> \n" +
                " PREFIX dc: <http://purl.org/dc/elements/1.1/> \n" +
                " PREFIX tddp: <http://databugger.aksw.org/patterns#> \n" +
                " PREFIX tddo: <http://databugger.aksw.org/ontology#> \n" +
                " PREFIX tddg: <http://databugger.aksw.org/generators#> \n"
                ;
    }

    public static DatasetSource getDatosBneEsDataset() {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        List<SchemaSource> sources = SchemaService.getSourceList(
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "frbrer", "isbd", "dcterms", "skos"));

        // TODO add endpoint / graph
        DatasetSource dataset = new DatasetSource("datos.bne.es", "http://datos.bne.es", "http://localhost:8890/sparql", "http://datos.bne.es", sources);

        return dataset;
    }

    public static DatasetSource getLCSHDataset() {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        List<SchemaSource> sources = SchemaService.getSourceList(
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "foaf", "dcterms", "skos"));

        // TODO add endpoint / graph
        DatasetSource dataset = new DatasetSource("id.loc.gov", "http://id.loc.gov", "-", "-", sources);

        return dataset;
    }

    public static DatasetSource getDBpediaENDataset() {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        List<SchemaSource> sources = SchemaService.getSourceList(
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        //Enriched Schema (cached in folder)
        sources.add(new EnrichedSchemaSource("dbo", "http://dbpedia.org"));

        DatasetSource dataset = new DatasetSource("dbpedia.org", "http://dbpedia.org", "http://dbpedia.org/sparql", "http://dbpedia.org", sources);


        return dataset;
    }

    public static DatasetSource getDBpediaNLDataset() {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        List<SchemaSource> sources = SchemaService.getSourceList(
            Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        //Enriched Schema (cached in folder)
        sources.add(new EnrichedSchemaSource("dbo", "http://nl.dbpedia.org"));

        DatasetSource dataset = new DatasetSource("nl.dbpedia.org", "http://nl.dbpedia.org", "http://nl.dbpedia.org/sparql", "http://nl.dbpedia.org", sources);


        return dataset;
    }

    public static void fillSchemaService(){
        // Manual
        SchemaService.addSchemaDecl("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        SchemaService.addSchemaDecl("owl", "http://www.w3.org/2002/07/owl#");
        SchemaService.addSchemaDecl("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        SchemaService.addSchemaDecl("dbo", "http://dbpedia.org/ontology/\thttp://mappings.dbpedia.org/server/ontology/dbpedia.owl");
        SchemaService.addSchemaDecl("foaf", "http://xmlns.com/foaf/0.1/");
        SchemaService.addSchemaDecl("dcterms", "http://purl.org/dc/terms/");
        SchemaService.addSchemaDecl("dc", "http://purl.org/dc/elements/1.1/");
        SchemaService.addSchemaDecl("skos", "http://www.w3.org/2004/02/skos/core#");
        SchemaService.addSchemaDecl("georss", "http://www.georss.org/georss/");
        SchemaService.addSchemaDecl("geo", "http://www.w3.org/2003/01/geo/wgs84_pos");
        SchemaService.addSchemaDecl("prov", "http://www.w3.org/ns/prov");
        SchemaService.addSchemaDecl("frbrer", "http://iflastandards.info/ns/fr/frbr/frbrer/");
        SchemaService.addSchemaDecl("isbd", "http://iflastandards.info/ns/isbd/elements/");
        SchemaService.addSchemaDecl("lgdo", "http://linkedgeodata.org/ontology\thttp://downloads.linkedgeodata.org/experimental/2013-06-26-lgd-ontology.nt");

        // Add from LOV
        //fillSchemasFromLOV()
    }

    public static void fillSchemasFromLOV(){
        List<Source> sources = new ArrayList<Source>();
        Source lov = new DatasetSource("lov", "http://lov.okfn.org", "http://lov.okfn.org/endpoint/lov","", null);

        // Example from http://lov.okfn.org/endpoint/lov
        QueryExecution qe = lov.getExecutionFactory().createQueryExecution(
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
                "SELECT ?vocabPrefix ?vocabURI \n" +
                "WHERE{\n" +
                "\t?vocabURI a voaf:Vocabulary.\n" +
                "\t?vocabURI vann:preferredNamespacePrefix ?vocabPrefix.\n" +
                "} \n" +
                "ORDER BY ?vocabPrefix ");

        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution row = rs.next();

            String prefix = row.get("vocabPrefix").toString();
            String vocab = row.get("vocabURI").toString();
            SchemaService.addSchemaDecl(prefix, vocab);
        }
    }
}
