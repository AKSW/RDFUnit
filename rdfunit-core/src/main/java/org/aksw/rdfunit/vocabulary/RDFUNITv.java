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
public final class RDFUNITv {

    //namespace
    public static final String namespace = "http://rdfunit.aksw.org/ns/core#";

    //Classes
    public static final Resource Binding = resource("Binding");

    //properties
    public static final Resource binding = property("binding");
    public static final Resource bindingValue = property("bindingValue");
    public static final Resource parameter = property("parameter");



    private RDFUNITv() {

    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
