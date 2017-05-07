package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import lombok.*;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.apache.jena.rdf.model.Resource;

@ToString
@EqualsAndHashCode(exclude={"element"})
@Builder
public class ComponentImpl implements Component{
    @Getter @NonNull private final Resource element;
    @Getter @NonNull @Singular private ImmutableList<ComponentParameter> parameters;
    @Getter @NonNull @Singular private ImmutableList<ComponentValidator> validators;

}
