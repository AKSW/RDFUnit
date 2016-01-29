package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * PROV properties and classes used in RDFUnit
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 * @version $Id: $Id
 */
public final class PROV {

    //namespace
    public static final String namespace = "http://www.w3.org/ns/prov#";

    //Classes
    public static final Resource Activity = resource("Activity");
    public static final Resource Collection = resource("Collection");




    //properties
    public static final Property wasGeneratedBy = property("wasGeneratedBy");
    public static final Property used = property("used");
    public static final Property startedAtTime = property("startedAtTime");
    public static final Property endedAtTime = property("endedAtTime");
    public static final Property wasStartedBy = property("wasStartedBy");
    public static final Property hadMember = property("hadMember");
    public static final Property wasAssociatedWith = property("wasAssociatedWith");





    private PROV() {

    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
