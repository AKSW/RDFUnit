package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.*;

import java.util.Collection;
import java.util.Collections;

/**
 * <p>PatternBasedTestCase class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:49 PM
 * @version $Id: $Id
 */
public class PatternBasedTestCaseImpl extends AbstractTestCaseImpl implements TestCase, PatternBasedTestCase {

    private final Pattern pattern;
    private final Collection<Binding> bindings;
    private String sparqlWhereCache = null;
    private String sparqlPrevalenceCache = null;

    /**
     * <p>Constructor for PatternBasedTestCaseImpl.</p>
     *
     * @param tcResource a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param annotation a {@link org.aksw.rdfunit.model.interfaces.TestCaseAnnotation} object.
     * @param pattern a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @param bindings a {@link java.util.Collection} object.
     */
    public PatternBasedTestCaseImpl(Resource tcResource, TestCaseAnnotation annotation, Pattern pattern, Collection<Binding> bindings) {
        super(tcResource, annotation);
        this.pattern = pattern;
        this.bindings = bindings;

        // validate
        if (bindings.size() != pattern.getParameters().size()) {
           // throw new TestCaseInstantiationException("Non valid bindings in TestCase: " + testURI);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlWhere() {
        if (sparqlWhereCache == null) {
            sparqlWhereCache = instantiateBindings(bindings, pattern.getSparqlWherePattern()).trim();
        }
        return sparqlWhereCache;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlPrevalence() {
        if (sparqlPrevalenceCache == null ) {
            if (pattern.getSparqlPatternPrevalence().isPresent()) {
                sparqlPrevalenceCache = instantiateBindings(bindings, pattern.getSparqlPatternPrevalence().get()).trim();
            }
            else {
                sparqlPrevalenceCache = "";
            }
        }
        return sparqlPrevalenceCache;
    }

    /**
     * <p>getAutoGeneratorURI.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 0.7.19
     */
    public String getAutoGeneratorURI() {
        return annotation.getAutoGeneratorURI();
    }

    private String instantiateBindings(Collection<Binding> bindings, String query) {
        String sparql = query;
        for (Binding b : bindings) {
            sparql = sparql.replace("%%" + b.getParameterId() + "%%", b.getValueAsString());
        }
        return sparql;
    }

    /**
     * <p>Getter for the field <code>pattern</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * <p>Getter for the field <code>bindings</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Binding> getBindings() {
        return Collections.unmodifiableCollection(bindings);
    }
}
