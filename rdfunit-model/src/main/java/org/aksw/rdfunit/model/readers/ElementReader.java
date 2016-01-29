package org.aksw.rdfunit.model.readers;

import org.apache.jena.rdf.model.Resource;

/**
 * Interface for reading SHACL / RDFUnit elements
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:03 PM
 * @version $Id: $Id
 */
public interface ElementReader <T> {

    /**
     * <p>read.</p>
     *
     * @param resource a {@link org.apache.jena.rdf.model.Resource} object.
     * @return a T object.
     */
    T read(Resource resource);
}
