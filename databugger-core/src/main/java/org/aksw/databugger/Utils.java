package org.aksw.databugger;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/24/13 11:25 AM
 */
public class Utils {
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
}
