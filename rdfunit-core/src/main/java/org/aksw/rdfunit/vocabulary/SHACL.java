package org.aksw.rdfunit.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Core SHACL Vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 * @version $Id: $Id
 */
public final class SHACL {

    //namespace
    /** Constant <code>namespace="http://www.w3.org/ns/shacl#"</code> */
    public static final String namespace = "http://www.w3.org/ns/shacl#";

    //Classes
    /** Constant <code>Argument</code> */
    public static final Resource Argument = resource("Argument");
    /** Constant <code>Function</code> */
    public static final Resource Function = resource("Function");
    /** Constant <code>ResultAnnotation</code> */
    public static final Resource ResultAnnotation = resource("ResultAnnotation");


    //properties
    /** Constant <code>argument</code> */
    public static final Property argument = property("argument");
    /** Constant <code>cachable</code> */
    public static final Property cachable = property("cachable");
    /** Constant <code>datatype</code> */
    public static final Property datatype = property("datatype");
    /** Constant <code>defaultValue</code> */
    public static final Property defaultValue = property("defaultValue");
    /** Constant <code>optional</code> */
    public static final Property optional = property("optional");
    /** Constant <code>predicate</code> */
    public static final Property predicate = property("predicate");
    /** Constant <code>returnType</code> */
    public static final Property returnType = property("returnType");
    /** Constant <code>sparql</code> */
    public static final Property sparql = property("sparql");
    /** Constant <code>valueType</code> */
    public static final Property valueType = property("valueType");

    /** Constant <code>clazz</code> */
    public static final Property clazz = property("class");



    /** Constant <code>annotationProperty</code> */
    public static final Property annotationProperty = property("annotationProperty");
    /** Constant <code>annotationVarName</code> */
    public static final Property annotationVarName = property("annotationVarName");
    /** Constant <code>annotationValue</code> */
    public static final Property annotationValue = property("annotationValue");
    //public static final Property cachable = property("cachable");
    //public static final Property cachable = property("cachable");
    //public static final Property cachable = property("cachable");




    private SHACL() {
    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
        /*
        public static final class Nodes {
            public static final Node Alt;
            public static final Node Bag;
            public static final Node Property;
            public static final Node Seq;
            public static final Node Statement;
            public static final Node List;
            public static final Node nil;
            public static final Node first;
            public static final Node rest;
            public static final Node subject;
            public static final Node predicate;
            public static final Node object;
            public static final Node type;
            public static final Node value;

            public Nodes() {
            }

            static {
                Alt = RDF.Alt.asNode();
                Bag = RDF.Bag.asNode();
                Property = RDF.Property.asNode();
                Seq = RDF.Seq.asNode();
                Statement = RDF.Statement.asNode();
                List = RDF.List.asNode();
                nil = RDF.nil.asNode();
                first = RDF.first.asNode();
                rest = RDF.rest.asNode();
                subject = RDF.subject.asNode();
                predicate = RDF.predicate.asNode();
                object = RDF.object.asNode();
                type = RDF.type.asNode();
                value = RDF.value.asNode();
            }
        } */


}
