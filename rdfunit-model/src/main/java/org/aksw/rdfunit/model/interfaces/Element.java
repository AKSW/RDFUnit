package org.aksw.rdfunit.model.interfaces;

import org.apache.jena.rdf.model.Resource;

/**
 * Defines a language Element
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 4:19 PM
 * @version $Id: $Id
 */
public interface Element {

    /**
     * Gets the resource associated with this element..
     *
     * @return the resource associated with this element
     */
    Resource getElement();
}
