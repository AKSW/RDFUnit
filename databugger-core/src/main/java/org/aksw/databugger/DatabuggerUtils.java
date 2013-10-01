package org.aksw.databugger;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.Source;

import java.util.ArrayList;
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

    public static List<Source> getSourcesFromLOV(){
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
            sources.add(new SchemaSource(prefix, vocab));
        }
        return sources;
    }
}
