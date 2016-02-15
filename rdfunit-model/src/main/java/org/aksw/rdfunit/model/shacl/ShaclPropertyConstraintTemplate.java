package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.aksw.rdfunit.model.interfaces.Argument;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 11:20 μμ
 */
@Builder
public class ShaclPropertyConstraintTemplate {
    @NonNull @Getter private final String sparqlSnippet;
    @NonNull @Getter @Singular private final ImmutableSet<Argument> arguments;
    @NonNull @Getter private final String message;
}
