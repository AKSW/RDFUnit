package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.model.impl.shacl.ShapePathImpl;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.P_Link;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ShapePathReader implements ElementReader<ShapePath> {

    private ShapePathReader() {}

    public static ShapePathReader create() {
        return new ShapePathReader();
    }

    /** {@inheritDoc} */
    @Override
    public ShapePath read(Resource resource) {
        checkNotNull(resource);

        ShapePathImpl.ShapePathImplBuilder shapePathBuilder = ShapePathImpl.builder();

        shapePathBuilder.element(resource);

        if (resource.isResource()) {
            shapePathBuilder.jenaPath(new P_Link(resource.asNode()));
        } else {
            throw new IllegalArgumentException("Complax paths not yet supported");
        }

        return shapePathBuilder.build();
    }
}

