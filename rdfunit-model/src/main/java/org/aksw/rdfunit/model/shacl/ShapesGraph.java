package org.aksw.rdfunit.model.shacl;


import com.google.common.collect.ImmutableList;
import lombok.*;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;

@Builder
@Value
public class ShapesGraph {
    @Getter @NonNull @Singular private final ImmutableList<Shape> shapes;
    //@Getter @NonNull @Singular private final ImmutableList<ShapeTarget> targets;
    @Getter @NonNull @Singular private final ImmutableList<Component> components;
}
