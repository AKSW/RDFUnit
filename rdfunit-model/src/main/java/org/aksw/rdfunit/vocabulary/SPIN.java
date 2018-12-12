package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * The SPIN vocabulary
 */
public final class SPIN {

    //namespace
    /** Constant <code>namespace="http://spinrdf.org/spin#"</code> */
    public static final String namespace = "http://spinrdf.org/spin#";

    //classes
    // not yet needed

    //properties
    public static final Property violationValue = property("violationValue");
    public static final Property violationRoot = property("violationRoot");
    public static final Property violationPath = property("violationPath");
    public static final Property violationLevel = property("violationLevel");
    public static final Property violationSource = property("violationSource");
    public static final Property fix = property("fix");

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
