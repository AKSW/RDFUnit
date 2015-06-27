package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Interface for writing elements back to RDF
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:55 PM
 */
public interface ElementWriter {

    Resource write();
}
