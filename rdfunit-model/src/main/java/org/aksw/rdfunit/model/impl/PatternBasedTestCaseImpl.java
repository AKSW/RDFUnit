package org.aksw.rdfunit.model.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.*;
import org.apache.jena.ext.com.google.common.collect.ImmutableSet;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;

/**
 * <p>PatternBasedTestCase class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @version $Id: $Id
 * @since 1/3/14 3:49 PM
 */
@ToString
@EqualsAndHashCode
public class PatternBasedTestCaseImpl implements TestCase, PatternBasedTestCase {

    @Getter @NonNull private final Pattern pattern;
    @NonNull private final ImmutableSet<Binding> bindings;

    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final TestCaseAnnotation testCaseAnnotation;

    @Getter(lazy = true) @NonNull private final String sparqlWhere = initSparqlWhere();
    @Getter(lazy = true) @NonNull private final String sparqlPrevalence = initSparqlPrevalence();

    /**
     * <p>Constructor for PatternBasedTestCaseImpl.</p>
     *
     * @param resource   a {@link org.apache.jena.rdf.model.Resource} object.
     * @param annotation a {@link org.aksw.rdfunit.model.interfaces.TestCaseAnnotation} object.
     * @param pattern    a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @param bindings   a {@link java.util.Collection} object.
     */
    public PatternBasedTestCaseImpl(@NonNull Resource resource, @NonNull TestCaseAnnotation annotation, @NonNull Pattern pattern, @NonNull Collection<Binding> bindings) {
        this.element = resource;
        this.testCaseAnnotation = annotation;
        this.pattern = pattern;
        this.bindings = ImmutableSet.copyOf(bindings);

        // validate
        if (bindings.size() != pattern.getParameters().size()) {
            // throw new TestCaseInstantiationException("Non valid bindings in TestCase: " + testURI);
        }
    }

    public String getAutoGeneratorURI() {
        return testCaseAnnotation.getAutoGeneratorURI();
    }

    public Collection<Binding> getBindings() {
        return bindings;
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


    private String instantiateBindings(Collection<Binding> bindings, String query) {
        String sparql = query;
        for (Binding b : bindings) {
            sparql = sparql.replace("%%" + b.getParameterId() + "%%", b.getValueAsString());
        }
        return sparql;
    }


}
