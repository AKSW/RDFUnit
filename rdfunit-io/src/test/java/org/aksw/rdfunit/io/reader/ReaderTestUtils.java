package org.aksw.rdfunit.io.reader;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/14/15 11:56 AM
 */
public final class ReaderTestUtils {

    public static Model createOneTripleModel() {
        Model model = ModelFactory.createDefaultModel();
        model.add(
                ResourceFactory.createResource("http://rdfunit.aksw.org"),
                OWL.sameAs,
                ResourceFactory.createResource("http://dbpedia.org/resource/Cool"));
        return model;
    }
}
