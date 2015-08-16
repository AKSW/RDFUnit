package org.aksw.rdfunit.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Core RDFUnit Vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 */
public final class RLOG {

    //namespace
    public static final String namespace = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#";

    //Classes
    public static final Resource Entry = resource("Entry");



    //properties
    public static final Property level = property("level");
    public static final Property resource = property("resource");
    public static final Property message = property("message");





    private RLOG() {

    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
