package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Core SHACL Vocabulary
 *
 * @author Markus Ackermann
 * @since 7/14/17 4:17 PM
 * @version $Id: $Id
 */
@SuppressWarnings("ClassWithTooManyFields")
public final class SHACL_TEST {

    //namespace
    /** Constant <code>namespace="http://www.w3.org/ns/shacl-test#"</code> */
    public static final String namespace = "http://www.w3.org/ns/shacl-test#";

//    //classes
//    public static final Resource Manifest = resource("Manifest");

    //individuals
    public static final Resource approved = resource("approved");

    //properties
    public static final Property dataGraph = property("dataGraph");
    public static final Property shapesGraph = property("shapesGraph");

    private SHACL_TEST() {
    }

    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
