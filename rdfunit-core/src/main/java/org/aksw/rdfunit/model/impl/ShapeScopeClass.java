package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:06 PM
 * @version $Id: $Id
 */
public class ShapeScopeClass implements ShapeScope {
    private final Resource resource;
    private final String clazz;

    /**
     * <p>Constructor for ShapeScopeClass.</p>
     *
     * @param resource a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param clazz a {@link java.lang.String} object.
     */
    public ShapeScopeClass(Resource resource, String clazz) {
        this.resource = resource;
        this.clazz = clazz;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getResource() {
        return resource;
    }

    /** {@inheritDoc} */
    @Override
    public String getScopeClass() {
        return clazz;
    }
}
