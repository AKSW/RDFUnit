package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:06 PM
 */
public class ShapeScopeClass implements ShapeScope {
    private final Resource resource;
    private final String clazz;

    public ShapeScopeClass(Resource resource, String clazz) {
        this.resource = resource;
        this.clazz = clazz;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public String getScopeClass() {
        return clazz;
    }
}
