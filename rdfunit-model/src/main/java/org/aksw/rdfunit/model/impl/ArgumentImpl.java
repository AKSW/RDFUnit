package org.aksw.rdfunit.model.impl;

import lombok.*;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:04 PM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode(exclude={"element"})
@Builder
public final class ArgumentImpl implements Argument {

    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final Property predicate;
    @Getter @NonNull private final String comment;
    @Getter private final boolean isOptional;
    private final Resource valueType;
    private final ValueKind valueKind;
    private final RDFNode defaultValue;

    @Override
    public Optional<Resource> getValueType() {
        return Optional.ofNullable(valueType);
    }

    @Override
    public Optional<ValueKind> getValueKind() {
        return Optional.ofNullable(valueKind);
    }

    @Override
    public Optional<RDFNode> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }


}
