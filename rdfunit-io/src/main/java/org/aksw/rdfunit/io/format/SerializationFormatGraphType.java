package org.aksw.rdfunit.io.format;

/**
 * Defines different types of RDF datasets : single or multi graph (dataset)
 *
 * @author Dimitris Kontokostas
 * @since 6/18/14 6:50 PM
 * @version $Id: $Id
 */
public enum SerializationFormatGraphType {
    /**
     * single graph only
     */
    singleGraph,

    /**
     * RDFdataset / possible multiple graphs
     */
    dataset

}
