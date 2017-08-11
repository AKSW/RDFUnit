package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.Validator;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

@ToString
@EqualsAndHashCode(exclude={"element"})
@Builder
public class SparqlValidatorImpl implements Validator {
    @Getter @NonNull private final Resource element;
    @Getter private final Literal message;
    @Getter @NonNull private final String sparqlQuery;
    @Getter @NonNull @Singular private final ImmutableSet<PrefixDeclaration> prefixDeclarations;

    @Override
    public Optional<Literal> getDefaultMessage() {
        return Optional.ofNullable(message);
    }
}
