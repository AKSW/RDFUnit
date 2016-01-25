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
    private final Resource element;
    private final String clazz;

    /**
     * <p>Constructor for ShapeScopeClass.</p>
     *
     * @param element a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param clazz a {@link java.lang.String} object.
     */
    public ShapeScopeClass(Resource element, String clazz) {
        this.element = element;
        this.clazz = clazz;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }

    /** {@inheritDoc} */
    @Override
    public String getScopeClass() {
        return clazz;
    }
}
