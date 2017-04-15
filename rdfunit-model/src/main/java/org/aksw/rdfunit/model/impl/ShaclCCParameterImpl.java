package org.aksw.rdfunit.model.impl;

import lombok.*;
import org.aksw.rdfunit.model.interfaces.ShaclCCParameter;
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
public final class ShaclCCParameterImpl implements ShaclCCParameter {

    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final Property predicate;
    @Getter private final boolean isOptional;
    private final RDFNode defaultValue;


    @Override
    public Optional<RDFNode> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }


}
