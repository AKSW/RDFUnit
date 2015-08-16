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
    public static final Resource ResultAnnotation = resource("ResultAnnotation");
    public static final Resource RLOGTestCaseResult = resource("RLOGTestCaseResult");


    //properties
    public static final Property binding = property("binding");
    public static final Property bindingValue = property("bindingValue");
    public static final Property parameter = property("parameter");
    public static final Property annotationProperty = property("annotationProperty");
    public static final Property annotationValue = property("annotationValue");
    public static final Property resultAnnotation = property("resultAnnotation");




    private RDFUNITv() {

    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
