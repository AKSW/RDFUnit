package org.aksw.rdfunit.model.impl;

import com.google.common.base.Optional;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.services.PrefixNSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * <p>TestGenerator class.</p>
 *
 * @author Dimitris Kontokostas
 *         Takes a sparqlPattern and a SPARQL query and based on the data
 *         returned from that query we generate test cases
 * @since 9/20/13 2:48 PM
 * @version $Id: $Id
 */
public final class TestGeneratorImpl implements TestGenerator {
    private static final Logger log = LoggerFactory.getLogger(TestGeneratorImpl.class);

    private final Resource element;
    private final String description;
    private final String query;
    private final Pattern pattern;
    private final Collection<ResultAnnotation> generatorAnnotations;


    /**
     * <p>Constructor for TestGenerator.</p>
     *
     * @param element
     * @param description a {@link java.lang.String} object.
     * @param query a {@link java.lang.String} object.
     * @param pattern a {@link Pattern} object.
     * @param generatorAnnotations a {@link java.util.Collection} object.
     */
    private TestGeneratorImpl(Resource element, String description, String query, Pattern pattern, Collection<ResultAnnotation> generatorAnnotations) {

        this.element = checkNotNull(element);
        String tagName = element.getLocalName();

        this.description = checkNotNull(description, "Description in %s should not be null", tagName);
        this.query = checkNotNull(query, "Query in %s should not be null", tagName);
        checkState(!query.trim().isEmpty(), "Query in %s should not be empty", tagName);
        this.pattern = checkNotNull(pattern, "Pattern in %s should not be null", tagName);
        this.generatorAnnotations = checkNotNull(generatorAnnotations);
    }

    /**
     * <p>createTAG.</p>
     *
     * @param element a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param description a {@link java.lang.String} object.
     * @param query a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @param generatorAnnotations a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.model.interfaces.TestGenerator} object.
     */
    public static TestGenerator createTAG(Resource element, String description, String query, Pattern pattern, Collection<ResultAnnotation> generatorAnnotations) {
        return new TestGeneratorImpl(element,description,query,pattern,generatorAnnotations);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(element);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        Query q;
        if (pattern == null) {
            log.error("{} : Pattern {} does not exist", getTAGUri(), getTAGPattern());
            return false;
        }
        try {
            q = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getTAGQuery());
        } catch (Exception e) {
            log.error("{} Cannot parse query:\n{}", getTAGUri(), PrefixNSService.getSparqlPrefixDecl() + getTAGQuery(), e);
            return false;
        }

        Collection<Var> sv = q.getProjectVars();
        if (sv.size() != pattern.getParameters().size() + 1) {
            log.error("{} Select variables are different than Pattern parameters", getTAGUri());
            return false;
        }


        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getTAGUri() {
        return element.getURI();
    }

    /** {@inheritDoc} */
    @Override
    public String getTAGDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public String getTAGQuery() {
        return query;
    }

    /** {@inheritDoc} */
    @Override
    public Pattern getTAGPattern() {
        return pattern;
    }

    @Override
    public Collection<ResultAnnotation> getTAGAnnotations() {
        return generatorAnnotations;
    }

}
