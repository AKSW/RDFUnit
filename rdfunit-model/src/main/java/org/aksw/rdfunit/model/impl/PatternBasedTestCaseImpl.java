package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.*;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Dimitris Kontokostas
 * @since 1/3/14 3:49 PM
 */
@ToString
@EqualsAndHashCode(of = {"pattern", "bindings", "testCaseAnnotation"})
public class PatternBasedTestCaseImpl implements TestCase, PatternBasedTestCase {

    @Getter @NonNull private final Pattern pattern;
    @NonNull private final ImmutableSet<Binding> bindings;

    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final TestCaseAnnotation testCaseAnnotation;

    @Getter @NonNull private final String sparqlWhere;
    @Getter @NonNull private final String sparqlPrevalence;

    public PatternBasedTestCaseImpl(@NonNull Resource resource, @NonNull TestCaseAnnotation annotation, @NonNull Pattern pattern, @NonNull Collection<Binding> bindings) {
        this.element = resource;
        this.testCaseAnnotation = annotation;
        this.pattern = pattern;
        this.bindings = ImmutableSet.copyOf(bindings);

        this.sparqlWhere = initSparqlWhere();
        this.sparqlPrevalence = initSparqlPrevalence();
        // validate
        if (bindings.size() != pattern.getParameters().size()) {
            // throw new TestCaseInstantiationException("Non valid bindings in TestCase: " + testURI);
        }
    }

    public String getAutoGeneratorURI() {
        return testCaseAnnotation.getAutoGeneratorURI();
    }

    @Override
    public Collection<Binding> getBindings() {
        return bindings;
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return Collections.emptyList();
    }

    private String initSparqlWhere() {
        return instantiateBindings(bindings, pattern.getSparqlWherePattern()).trim();

    }

    private String initSparqlPrevalence() {

        if (pattern.getSparqlPatternPrevalence().isPresent()) {
            return instantiateBindings(bindings, pattern.getSparqlPatternPrevalence().get()).trim();
        } else {
            return "";
        }

    }


    private static String instantiateBindings(Collection<Binding> bindings, String query) {
        String sparql = query;
        for (Binding b : bindings) {
            sparql = sparql.replace("%%" + b.getParameterId() + "%%", b.getValueAsString());
        }
        return sparql;
    }


}
