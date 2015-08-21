package org.aksw.rdfunit.elements.interfaces;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Resource;

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
    Optional<Resource> getResource();
}
