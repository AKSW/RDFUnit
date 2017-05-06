package org.aksw.rdfunit.model.impl.shacl;

import lombok.*;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Resource;

import java.util.List;
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
    private final String shaclPath;
    @Getter @NonNull @Singular private final List<ShapeTarget> targets;
    @Getter @NonNull private final PropertyValuePairSet propertyValuePairSets;

    @Override
    public Optional<String> getPath() {
        return Optional.ofNullable(shaclPath);
    }

}
