package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

/**
 * A simple Shape implementation
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:07 PM
 * @version $Id: $Id
 */
@Builder
@Value
public class ShapeImpl implements Shape {

    @Getter @NonNull private final Resource element;
    private final ShapePath shaclPath;
    @Getter @NonNull private final PropertyValuePairSet propertyValuePairSets;

    @Override
    public Optional<ShapePath> getPath() {
        return Optional.ofNullable(shaclPath);
    }

}
