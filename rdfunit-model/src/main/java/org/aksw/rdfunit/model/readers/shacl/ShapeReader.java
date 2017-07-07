package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.shacl.ShapeImpl;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ShapeReader implements ElementReader<Shape> {

    private ShapeReader() {}

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
            .propertyValuePairSets(PropertyValuePairSet.createFromResource(resource));

        Resource path = resource.getPropertyResourceValue(SHACL.path);
        if (path != null) {
            shapeBuilder.shaclPath(path.getURI());
        }


        return shapeBuilder.build();


    }
}

