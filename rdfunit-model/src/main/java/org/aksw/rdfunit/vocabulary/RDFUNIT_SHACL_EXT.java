package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;


public final class RDFUNIT_SHACL_EXT {

    //namespace
    /** Constant <code>namespace="http://rdfunit.aksw.org/ns/core#"</code> */
    public static final String namespace = "http://rdfunit.aksw.org/ns/shacl-ext#";

    //Classes
    //public static final Resource Binding = resource("Binding");

    //properties
    public static final Property filter = property("filter");


    private RDFUNIT_SHACL_EXT() {

    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
