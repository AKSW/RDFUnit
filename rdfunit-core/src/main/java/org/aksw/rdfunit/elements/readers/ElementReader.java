package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Interface for reading SHACL / RDFUnit elements
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:03 PM
 */
public interface ElementReader <T> {

    T read(Resource resource);
}
