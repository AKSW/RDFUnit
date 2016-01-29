package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Core RDFUnit Vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 * @version $Id: $Id
 */
public final class RLOG {

    //namespace
    /** Constant <code>namespace="http://persistence.uni-leipzig.org/nlp2"{trunked}</code> */
    public static final String namespace = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#";

    //Classes
    /** Constant <code>Entry</code> */
    public static final Resource Entry = resource("Entry");



    //properties
    /** Constant <code>level</code> */
    public static final Property level = property("level");
    /** Constant <code>resource</code> */
    public static final Property resource = property("resource");
    /** Constant <code>message</code> */
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
