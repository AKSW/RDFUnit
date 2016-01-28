package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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
@ToString
@EqualsAndHashCode
public final class TestGeneratorImpl implements TestGenerator {
    private static final Logger log = LoggerFactory.getLogger(TestGeneratorImpl.class);

    @Getter private final Resource element;
    @Getter private final String description;
    @Getter private final String query;
    @Getter private final Pattern pattern;
    @Getter private final Collection<ResultAnnotation> annotations;



    @Builder
    private TestGeneratorImpl(Resource element, String description, String query, Pattern pattern, Collection<ResultAnnotation> generatorAnnotations) {

        this.element = checkNotNull(element);
        String tagName = element.getLocalName();

        this.description = checkNotNull(description, "Description in %s should not be null", tagName);
        this.query = checkNotNull(query, "Query in %s should not be null", tagName);
        checkState(!query.trim().isEmpty(), "Query in %s should not be empty", tagName);
        this.pattern = checkNotNull(pattern, "Pattern in %s should not be null", tagName);
        this.annotations = checkNotNull(generatorAnnotations);
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

    @Override
    public String getUri() {
        return element.getURI();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        Query q;
        if (pattern == null) {
            log.error("{} : Pattern {} does not exist", getUri(), getPattern());
            return false;
        }
        try {
            q = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getQuery());
        } catch (Exception e) {
            log.error("{} Cannot parse query:\n{}", getUri(), PrefixNSService.getSparqlPrefixDecl() + getQuery(), e);
            return false;
        }

        Collection<Var> sv = q.getProjectVars();
        if (sv.size() != pattern.getParameters().size() + 1) {
            log.error("{} Select variables are different than Pattern parameters", getUri());
            return false;
        }


        return true;
    }



}
