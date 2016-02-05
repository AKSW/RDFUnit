package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.ShapeImpl;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.apache.jena.rdf.model.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ShapeReader implements ElementReader<Shape> {

    private ShapeReader() {
    }

    public static ShapeReader create() {
        return new ShapeReader();
    }

    /** {@inheritDoc} */
    @Override
    public Shape read(Resource resource) {
        checkNotNull(resource);

        ShapeImpl.ShapeImplBuilder shapeBuilder = ShapeImpl.builder();

        shapeBuilder
            .element(resource)
            .scopes(
                BatchShapeScopeReader.create().read(resource));

        return shapeBuilder.build();


    }
}

