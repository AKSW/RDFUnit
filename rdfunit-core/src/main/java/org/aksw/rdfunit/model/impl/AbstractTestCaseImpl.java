package org.aksw.rdfunit.model.impl;

import com.google.common.base.Optional;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.services.PrefixNSService;

import java.util.Collection;

/**
 * <p>Abstract TestCase class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/23/13 6:31 AM
 * @version $Id: $Id
 */
public abstract class AbstractTestCaseImpl implements TestCase, Comparable<AbstractTestCaseImpl> {

    protected final Resource tcResource;
    protected final TestCaseAnnotation annotation;

    /**
     * <p>Constructor for AbstractTestCaseImpl.</p>
     *
     * @param tcResource a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param annotation a {@link org.aksw.rdfunit.model.interfaces.TestCaseAnnotation} object.
     */
    public AbstractTestCaseImpl(Resource tcResource, TestCaseAnnotation annotation) {
        this.tcResource = tcResource;
        this.annotation = annotation;
        // Validate on subclasses
    }

    /**
     * <p>getSparqlWhere.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getSparqlWhere();

    /**
     * <p>getSparqlPrevalence.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getSparqlPrevalence();

    /**
     * <p>getResultMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getResultMessage() {
        return annotation.getDescription();
    }

    /**
     * <p>getLogLevel.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     */
    public RLOGLevel getLogLevel() {
        return annotation.getTestCaseLogLevel();
    }

    /**
     * <p>getResultAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ResultAnnotation> getResultAnnotations() {
        return annotation.getResultAnnotations();
    }

    /**
     * <p>getVariableAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ResultAnnotation> getVariableAnnotations() {
        return annotation.getVariableAnnotations();
    }

    /**
     * <p>getSparqlPrevalenceQuery.</p>
     *
     * @return a {@link com.hp.hpl.jena.query.Query} object.
     */
    public Query getSparqlPrevalenceQuery() {
        if (getSparqlPrevalence().trim().isEmpty())
            return null;
        return QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getSparqlPrevalence());
    }

    /**
     * <p>Getter for the field <code>testURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTestURI() {
        return tcResource.getURI();
    }

    /**
     * <p>getAbrTestURI.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAbrTestURI() {
        return getTestURI().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:");
    }



    /** {@inheritDoc} */
    @Override
    public int compareTo(AbstractTestCaseImpl o) {
        if (o == null) {
            return -1;
        }

        return this.getTestURI().compareTo(o.getTestURI());
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTestCaseImpl)) return false;

        AbstractTestCaseImpl testCase = (AbstractTestCaseImpl) o;

        return tcResource.equals(testCase.tcResource);

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return tcResource.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.getTestURI();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(tcResource);
    }

    /** {@inheritDoc} */
    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        return annotation;
    }
}
