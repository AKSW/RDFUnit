package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.model.interfaces.PropertyConstraint;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 11/2/2016 11:49 πμ
 */
@Value
@Builder
public class ShaclBindingPattern implements PropertyConstraint{
    @Getter @NonNull private final ShaclPattern pattern;
    @Getter @NonNull @Singular private final ImmutableSet<Binding> bindings;
    //@Getter @NonNull private final PropertyConstraintGroup propertyConstraintGroup;

    @Override
    public Property getFacetProperty() {
        return pattern.getMainArgument().getPredicate();
    }

    @Override
    public Set<RDFNode> getFacetValues() {
        PatternParameter mainParameter = pattern.getParameters().get(pattern.getMainArgument());

        return bindings.stream()
                .filter( b -> b.getParameter().equals(mainParameter))
                .map(Binding::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyValuePair> getAdditionalArguments() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public TestCase getTestCase() {
        return new PatternBasedTestCaseImpl(null, null, pattern.getPattern(), bindings);
    }
}
