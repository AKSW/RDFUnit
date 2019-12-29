package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import lombok.*;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.apache.jena.rdf.model.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

@Builder
@Value
@EqualsAndHashCode()
public class ShapeList implements Shape {

    @Getter @NonNull private final ImmutableList<Shape> shapes;
    
    @Getter @NonNull private final Resource element;
    private final ShapePath shaclPath;
    @Getter @NonNull private final PropertyValuePairSet propertyValuePairSets;

    @Override
    public Optional<ShapePath> getPath() {
        return Optional.ofNullable(shaclPath);
    }
}
