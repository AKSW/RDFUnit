package org.aksw.rdfunit.model.shacl;

import lombok.*;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PatternParameter;

import java.util.Map;
import java.util.Set;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 11/2/2016 10:05 πμ
 */
@Value
@Builder
public class ShaclPattern {
    @Getter @NonNull private final Pattern pattern;
    @Getter @NonNull @Singular private Map<Argument, PatternParameter> parameters;
    @Getter @NonNull private final Argument mainArgument;
    //@Getter @NonNull @Singular private Set<Binding> bindings;

    public Set<Argument> getArguments() {
        return parameters.keySet();
    }
}
