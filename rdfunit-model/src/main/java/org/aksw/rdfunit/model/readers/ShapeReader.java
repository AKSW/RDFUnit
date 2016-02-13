package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.ShapeImpl;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.shacl.TemplateRegistry;
import org.apache.jena.rdf.model.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ShapeReader implements ElementReader<Shape> {

    private final TemplateRegistry templateRegistry;

    private ShapeReader(TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
    }

    public static ShapeReader create(TemplateRegistry templateRegistry) {
        return new ShapeReader(templateRegistry);
    }

    /** {@inheritDoc} */
    @Override
    public Shape read(Resource resource) {
        checkNotNull(resource);

        ShapeImpl.ShapeImplBuilder shapeBuilder = ShapeImpl.builder();

        shapeBuilder
            .element(resource)
            .scopes(
                BatchShapeScopeReader.create().read(resource))
            .propertyConstraintGroups(
                    BatchShapePropertyGroupReader.create(templateRegistry).readShapePropertyGroups(resource)
            );

        return shapeBuilder.build();


    }
}

