package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Interface for writing elements back to RDF
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:55 PM
 * @version $Id: $Id
 */
public interface ElementWriter {

    /**
     * <p>write.</p>
     *
     * @return a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     */
    Resource write(Model model);
}
